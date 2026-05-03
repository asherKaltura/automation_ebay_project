package flows;

import com.microsoft.playwright.Page;
import pages.*;

import java.util.List;

public class ShoppingFlow {

    private final SearchResultsPage searchPage;
    private final ProductPage productPage;
    private final CartPage cartPage;

    public ShoppingFlow(Page page) {
        this.searchPage = new SearchResultsPage(page);
        this.productPage = new ProductPage(page);
        this.cartPage = new CartPage(page);
    }

    public double run(String query, double maxPrice, int limit) {

        List<String> urls =
                searchPage.searchItemsByNameUnderPrice(query, maxPrice, limit);

        productPage.addItemsToCart(urls);

        cartPage.openCart();

        return cartPage.getCartTotal();
    }
}