package utils;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public final class FileUtilities {

    private static final ConcurrentHashMap<String, File> CACHE = new ConcurrentHashMap<>();

    private FileUtilities() {}

    public static File getResourceFile(String path) {
        return CACHE.computeIfAbsent(path, p -> {
            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                URL url = cl.getResource(p);

                if (url == null) {
                    throw new RuntimeException("Resource not found: " + p);
                }

                return new File(url.toURI());
            } catch (Exception e) {
                throw new RuntimeException("Failed to load resource: " + path, e);
            }
        });
    }
}
