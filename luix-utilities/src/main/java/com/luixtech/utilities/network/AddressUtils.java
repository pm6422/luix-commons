package com.luixtech.utilities.network;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Network address utils
 */
@Slf4j
public abstract class AddressUtils {
    public static final  String      LOCALHOST          = "127.0.0.1";
    public static final  String      ANY_HOST           = "0.0.0.0";
    public static final  String      INFINITY_IP_PREFIX = "LUIX_IP_PREFIX";
    private static final Pattern     ADDRESS_PATTERN    = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}:\\d{1,5}$");
    private static final Pattern     IP_PATTERN         = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    private static final String      COMMA_SEPARATOR    = ",";
    private static final int         MAX_PORT           = 65535;
    private static       InetAddress localAddressCache  = null;

    /**
     * Check whether it is the valid IP address
     *
     * @param address IP address, e.g 192.168.1.1:8080
     * @return {@code true} if it was valid and {@code false} otherwise
     */
    public static boolean isValidAddress(String address) {
        return ADDRESS_PATTERN.matcher(address).matches();
    }

    /**
     * Check whether it is the valid IP
     *
     * @param ip ip
     * @return {@code true} if it was active and {@code false} otherwise
     */
    public static boolean isValidIp(String ip) {
        return IP_PATTERN.matcher(ip).matches();
    }

    /**
     * Check whether it is the valid IP address
     *
     * @param address IP address
     * @return {@code true} if it was valid and {@code false} otherwise
     */
    public static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null && !ANY_HOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
    }

    /**
     * Get the intranet IP address based on priorities.
     * Configuration priority:
     * environment variables > IP whose network interface matches the IP prefix
     * > IP associated with the hostname > Loop all the network interfaces
     *
     * @return local ip address
     */
    public static String getIntranetIp() {
        if (localAddressCache != null) {
            // Get from cache
            return localAddressCache.getHostAddress();
        }
        InetAddress localAddress = null;
        // Get IP prefix from environment variable
        String ipPrefix = System.getenv(INFINITY_IP_PREFIX);
        if (StringUtils.isNotEmpty(ipPrefix)) {
            // Get the IP whose network interface matches the IP prefix
            localAddress = getLocalAddressByNetworkInterface(ipPrefix);
            log.info("Found local address [{}] by ip prefix [{}]", localAddress, ipPrefix);
        }
        if (!isValidAddress(localAddress)) {
            localAddress = getLocalAddressByHostname();
            log.info("Found local address [{}] by hostname", localAddress);
        }
        if (!isValidAddress(localAddress)) {
            localAddress = getLocalAddressByNetworkInterface(null);
            log.info("Found local address [{}] by looping network interfaces", localAddress);
        }
        if (!isValidAddress(localAddress)) {
            log.warn("Failed to get local address!");
            return null;
        }
        localAddressCache = localAddress;
        return localAddressCache.getHostAddress();
    }

    private static InetAddress getLocalAddressByNetworkInterface(String prefix) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces == null) {
                return null;
            }
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (isValidAddress(address)) {
                        if (StringUtils.isBlank(prefix)) {
                            return address;
                        }
                        if (address.getHostAddress().startsWith(prefix)) {
                            return address;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            log.warn("Failed to get local ip address", e);
        }
        return null;
    }

    private static InetAddress getLocalAddressByHostname() {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            log.warn("Failed to get local address by hostname", e);
        }
        return null;
    }

    public static List<Pair<String, Integer>> parseAddress(String address) {
        return Arrays.stream(address.split(COMMA_SEPARATOR)).map(AddressUtils::parseHostPort).collect(Collectors.toList());
    }

    private static Pair<String, Integer> parseHostPort(String addr) {
        String[] hostAndPort = addr.split(":");
        String host = hostAndPort[0].trim();
        int port = Integer.parseInt(hostAndPort[1].trim());
        if (port < 0 || port > MAX_PORT) {
            throw new IllegalArgumentException("Illegal port range!");
        }
        return Pair.of(host, port);
    }

    public static String getHostName(SocketAddress socketAddress) {
        if (socketAddress == null) {
            return null;
        }
        if (socketAddress instanceof InetSocketAddress) {
            InetAddress addr = ((InetSocketAddress) socketAddress).getAddress();
            if (addr != null) {
                return addr.getHostAddress();
            }
        }
        return null;
    }
}
