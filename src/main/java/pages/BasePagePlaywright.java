package pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.SelectOption;
import il.co.topq.difido.ReportDispatcher;
import il.co.topq.difido.ReportManager;
import io.qameta.allure.Allure;
import utils.DifidoReportUtil;

import java.io.ByteArrayInputStream;

public class BasePagePlaywright {
    protected ReportDispatcher report = ReportManager.getInstance();

    protected Page page;
    private final int DEFAULT_TIMEOUT = 10000;

    public BasePagePlaywright(Page page) {
        this.page = page;
    }

    // ===== WAIT =====
    protected void waitForVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setTimeout(DEFAULT_TIMEOUT));
    }

    // ===== CLICK =====
    public void click(Locator locator) {
        waitForVisible(locator);
        Allure.step("Click element");
        locator.click();
    }
    public void clickVisibleByText(String text) {
        click(page.locator("a").filter(
                new Locator.FilterOptions().setHasText(text)
        ).first());
    }
    public void clickWithoutWait(Locator locator) {
        Allure.step("Click without wait");
        locator.click();
    }

    // ===== TYPE =====
    public void type(Locator locator, String text) {
        waitForVisible(locator);
        Allure.step("Type: " + text);
        locator.fill(text);
    }

    // ===== GET TEXT =====
    public String getText(Locator locator) {
        waitForVisible(locator);
        String text = locator.textContent();
        String result = (text != null) ? text.trim() : "";

        Allure.step("Get text: " + result);
        return result;
    }
    // ===== FILL (DIRECT INPUT) =====
    public void fill(Locator locator, String text) {
        waitForVisible(locator);
        Allure.step("Fill: " + text);
        locator.fill(text);
    }
    // ===== ASSERT VISIBILITY =====
    public boolean isVisible(Locator locator) {
        boolean visible = locator.isVisible();
        Allure.step("Element visible: " + visible);
        return visible;
    }

    // ===== DROPDOWN =====
    public void selectByLabel(Locator locator, String label) {
        waitForVisible(locator);
        Allure.step("Select by label: " + label);
        locator.selectOption(new SelectOption().setLabel(label));
    }

    public void selectByValue(Locator locator, String value) {
        waitForVisible(locator);
        Allure.step("Select by value: " + value);
        locator.selectOption(new SelectOption().setValue(value));
    }

    // ===== NAVIGATION (NEW) =====
    public void navigate(String url) {
        Allure.step("Navigate to: " + url);
        page.navigate(url);
    }

    // ===== SCREENSHOT (IMPROVED) =====
    public void takeScreenshot(String name) {
        byte[] screenshot = page.screenshot(
                new Page.ScreenshotOptions().setFullPage(true)
        );

        Allure.addAttachment(
                name,
                "image/png",
                new ByteArrayInputStream(screenshot),
                ".png"
        );
        DifidoReportUtil.attachScreenshot(name,screenshot);
    }
    public void takeScreenshotDifido(String name) {
        byte[] screenshot = page.screenshot(
                new Page.ScreenshotOptions().setFullPage(true)
        );
        Allure.addAttachment(
                name,
                "image/png",
                new ByteArrayInputStream(screenshot),
                ".png"
        );
        DifidoReportUtil.attachScreenshot(name,screenshot);
    }
    // ===== REACT DROPDOWN (IMPROVED) =====
    public void selectReact(String inputId, String value) {
        Allure.step("Select React value: " + value);

        page.locator("div:has(input#" + inputId + ")").click();
        page.keyboard().type(value);
        page.keyboard().press("Enter");
    }
}