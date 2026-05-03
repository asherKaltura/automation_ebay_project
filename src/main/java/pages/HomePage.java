package pages;

import com.microsoft.playwright.Page;
import utils.ConfigReader;

public class HomePage extends BasePagePlaywright {

    public HomePage(Page page) {
        super(page);
    }

    // ===== OPEN PAGE =====
    public void open() {
        String url = ConfigReader.get("baseUrl");
        navigate(url);
    }

    // ===== OPEN WITH KEY (OPTIONAL FLEX) =====
    public void open(String urlKey) {
        String url = ConfigReader.get(urlKey);
        navigate(url);
    }
}