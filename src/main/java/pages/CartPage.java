package pages;

import com.microsoft.playwright.Page;
import il.co.topq.difido.ReportDispatcher;
import il.co.topq.difido.ReportManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import utils.ConfigReader;

public class CartPage extends BasePagePlaywright {

    private final ReportDispatcher report = ReportManager.getInstance();

    public CartPage(Page page) {
        super(page);
    }

    // ===== OPEN CART =====
    @Step("Open cart page")
    public void openCart() {
        String url = ConfigReader.get("cart.url");
        report.log("Navigating to cart: " + url);
        navigate(url);
    }

    // ===== GET CART TOTAL =====
    @Step("Get cart total")
    public double getCartTotal() {

        String totalText = getText(
                page.locator("[data-test-id='SUBTOTAL']")
        );

        if (totalText == null || totalText.isEmpty()) {
            report.log("Cart total not found - returning 0.0");
            Allure.step("Cart total not found - returned 0.0");
            return 0.0;
        }

        String cleaned = totalText.replaceAll("[^0-9.]", "");
        double total = cleaned.isEmpty() ? 0.0 : Double.parseDouble(cleaned);

        report.log("Cart total: $" + total);
        Allure.step("Cart total is: $" + total);

        return total;
    }
}
