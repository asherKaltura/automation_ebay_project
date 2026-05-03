package pages;

import com.microsoft.playwright.Page;
import utils.ConfigReader;

public class CartPage extends BasePagePlaywright {

    public CartPage(Page page) {
        super(page);
    }

    // ===== OPEN CART =====
    public void openCart() {
        String url = ConfigReader.get("cart.url");
        navigate(url);
    }

    // ===== GET CART TOTAL =====
    public double getCartTotal() {

        String totalText = getText(
                page.locator("[data-test-id='SUBTOTAL']")
        );

        if (totalText == null || totalText.isEmpty()) {
            return 0.0;
        }

        String cleaned = totalText.replaceAll("[^0-9.]", "");

        return cleaned.isEmpty() ? 0.0 : Double.parseDouble(cleaned);
    }
}