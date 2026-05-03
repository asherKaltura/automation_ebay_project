package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.ConfigReader;

public class LoginPage extends BasePagePlaywright {

    // ===== LOCATORS =====
    private final Locator signInLink;
    private final Locator emailField;
    private final Locator continueBtn;
    private final Locator passwordField;
    private final Locator signInBtn;

    public LoginPage(Page page) {
        super(page);

        this.emailField = page.locator("input#userid");
        this.continueBtn = page.locator("#signin-continue-btn");
        this.passwordField = page.locator("input#pass");
        this.signInBtn = page.locator("#sgnBt");
        this.signInLink = page.locator("header")
                .locator("a")
                .filter(new Locator.FilterOptions().setHasText("Sign in"))
                .first();
    }

    // ===== OPEN LOGIN =====
    public void openLogin() {
        click(signInLink);
    }

    // ===== LOGIN FLOW =====
    public void login() {
        openLogin();
        String username = ConfigReader.get("login.email");
        String password = ConfigReader.get("login.password");

        // email
        type(emailField, username);
        click(continueBtn);

        // password
        waitForVisible(passwordField);
        fill(passwordField, password);

        click(signInBtn);

        page.waitForLoadState();
    }
}