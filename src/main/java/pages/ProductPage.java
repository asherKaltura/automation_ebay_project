package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import il.co.topq.difido.ReportDispatcher;
import il.co.topq.difido.ReportManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import utils.DifidoReportUtil;

import java.util.List;

public class ProductPage extends BasePagePlaywright {

    private final ReportDispatcher report = ReportManager.getInstance();

    public ProductPage(Page page) {
        super(page);
    }

    // ===== ADD MULTIPLE ITEMS TO CART =====
    @Step("Add {urls.size()} items to cart")
    public void addItemsToCart(List<String> urls) {

        report.step("Add items to cart");

        for (int i = 0; i < urls.size(); i++) {

            report.step("Item " + (i + 1) + " of " + urls.size());
            Allure.step("Adding item " + (i + 1) + " of " + urls.size() + " to cart");

            navigate(urls.get(i));
            page.waitForLoadState();

            handleDropdowns();

            click(page.locator("#atcBtn_btn_1"));

            // ===== SCREENSHOT → ALLURE + DIFIDO =====
            byte[] screenshot = page.screenshot();
            String screenshotName = "item_" + (i + 1);

            Allure.getLifecycle().addAttachment(screenshotName, "image/png", "png", screenshot);
            DifidoReportUtil.attachScreenshot(screenshotName, screenshot);

            report.log("Item " + (i + 1) + " added to cart successfully");

            page.goBack();
        }

    }

    // ===== HANDLE DROPDOWNS (EXTRACTED LOGIC) =====
    @Step("Handle variant dropdowns")
    private void handleDropdowns() {

        Locator dropdowns = page.locator("[aria-haspopup='listbox']");
        int count = dropdowns.count();

        if (count == 0) {
            report.log("No dropdowns found on this product page");
            return;
        }

        report.log("Found " + count + " dropdown(s) to handle");

        for (int j = 0; j < count; j++) {

            Locator dropdown = dropdowns.nth(j);
            String text = dropdown.innerText();

            if (text.contains("rating") || text.contains("Positive")) {
                report.log("Skipping dropdown: " + text);
                continue;
            }

            click(dropdown);
            page.waitForSelector(".listbox__options:visible");

            Locator options = page.locator(".listbox__options:visible")
                    .locator(".listbox__option:not([aria-disabled='true'])");

            selectBestOption(options);
        }
    }

    // ===== SELECT OPTION (SAFE LOGIC) =====
    private void selectBestOption(Locator options) {

        int count = options.count();

        if (count > 1) {
            report.log("Selecting option index 1 of " + count);
            click(options.nth(1));
        } else if (count == 1) {
            report.log("Only one option available, selecting it");
            click(options.first());
        } else {
            report.log("No selectable options found");
        }
    }
}
