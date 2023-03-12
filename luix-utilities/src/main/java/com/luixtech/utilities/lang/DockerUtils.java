package com.luixtech.utilities.lang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * DockerUtils
 */
public abstract class DockerUtils {

    /**
     * Docker flag
     */
    private static boolean IS_DOCKER;

    static {
        try (Stream<String> stream = Files.lines(Paths.get("/proc/1/cgroup"))) {
            IS_DOCKER = stream.anyMatch(line -> line.contains("/docker") || line.contains("/pod"));
        } catch (IOException e) {
            IS_DOCKER = false;
        }
    }

    public static boolean isRunningInDocker() {
        return IS_DOCKER;
    }
}
