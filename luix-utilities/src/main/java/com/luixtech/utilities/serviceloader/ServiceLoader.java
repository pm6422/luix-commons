package com.luixtech.utilities.serviceloader;

import com.luixtech.utilities.lang.CharacterUtils;
import com.luixtech.utilities.serviceloader.annotation.Spi;
import com.luixtech.utilities.serviceloader.annotation.SpiName;
import com.luixtech.utilities.serviceloader.annotation.SpiScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.annotation.concurrent.ThreadSafe;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility used to load a specified implementation of a service interface.
 * It carries out similar functions as {@link java.util.ServiceLoader}
 * Service providers can be installed in an implementation of the Java platform in the form of
 * jar files placed into any of the usual extension directories. Providers can also be made available by adding them to the
 * application's class path or by some other platform-specific means.
 * <p>
 * Requirements:
 * _ The service provider interface must be an interface class, not a concrete or abstract class
 * _ The service provider implementation class must have a zero-argument constructor so that they can be instantiated during loading
 * - The service provider is identified by placing a configuration file in the resource directory META-INF/services/
 * - The configuration file must be encoded in UTF-8
 * - The configuration file's name is the fully-qualified name of service provider interface
 * - The configuration file's contents are the fully-qualified name of service provider implementation class
 *
 * @param <T> Service interface type
 */
@Slf4j
@ThreadSafe
public class ServiceLoader<T> {
    /**
     * Service directory prefix
     */
    private static final String                        SERVICE_DIR_PREFIX          = "META-INF/services/";
    /**
     * Charset of the service configuration file
     */
    public static final  Charset                       SERVICE_CONFIG_FILE_CHARSET = StandardCharsets.UTF_8;
    /**
     * Cached used to store service loader instance associated with the service interface
     */
    private static final Map<String, ServiceLoader<?>> SERVICE_LOADERS_CACHE       = new ConcurrentHashMap<>();
    /**
     * The loaded service implementation singleton instances associated with the SPI name
     */
    private final        Map<String, T>                singletonInstances          = new ConcurrentHashMap<>();
    /**
     * The class loader used to locate, load and instantiate service
     */
    private final        ClassLoader                   classLoader;
    /**
     * The interface representing the service being loaded
     */
    private final        Class<T>                      serviceInterface;
    /**
     * The loaded service implementation classes associated with the SPI name
     */
    private final        Map<String, Class<T>>         serviceImplClasses;


    /**
     * Get the service loader associated with service interface type class
     *
     * @param serviceInterface provider interface class annotated @Spi annotation
     * @param <T>              service interface type
     * @return the specified singleton service loader instance
     */
    public static <T> ServiceLoader<T> forClass(Class<T> serviceInterface) {
        Validate.notNull(serviceInterface, "Service interface must not be null!");
        Validate.isTrue(serviceInterface.isInterface(), "Service interface must be an interface class!");
        Validate.isTrue(serviceInterface.isAnnotationPresent(Spi.class), "Service interface must be annotated with @Spi annotation!");

        return createServiceLoader(serviceInterface);
    }

    /**
     * Create a service loader or get it from cache if exists
     *
     * @param serviceInterface service interface
     * @param <T>              service interface type
     * @return service instance loader cache instance
     */
    @SuppressWarnings("unchecked")
    private static synchronized <T> ServiceLoader<T> createServiceLoader(Class<T> serviceInterface) {
        ServiceLoader<T> loader = (ServiceLoader<T>) SERVICE_LOADERS_CACHE.get(serviceInterface.getName());
        if (loader == null) {
            // Load all the implementation classes
            loader = new ServiceLoader<>(serviceInterface.getClassLoader(), serviceInterface);
            SERVICE_LOADERS_CACHE.put(serviceInterface.getName(), loader);
        }
        return loader;
    }

    /**
     * Prevent instantiation of it outside the class
     *
     * @param classLoader      class loader
     * @param serviceInterface service interface
     */
    private ServiceLoader(ClassLoader classLoader, Class<T> serviceInterface) {
        ClassLoader preferred = serviceInterface != null ? serviceInterface.getClassLoader() : null;
        this.classLoader = preferred != null ? preferred :
                (classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader());
        this.serviceInterface = serviceInterface;
        // Load all the implementation classes
        this.serviceImplClasses = loadImplClasses();
    }

