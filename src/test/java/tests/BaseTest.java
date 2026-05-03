package tests;

import com.microsoft.playwright.*;
import factory.BrowserFactory;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.*;
import test.AbstractTestCase;
import utils.ConfigReader;

@Listeners({AllureTestNg.class})
public class BaseTest  extends AbstractTestCase {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeMethod
    public void setup() {

        playwright = Playwright.create();

        String browserType = ConfigReader.get("browser");
        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless"));
        String baseUrl = ConfigReader.get("base.url");

        // 🔥 DEBUG logic
        boolean isDebug = Boolean.parseBoolean(System.getProperty("debug", "false"));

        int slowMo = isDebug ? 500 : 0;

        browser = BrowserFactory.createBrowser(
                playwright,
                browserType,
                headless,
                slowMo
        );

        context = browser.newContext();
        page = context.newPage();

        page.navigate(baseUrl);
    }

    @AfterMethod
    public void teardown() {

        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}