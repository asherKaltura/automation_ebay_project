package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsPage extends BasePagePlaywright {

    public SearchResultsPage(Page page) {
        super(page);
    }

    // ===== SEARCH + FILTER RESULTS =====
    public List<String> searchItemsByNameUnderPrice(String query, double maxPrice, int limit) {

        List<String> results = new ArrayList<>();

        search(query);

        while (results.size() < limit) {

            waitForResults();

            Locator items = page.locator("ul.srp-results > li.s-card");
            int count = items.count();

            for (int i = 0; i < count && results.size() < limit; i++) {

                Locator item = items.nth(i);

                Double price = extractPrice(item);
                if (price == null || price > maxPrice) continue;

                String url = extractUrl(item);
                if (url != null) {
                    results.add(url);
                }
            }

            if (!goToNextPage(results.size(), limit)) {
                break;
            }
        }

        return results;
    }

    // ===== SEARCH =====
    private void search(String query) {
        AllureStep("Search for: " + query);

        page.fill("#gh-ac", query);
        page.click("#gh-search-btn");
    }

    // ===== WAIT RESULTS =====
    private void waitForResults() {
        page.waitForSelector("ul.srp-results li.s-card");
    }

    // ===== EXTRACT PRICE (SAFE) =====
    private Double extractPrice(Locator item) {

        Locator priceEl = item.locator(".s-card__price").first();
        if (!priceEl.isVisible()) return null;

        String priceRaw = priceEl.innerText();

        String cleaned = priceRaw.replaceAll("[^0-9.]", "");

        if (cleaned.isEmpty()) return null;

        try {
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    // ===== EXTRACT URL =====
    private String extractUrl(Locator item) {

        Locator link = item.locator("a.s-card__link").first();

        return link.getAttribute("href");
    }

    // ===== NEXT PAGE =====
    private boolean goToNextPage(int currentSize, int limit) {

        Locator nextBtn = page.locator("a[aria-label='Go to next search page']");

        if (currentSize < limit && nextBtn.isVisible()) {
            click(nextBtn);
            page.waitForLoadState();
            return true;
        }

        return false;
    }

    // ===== ALLURE HELPER =====
    private void AllureStep(String msg) {
        io.qameta.allure.Allure.step(msg);
    }
}