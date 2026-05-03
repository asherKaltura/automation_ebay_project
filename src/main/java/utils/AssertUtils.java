package utils;

import assertion.AssertFalse;
import assertion.AssertTrue;
import assertion.KAssertion;

public class AssertUtils {
    protected static KAssertion assertion = new KAssertion();

    public static void assertCartTotalNotExceeds(
            double total,
            double budgetPerItem,
            int itemsCount
    ) {
        double expectedMax = budgetPerItem * itemsCount;
        assertion.verify(new AssertFalse(total > expectedMax, "Cart total exceeded! actual=" + total + " max=" + expectedMax), false);
    }
}