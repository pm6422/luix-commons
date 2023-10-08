package com.luixtech.utilities.inputstream;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Solved class load issue when using springboot devtool
 */
@Slf4j
public class MultiClassLoaderObjectInputStream extends ObjectInputStream {
    public MultiClassLoaderObjectInputStream(InputStream str) throws IOException {
        super(str);
    }

    /**
     * Try :
     * 1. thread class loader
     * 2. application class loader
     * 3. system class loader
     *
     * @param desc an instance of class ObjectStreamClass
     * @return a Class object corresponding to desc
     * @throws IOException            any of the usual Input/Output exceptions.
     * @throws ClassNotFoundException if class of a serialized object cannot be found.
     */
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            return Class.forName(name, false, cl);
        } catch (Throwable ex) {
            log.debug("Cannot access thread context ClassLoader!", ex);
        }

        try {
            ClassLoader cl = MultiClassLoaderObjectInputStream.class.getClassLoader();
            return Class.forName(name, false, cl);
        } catch (Throwable ex) {
            log.debug("Cannot access application ClassLoader", ex);
        }

        try {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            return Class.forName(name, false, cl);
        } catch (Throwable ex) {
            log.debug("Cannot access system ClassLoader", ex);
        }
        return super.resolveClass(desc);
    }
}
