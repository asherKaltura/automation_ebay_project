package report;

import il.co.topq.difido.model.test.ReportElement;

import java.io.File;

public class KReportElement extends ReportElement {
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
