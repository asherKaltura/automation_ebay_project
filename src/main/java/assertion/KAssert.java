package assertion;

import org.testng.asserts.IAssert;

public interface KAssert<T> extends IAssert<T> {

    String getSuccessMessage();
}