    /**
     * Load service implementation class based on the service configuration file
     *
     * @return service implementation class map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Class<T>> loadImplClasses() {
        String serviceFileName = SERVICE_DIR_PREFIX.concat(serviceInterface.getName());
        List<String> serviceImplClassNames = new ArrayList<>();
        try {
            Enumeration<URL> fileUrls = classLoader != null ? classLoader.getResources(serviceFileName) :
                    ClassLoader.getSystemResources(serviceFileName);
            if (CollectionUtils.sizeIsEmpty(fileUrls)) {
                log.warn("Cannot find the spi configuration file with name {}!", serviceFileName);
                return Collections.EMPTY_MAP;
            }
            while (fileUrls.hasMoreElements()) {
                // Loop each spi configuration file
                readImplClassNames(fileUrls.nextElement(), serviceInterface, serviceImplClassNames);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load the spi configuration file: ".concat(serviceFileName), e);
        }
        return loadImplClass(serviceImplClassNames);
    }

    /**
     * Read the service implementation class name
     *
     * @param fileUrl          file resource url
     * @param serviceInterface service interface
     * @param implClassNames   service implementation class names
     */
    private void readImplClassNames(URL fileUrl, Class<T> serviceInterface, List<String> implClassNames) {
        int lineNum = 0;
        // try-with-resource statement can automatically close the stream after use
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileUrl.openStream(), SERVICE_CONFIG_FILE_CHARSET))) {
            String line;
            // Read and assign value in one statement
            while ((line = reader.readLine()) != null) {
                readLine(fileUrl, line, ++lineNum, serviceInterface, implClassNames);
            }
        } catch (Exception e) {
            // Catch the exception and continue to read next line
            log.error("Failed to read the spi configuration file at line: " + lineNum, e);
        }
    }

    /**
     * Read line of the configuration file
     *
     * @param fileUrl          file resource url
     * @param line             line content
     * @param lineNum          line number
     * @param serviceInterface service interface
     * @param implClassNames   service implementation class names
     */
    private void readLine(URL fileUrl, String line, int lineNum, Class<T> serviceInterface, List<String> implClassNames) {
        int poundSignIdx = line.indexOf('#');
        if (poundSignIdx >= 0) {
            // Get the line string without the comment suffix
            line = line.substring(0, poundSignIdx);
        }

        line = line.trim();
        if (StringUtils.isEmpty(line)) {
            // Skip comment line
            return;
        }

        Validate.isTrue(!line.contains(StringUtils.SPACE) && !line.contains(CharacterUtils.TAB),
                "Found illegal space or tab key at line: " + lineNum + " of the file " + fileUrl);

        // Returns the character (Unicode code point) at the specified index
        // Codepoint of character 'a' is 97.
        // Codepoint of character 'b' is 98.
        int cp = line.codePointAt(0);
        // Determines if the character (Unicode code point) is permissible as the first character in a Java identifier.
        Validate.isTrue(Character.isJavaIdentifierStart(cp),
                "Found illegal service class name at line: " + lineNum + " of the file " + fileUrl);

        for (int i = Character.charCount(cp); i < line.length(); i += Character.charCount(cp)) {
            cp = line.codePointAt(i);
            Validate.isTrue(Character.isJavaIdentifierPart(cp) || cp == '.',
                    "Found illegal service class name at line: " + lineNum + " of the file " + fileUrl);
        }

        if (!implClassNames.contains(line)) {
            implClassNames.add(line);
        }
    }

    /**
     * Load the service implementation class associated with the interface class name
     *
     * @param implClassNames service implementation class name
     * @return spi name to service implementation class map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Class<T>> loadImplClass(List<String> implClassNames) {
        if (CollectionUtils.isEmpty(implClassNames)) {
            return Collections.emptyMap();
        }
        Map<String, Class<T>> map = new ConcurrentHashMap<>(implClassNames.size());
        for (String implClassName : implClassNames) {
            try {
                Class<T> implClass;
                if (classLoader == null) {
                    implClass = (Class<T>) Class.forName(implClassName);
                } else {
                    implClass = (Class<T>) Class.forName(implClassName, true, classLoader);
                }
                log.debug("Loaded the service instance [{}]", implClassName);

                // Validate the implementation class
                checkServiceImplClass(implClass);

                // SPI service name, e.g, 'failover' strategy
                String spiName = getSpiServiceName(implClass);

                Validate.isTrue(!map.containsKey(spiName), "Found duplicated SPI name: " + spiName + " for " + implClass.getName());
                map.put(spiName, implClass);
            } catch (Exception e) {
                log.error("Failed to load the spi class: " + implClassName, e);
            }
        }
        return map;
    }

    private void checkServiceImplClass(Class<T> implClass) {
        Validate.isTrue(Modifier.isPublic(implClass.getModifiers()), implClass.getName() + " must be public!");
        Validate.isTrue(serviceInterface.isAssignableFrom(implClass), implClass.getName() + " must be the implementation of " + serviceInterface.getName());
        checkConstructor(implClass);
    }

    private void checkConstructor(Class<T> implClass) {
        Constructor<?>[] constructors = implClass.getConstructors();
        Validate.notEmpty(constructors, implClass.getName() + " has no constructor");

        for (Constructor<?> constructor : constructors) {
            if (Modifier.isPublic(constructor.getModifiers()) && ArrayUtils.isEmpty(constructor.getParameterTypes())) {
                // Found the public no-arg constructor
                return;
            }
        }
        throw new IllegalArgumentException(implClass.getName() + " has no public no-args constructor");
    }

    /**
     * Get SPI service name from {@link SpiName}
     *
     * @param implClass service implementation class
     * @return SPI service name
     */
    public String getSpiServiceName(Class<?> implClass) {
        SpiName spiName = implClass.getAnnotation(SpiName.class);
        return spiName != null && StringUtils.isNotEmpty(spiName.value()) ? spiName.value() : implClass.getSimpleName();
    }

    /**
     * Manually add service implementation class to service loader
     *
     * @param implClass class to add to service loader
     */
    public void addServiceImplClass(Class<T> implClass) {
        if (implClass == null) {
            return;
        }
        checkServiceImplClass(implClass);
        String spiName = getSpiServiceName(implClass);
        synchronized (serviceImplClasses) {
            if (serviceImplClasses.containsKey(spiName)) {
                throw new IllegalArgumentException("Already existing the service implementation class with name: " + spiName);
            }
            serviceImplClasses.put(spiName, implClass);
        }
    }

    /**
     * Get service implementation instance by name
     *
     * @param name service implementation service name
     * @return implementation service instance
     */
    public T load(String name) {
        Validate.notEmpty(name, "Service name must not be empty!");

        try {
            Spi spi = serviceInterface.getAnnotation(Spi.class);
            if (SpiScope.SINGLETON.equals(spi.scope())) {
                return createSingleton(name);
            } else {
                return createPrototype(name);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load service instance: " + name);
        }
    }

    /**
     * Load all service implementations
     *
     * @return service implementations
     */
    public List<T> loadAll() {
        List<T> serviceImpls = new ArrayList<>(serviceImplClasses.size());
        serviceImplClasses.keySet().forEach(key -> serviceImpls.add(load(key)));
        return serviceImpls;
    }

    private T createSingleton(String name) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        T obj = singletonInstances.get(name);
        if (obj != null) {
            return obj;
        }

        Class<T> clz = serviceImplClasses.get(name);
        if (clz == null) {
            return null;
        }

        synchronized (singletonInstances) {
            obj = singletonInstances.get(name);
            if (obj != null) {
                return obj;
            }
            obj = clz.getDeclaredConstructor().newInstance();
            singletonInstances.put(name, obj);
        }
        return obj;
    }

    private T createPrototype(String name) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<T> clz = serviceImplClasses.get(name);
        if (clz == null) {
            return null;
        }
        return clz.getDeclaredConstructor().newInstance();
    }
}
