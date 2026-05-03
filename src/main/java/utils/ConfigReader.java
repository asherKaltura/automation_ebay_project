package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties prop = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Sorry, unable to find config.properties");
            } else {
                prop.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String get(String key) {
        if (System.getProperty(key)==null){
            return prop.getProperty(key);

        }else{

            return System.getProperty(key);
        }

    }
}
