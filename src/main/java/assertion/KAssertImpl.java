package assertion;

public class KAssertImpl {

    protected String getPrintableHTML(String text) {
        return text.replace("<", "&lt;").replace(">", "&gt;");
    }
}
