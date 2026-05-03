package report;

import enums.TextColor;
import enums.TextStyle;
import il.co.topq.difido.ReportDispatcher;
import il.co.topq.difido.ReportManager;
import il.co.topq.difido.model.Enums;
import il.co.topq.difido.model.test.ReportElement;
import il.co.topq.difido.reporters.AbstractDifidoReporter;
import il.co.topq.difido.reporters.Reporter;
import org.apache.commons.io.FileUtils;
import org.testng.IInvokedMethod;
import org.testng.ITestResult;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class KReportManager implements KReportDispatcher  {

    private ReportDispatcher report;
    private static KReportManager instance;

    private KReportElement lastReportElement;
    private String lastReportTitle;

    private boolean silent;
    private boolean buffer;

    private HashMap<String, List<KReportElement>> bufferedReports;

    private KReportManager() {
        report = ReportManager.getInstance();
        silent = false;
        buffer = false;
    }

    public static KReportManager getInstance() {
        if (null == instance) {
            instance = new KReportManager();
        }

        return instance;
    }

    @Override
    public void success(String title) {
        logHtml("<font color=\"green\">" + title + "</font>", Enums.Status.success);
    }

    @Override
    public void fail(String title) {
        fail(title, null);
    }

    @Override
    public void fail(String title, String message) {
        logHtml(title, message, Enums.Status.failure);
    }

    @Override
    public void bold(String title) {
        logHtml("<b>" + title + "</b>", Enums.Status.success);
    }

    @Override
    public void debug(String title) {
        debug(title, Enums.Status.success);
    }

    @Override
    public void debug(String title, Enums.Status status) {
        logHtml("<mark>DEBUG: " + title + "</mark>", status);
    }

    @Override
    public void logHtml(String title) {
        logHtml(title, Enums.Status.success);
    }

    @Override
    public void logHtml(String title, Enums.Status status) {
        logHtml(title, null, status);
    }

    @Override
    public void logHtml(String title, String message, Enums.Status status) {
        Enums.ElementType type = Enums.ElementType.html;
        if (!silent) {
            if (buffer) {
                addBufferedReport(title, message, null, status, type);
            } else {
                if (isTeardownWarning(status)) {
                    status = Enums.Status.success;
                    title = getTeardownWarningReport(title);
                }
                report.logHtml(title, message, status);
            }
        }
        setLastReport(title, message, status, type);
    }

    @Override
    public void logKeyValue(String key, String value){
        logHtml(String.format("<b>%s:</b> %s", key, value));
    }

    @Override
    public void logFormat(String text, TextColor color, TextStyle... styles) {
        if(!silent){
            StringBuilder stylesOpen = new StringBuilder();
            StringBuilder stylesClose = new StringBuilder();
            StringBuilder finalText = new StringBuilder();
            if(styles != null) {
                Arrays.stream(styles).forEach(style -> stylesOpen.append("<").append(style.getHtmlValue()).append(">"));
                Arrays.stream(styles).sorted(Comparator.reverseOrder()).forEach(style -> stylesClose.append("</").append(style.getHtmlValue()).append(">"));
            }

            if(color != null){
                finalText.append(String.format("<font color='%s'>", color.name().toLowerCase()));
            }
            if(styles != null && styles.length > 0 && styles[0] != TextStyle.NONE){
                finalText.append(stylesOpen);
            }
            finalText.append(text);
            if(styles != null){
                finalText.append(stylesClose);
            }
            if(color != null){
                finalText.append("</font>");
            }
            logHtml(finalText.toString());
        }
    }

    @Override
    public void logFormat(String text, TextStyle ... style) {
        logFormat(text, null, style);
    }

    @Override
    public void logFormat(String text, TextColor color) {
        logFormat(text, color, TextStyle.NONE);
    }

    @Override
    public void log(String title) {
        log(title, Enums.Status.success);
    }

    @Override
    public void log(String title, Enums.Status status) {
        log(title, null, status);
    }

    @Override
    public void log(String title, String message) {
        log(title, message, Enums.Status.success);
    }

    @Override
    public void log(String title, String message, Enums.Status status) {
        Enums.ElementType type = Enums.ElementType.regular;
        if (!silent) {
            if (buffer) {
                addBufferedReport(title, message, null, status, type);
            } else {
                if (isTeardownWarning(status)) {
                    status = Enums.Status.success;
                    title = getTeardownWarningReport(title);
                }
                report.log(title, message, status);
            }
        }
        setLastReport(title, message, status, type);
    }

    @Override
    public void startLevel(String description) {
        if (!silent) {
            if (buffer) {
                addBufferedReport(description, null, null, Enums.Status.success, Enums.ElementType.startLevel);
            } else {
                report.startLevel(description);
            }
        }
    }

    @Override
    public void endLevel() {
        if (!silent) {
            if (buffer) {
                addBufferedReport(null, null, null, Enums.Status.success, Enums.ElementType.stopLevel);
            } else {
                report.endLevel();
            }
        }
    }

    @Override
    public void step(String description) {
        description = "<table width=\"100%\">\n" +
                "  <tr>\n" +
                "    <td style=\"width:1px; padding: 0 10px; white-space: nowrap;\"><font color=\"#0000ff\"><b>"+ description + "</b></font></td>\n" +
                "    <td><hr /></td>\n" +
                "  </tr>\n" +
                "</table>";
        logHtml(description, Enums.Status.success);
    }

    @Override
    public void addFile(File file, String description) {
        Enums.Status status = Enums.Status.success;
        Enums.ElementType type = Enums.ElementType.lnk;
        if (!silent) {
            if (buffer) {
                addBufferedReport(description, null, file, status, type);
            } else {
                report.addFile(file, description);
            }
        }
        setLastReport(description, null, status, type);
    }

    public void addFile(String fileContent, String description) throws Exception {
        if (!silent) {
            File file = new File("file_" + System.currentTimeMillis() + ".log");
            file.createNewFile();
            FileUtils.writeStringToFile(file, fileContent);
            addFile(file, description);
        }
    }

    @Override
    public void addImage(File file, String description) {
        Enums.Status status = Enums.Status.success;
        Enums.ElementType type = Enums.ElementType.img;
        if (!silent) {
            if (buffer) {
                addBufferedReport(description, null, file, status, type);
            } else {
                report.addImage(file, description);
            }
        }
        setLastReport(description, null, status, type);
    }

    @Override
    public void addLink(String link, String description) {
        Enums.Status status = Enums.Status.success;
        Enums.ElementType type = Enums.ElementType.lnk;
        if (!silent) {
            if (buffer) {
                addBufferedReport(description, link, null, status, type);
            } else {
                report.addLink(link, description);
            }
        }
        setLastReport(description, link, status, type);
    }

    @Override
    public void addTestProperty(String name, String value) {
        report.addTestProperty(name, value);
    }

    @Override
    public void addRunProperty(String name, String value) {
        report.addRunProperty(name, value);
    }

    @Override
    public void beforeTeardown(IInvokedMethod method, ITestResult testResult) {
        report.beforeTeardown(method, testResult);
    }

    @Override
    public void beforeSetup(IInvokedMethod method, ITestResult testResult) {
        report.beforeSetup(method, testResult);
    }

    @Override
    public void afterTeardown(IInvokedMethod method, ITestResult testResult) {
        report.afterTeardown(method, testResult);
    }

    @Override
    public void afterSetup(IInvokedMethod method, ITestResult testResult) {
        report.afterSetup(method, testResult);
    }


    @Override
    public String getLastReportTitle() {
        return lastReportTitle;
    }

    private void setLastReport(String title, String message, Enums.Status status, Enums.ElementType type) {
        if (type == Enums.ElementType.regular || type == Enums.ElementType.html) {
            lastReportElement = new KReportElement();
            lastReportElement.setTitle(title);
            lastReportElement.setMessage(message);
            lastReportElement.setStatus(status);
            lastReportElement.setType(type);
        }
        lastReportTitle = title;
    }

    public void last() {
        switch (lastReportElement.getType()) {
            case regular:
                report.log(lastReportElement.getTitle(), lastReportElement.getMessage(), lastReportElement.getStatus());
                break;
            case html:
                report.logHtml(lastReportElement.getTitle(), lastReportElement.getMessage(), lastReportElement.getStatus());
                break;
            default:
                break;
        }
    }

    public void report(ReportElement reportElement) {
        switch (reportElement.getType()) {
            case regular:
                report.log(reportElement.getTitle(), reportElement.getMessage(), reportElement.getStatus());
                break;
            case html:
                report.logHtml(reportElement.getTitle(), reportElement.getMessage(), reportElement.getStatus());
                break;
            default:
                break;
        }
    }

    public KReportElement getLastReportElement() {
        return lastReportElement;
    }

    @Override
    public KReportElement getReportElement(String title) {
        return getReportElement(title, null, Enums.Status.success);
    }

    @Override
    public KReportElement getReportElement(String title, Enums.Status status) {
        return getReportElement(title, null, status);
    }

    @Override
    public KReportElement getReportElement(String title, String message) {
        return getReportElement(title, message, Enums.Status.success);
    }

    @Override
    public KReportElement getReportElement(String title, String message, Enums.Status status) {
        return getReportElement(title, message, status, null);
    }

    public KReportElement getReportElement(String title, String message, Enums.Status status, Enums.ElementType type) {
        return getReportElement(title, message, null, status, type);
    }

    private KReportElement getReportElement(String title, String message, File file, Enums.Status status, Enums.ElementType type) {
        KReportElement reportElement = new KReportElement();
        reportElement.setTitle(title);
        reportElement.setMessage(message);
        reportElement.setFile(file);
        reportElement.setStatus(status);
        if (null == type) {
            type = Enums.ElementType.regular;
        }

        reportElement.setType(type);
        return reportElement;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public void startBuffer() {
        buffer = true;
        bufferedReports = new HashMap<>();
    }

    public void stopBuffer() {
        buffer = false;
    }


    public List<KReportElement> getBufferedReport(String threadName) {
        return bufferedReports.get(threadName);
    }

    public HashMap<String, List<KReportElement>> getBufferedReport() {
        return bufferedReports;
    }

    public void reportAll(List<KReportElement> reportElements) {
        for (KReportElement reportElement : reportElements) {
            switch (reportElement.getType()) {
                case regular:
                    report.log(reportElement.getTitle(), reportElement.getMessage(), reportElement.getStatus());
                    break;
                case html:
                    report.logHtml(reportElement.getTitle(), reportElement.getMessage(), reportElement.getStatus());
                    break;
                case img:
                    report.addImage(reportElement.getFile(), reportElement.getTitle());
                    break;
                case lnk:
                    if (reportElement.getFile() != null) {
                       report.addFile(reportElement.getFile(), reportElement.getTitle());
                    } else {
                        report.addLink(reportElement.getMessage(), reportElement.getTitle());
                    }
                default:
                    break;
            }
        }
    }

    public void reportAllBuffer() {
        getBufferedReport().keySet().forEach(threadName -> {
            reportAll(getBufferedReport().get(threadName));
        });
    }

    private void addBufferedReport(String title, String message, File file, Enums.Status status, Enums.ElementType type) {
        KReportElement reportElement = getReportElement(title, message, file, status, type);
        String currentThreadName = Thread.currentThread().getName();
        List<KReportElement> threadBufferedReports;
        if (bufferedReports.keySet().contains(currentThreadName)) {
            threadBufferedReports = bufferedReports.get(currentThreadName);
        } else {
            threadBufferedReports = new ArrayList<>();
        }
        threadBufferedReports.add(reportElement);
        bufferedReports.put(currentThreadName, threadBufferedReports);
    }

    private boolean isTeardownWarning(Enums.Status status) {
        if (status == Enums.Status.failure) {
            // Change failure status to warning if we are in teardown
            try {
                Field reportersField = getField(report.getClass(),"reporters", 3);
                if (reportersField == null) return false;
                Field inTeardownField = getField(((AbstractDifidoReporter)((ArrayList<Reporter>) reportersField.get(report)).get(0)).getClass(), "inTeardown", 3);
                if (inTeardownField == null) return false;
                boolean inTeardown = (Boolean) inTeardownField.get((((ArrayList<Reporter>) reportersField.get(report)).get(0)));
                return inTeardown;
            } catch (Exception e) {}
        }
        return false;
    }

    private Field getField(Class classObj, String fieldName, int maxDepth) throws Exception{
        Class serviceClass = classObj;
        Field serviceField;
        for(int i = 0 ; i < maxDepth ; i++){
            if(Arrays.stream(serviceClass.getDeclaredFields()).anyMatch(f -> f.getName().equals(fieldName))){
                serviceField = serviceClass.getDeclaredField(fieldName);
                serviceField.setAccessible(true);
                return serviceField;
            }
            serviceClass = serviceClass.getSuperclass();
            if(serviceClass == null) break;
        }
        return null;
    }

    @Override
    public void testNameDivider(String testName){
        String testNameFormatted = "<div style='border-bottom: 2px solid #000; padding-top: 5px; margin-top: 10px;'>" + testName+ "</div>";
        logFormat(testNameFormatted, TextColor.BLUE, TextStyle.ITALIC);
    }

    private String getTeardownWarningReport(String title) {
        return "<font color=\"orange\"><b>" + title + "</b></font>";
    }
}
