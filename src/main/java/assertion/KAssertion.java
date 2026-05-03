package assertion;

import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;
import report.KReportDispatcher;
import report.KReportManager;

public class KAssertion extends Assertion {

    private static KReportDispatcher report = KReportManager.getInstance();

    private boolean status;

    private boolean isAnyAssertionFailedLastPeriod = false;

    public boolean isAnyAssertionFailedLastPeriod() {
        boolean currentValue = isAnyAssertionFailedLastPeriod;
        isAnyAssertionFailedLastPeriod = false;
        return currentValue;
    }

    public void resetAssertionFailedLastPeriodCheck() {
        isAnyAssertionFailedLastPeriod = false;
    }

    public boolean verify(IAssert<?> assertCommand, boolean... throwException) {
        boolean actualThrowException = throwException.length > 0 ? throwException[0] : true;

        onBeforeAssert(assertCommand);
        try {
            executeAssert(assertCommand);
            onAssertSuccess(assertCommand);
            status = true;
        } catch (AssertionError ex) {
            onAssertFailure(assertCommand, ex);
            status = false;
            isAnyAssertionFailedLastPeriod = true;
            if (actualThrowException)
                throw ex;
        } finally {
            onAfterAssert(assertCommand);
        }
        return status;
    }

    public void failWithNoInterrupt(String message) {
        report.fail(message);
    }

    @Override
    public void onAssertSuccess(IAssert<?> var1) {
        if (var1 instanceof KAssertImpl) {
            report.success(((KAssert) var1).getSuccessMessage());
        }
    }

    @Override
    public void onAssertFailure(IAssert<?> var1, AssertionError ex) {
        if (var1 instanceof KAssertImpl) {
            report.fail((var1).getMessage());
        }
    }
}
