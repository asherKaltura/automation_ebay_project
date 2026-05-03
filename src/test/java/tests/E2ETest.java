package tests;

import annotations.TestData;
import flows.ShoppingFlow;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.AssertUtils;
import utils.FlexibleDataProvider;

import java.util.Map;

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
    public void testE2E(Map<String, String> data) {
        //  LoginPage loginPage = new LoginPage(page);
        // loginPage.login();
        ShoppingFlow flow = new ShoppingFlow(page);

        // ===== TEST DATA =====
        String query = data.get("query");
        double maxPrice = Double.parseDouble(data.get("maxPrice"));
        int limit = Integer.parseInt(data.get("limit"));

        // ===== EXECUTION =====
        double total = flow.run(query, maxPrice, limit);

        // ===== ASSERTION =====

        AssertUtils.assertCartTotalNotExceeds(total,maxPrice,limit);
    }

}