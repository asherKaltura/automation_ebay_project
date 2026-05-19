package flows;

import com.microsoft.playwright.Page;
import il.co.topq.difido.ReportDispatcher;
import il.co.topq.difido.ReportManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import pages.*;

import java.util.List;

public class ShoppingFlow {

    private final SearchResultsPage searchPage;
    private final ProductPage productPage;
    private final CartPage cartPage;
    private final ReportDispatcher report = ReportManager.getInstance();

    public ShoppingFlow(Page page) {
        this.searchPage = new SearchResultsPage(page);
        this.productPage = new ProductPage(page);
        this.cartPage = new CartPage(page);
    }

    @Step("Run full shopping flow: query={query}, maxPrice={maxPrice}, limit={limit}")
    public double run(String query, double maxPrice, int limit) {

        report.startLevel("Shopping Flow: " + query);

        // ===== SEARCH =====
        report.log("Searching for: " + query + " | maxPrice: " + maxPrice + " | limit: " + limit);
        List<String> urls = searchPage.searchItemsByNameUnderPrice(query, maxPrice, limit);

        report.log("Found " + urls.size() + " matching items under $" + maxPrice);
        Allure.step("Found " + urls.size() + " items matching criteria");

        // ===== ADD TO CART =====
        productPage.addItemsToCart(urls);

        // ===== OPEN CART =====
        cartPage.openCart();

        // ===== GET TOTAL =====
        double total = cartPage.getCartTotal();

        report.log("Cart total: $" + total);
        Allure.step("Cart total: $" + total);

        report.endLevel();

        return total;
    }
}
