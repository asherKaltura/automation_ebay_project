package tests;

import annotations.TestData;
import flows.ShoppingFlow;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import utils.AssertUtils;
import utils.FlexibleDataProvider;

import java.util.Map;

@Epic("eBay Automation")
@Feature("Shopping Flow")
public class E2ETest extends BaseTest {

    @Test(
            dataProvider = "tabularData",
            dataProviderClass = FlexibleDataProvider.class
    )
    @TestData(
            value = "data/csvData.csv",
            source = enums.DataSource.CSV,
            limit = 1
    )
    @Story("Search, add to cart, and assert total")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Searches eBay for items under a given price, adds them to cart, and asserts total does not exceed budget")
    public void testE2E(Map<String, String> data) {

        // ===== TEST DATA =====
        String query     = data.get("query");
        double maxPrice  = Double.parseDouble(data.get("maxPrice"));
        int    limit     = Integer.parseInt(data.get("limit"));

        report.step("Test: testE2E | query=" + query + " maxPrice=" + maxPrice + " limit=" + limit);
        report.log("Starting E2E test with: query=" + query + ", maxPrice=" + maxPrice + ", limit=" + limit);

        // ===== EXECUTION =====
        ShoppingFlow flow = new ShoppingFlow(page);
        double total = flow.run(query, maxPrice, limit);

        // ===== ASSERTION =====
        AssertUtils.assertCartTotalNotExceeds(total, maxPrice, limit);


    }
}
