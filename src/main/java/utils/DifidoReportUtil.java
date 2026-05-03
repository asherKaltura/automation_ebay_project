package utils;

import report.KReportManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DifidoReportUtil {

    private static final String SCREENSHOT_DIR = "target/screenshots/";

    public static void attachScreenshot(String name, byte[] screenshot) {

        try {
            // ===== SAVE FILE =====
            Path dir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(dir);

            String filePath = SCREENSHOT_DIR + name + ".png";
            Files.write(Paths.get(filePath), screenshot);

            // ===== ATTACH TO DIFIDO / KREPORT =====
            KReportManager.getInstance()
                    .addFile(new File(filePath), name);

        } catch (Exception e) {
            throw new RuntimeException("Failed to attach screenshot to Difido", e);
        }
    }
}