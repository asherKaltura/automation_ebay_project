package utils;

import assertion.AssertFalse;
import assertion.KAssertion;
import il.co.topq.difido.ReportDispatcher;
import il.co.topq.difido.ReportManager;
import il.co.topq.difido.model.Enums;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public class AssertUtils {

    protected static KAssertion assertion = new KAssertion();
    private static final ReportDispatcher report = ReportManager.getInstance();

    @Step("Assert cart total does not exceed budget: total={total}, maxPrice={budgetPerItem}, items={itemsCount}")
    public static void assertCartTotalNotExceeds(
            double total,
            double budgetPerItem,
            int itemsCount
    ) {
        double expectedMax = budgetPerItem * itemsCount;
        boolean exceeded = total > expectedMax;

        if (exceeded) {
            String msg = "FAILED: Cart total exceeded! actual=$" + total + " max=$" + expectedMax;
            report.log(msg);
            Allure.step( msg);
            assertion.fail(msg);
        } else {
            String msg = "PASSED: Cart total $" + total + " is within budget $" + expectedMax;
            report.log(msg, Enums.Status.success);
            Allure.step(msg);
        }
    }
}