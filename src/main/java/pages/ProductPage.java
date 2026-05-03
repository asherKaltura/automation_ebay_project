package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

public class ProductPage extends BasePagePlaywright {

    public ProductPage(Page page) {
        super(page);
    }

    // ===== ADD MULTIPLE ITEMS TO CART =====
    public void addItemsToCart(List<String> urls) {

        for (int i = 0; i < urls.size(); i++) {

            navigate(urls.get(i));
            page.waitForLoadState();

            handleDropdowns();

            click(page.locator("#atcBtn_btn_1"));

            takeScreenshot("item_" + i);

            page.goBack();
        }
    }

    // ===== HANDLE DROPDOWNS (EXTRACTED LOGIC) =====
    private void handleDropdowns() {

        Locator dropdowns = page.locator("[aria-haspopup='listbox']");

        for (int j = 0; j < dropdowns.count(); j++) {

            Locator dropdown = dropdowns.nth(j);

            String text = dropdown.innerText();

            if (text.contains("rating") || text.contains("Positive")) {
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
            click(options.nth(1));
        } else if (count == 1) {
            click(options.first());
        }
    }
}