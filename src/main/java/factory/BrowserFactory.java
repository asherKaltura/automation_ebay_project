package factory;

import com.microsoft.playwright.*;

public class BrowserFactory {

    public static Browser createBrowser(Playwright playwright,
                                        String browserType,
                                        boolean headless,
                                        int slowMo) {

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMo);

        switch (browserType.toLowerCase()) {
            case "firefox":
                return playwright.firefox().launch(options);

            case "webkit":
                return playwright.webkit().launch(options);

            default:
                return playwright.chromium().launch(options);
        }
    }
}