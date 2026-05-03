package report;


import enums.TextColor;
import enums.TextStyle;
import il.co.topq.difido.ReportDispatcher;
import il.co.topq.difido.model.Enums;
import il.co.topq.difido.model.test.ReportElement;

import java.util.HashMap;
import java.util.List;

public interface KReportDispatcher extends ReportDispatcher {

    public abstract void success(String title);

//    public abstract void success(String title, String message);

    public abstract void fail(String title);

    public abstract void fail(String title, String message);

    public abstract void bold(String title);

    public abstract void debug(String title);

    public abstract void debug(String title, Enums.Status status);

    public abstract void addFile(String fileContent, String description) throws Exception;

    public abstract String getLastReportTitle();

    public abstract void last();

    public abstract KReportElement getLastReportElement();

    public abstract void report(ReportElement reportElement);

    public abstract ReportElement getReportElement(String title);

    public abstract ReportElement getReportElement(String title, Enums.Status status);

    public abstract ReportElement getReportElement(String title, String message);

    public abstract ReportElement getReportElement(String title, String message, Enums.Status status);

    public abstract void logHtml(String var1);

    public abstract void logFormat(String text, TextColor color, TextStyle... style);

    public abstract void logFormat(String text, TextStyle... style);

    public abstract void logFormat(String text, TextColor color);

    public abstract void logKeyValue(String key, String value);

    public abstract boolean isSilent();

    public abstract void setSilent(boolean silent);

    public abstract void startBuffer();
    public abstract void stopBuffer();

    public abstract List<KReportElement> getBufferedReport(String threadName);
    public abstract HashMap<String, List<KReportElement>> getBufferedReport();
    public abstract void reportAll(List<KReportElement> reportElements);
    public abstract void reportAllBuffer();

    public abstract void testNameDivider(String testName);
}
