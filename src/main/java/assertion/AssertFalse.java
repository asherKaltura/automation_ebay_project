package assertion;
import org.testng.Assert;

public class AssertFalse extends KAssertImpl implements KAssert<Boolean> {

    private Boolean condition;
    private String message;

    public AssertFalse(Boolean condition, String message) {
        this.condition = condition;
        this.message = message;
    }

    @Override
    public String getSuccessMessage() {
        return message + " assertion passed";
    }

    @Override
    public String getMessage() {
        return message + " assertion failed";
    }

    @Override
    public void doAssert() {
        Assert.assertFalse(condition);
    }

    @Override
    public Boolean getActual() {
        return null;
    }

    @Override
    public Boolean getExpected() {
        return null;
    }
}