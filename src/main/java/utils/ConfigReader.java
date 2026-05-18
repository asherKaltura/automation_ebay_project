package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties prop = new Properties();

    static {
        String env = System.getProperty("env", "default");

        String fileName = env.equals("default")
                ? "config.properties"
                : "config-" + env + ".properties";

        System.out.println("[ConfigReader] Loading: " + fileName);

        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream(fileName)) {

            if (input == null) {
                System.err.println("[ConfigReader] File not found: " + fileName + " — falling back to config.properties");
                InputStream fallback = ConfigReader.class.getClassLoader()
                        .getResourceAsStream("config.properties");
                if (fallback != null) prop.load(fallback);
            } else {
                prop.load(input);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {

        // 1. ENV Variable —
        String envKey = key.toUpperCase().replace(".", "_");
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isEmpty()) {
            return envVal;
        }

        // 2. System Property
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }

        // 3. config.properties
        return prop.getProperty(key);
    }
}