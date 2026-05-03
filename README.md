# eBay Automation E2E Project

Playwright-based Java automation suite for eBay — search, cart, and assertion flows with Data-Driven support (CSV/Excel/JSON), Allure reporting, and a clean POM architecture.

---

## Setup

### 1. Clone the repository
```bash
git clone https://github.com/asherKaltura/automation_ebay_project
cd automation_ebay_project
```

### 2. Configure credentials
Edit `src/test/resources/config.properties`:
```properties
login.email=your_ebay_email@example.com
login.password=your_password
browser=chrome          # chrome | firefox | webkit
headless=false          # true for CI
base.url=https://www.ebay.com
cart.url=https://cart.ebay.com
loginUrl=https://signin.ebay.com
```

> ⚠️ **Note:** Login credentials are used for the `LoginPage` flow. If running as a guest, the search and cart flows still work but cart totals depend on session state.

---

## Environments

The project supports multiple environments via separate properties files under `src/test/resources/`:

| File | Environment | Headless |
|------|-------------|----------|
| `config.properties` | default (local) | false |
| `config-dev.properties` | development | false |
| `config-staging.properties` | staging | true |
| `config-prod.properties` | production | true |

`ConfigReader` loads the correct file automatically based on the active profile.  
In production, sensitive values (email, password) are read from **ENV Variables** instead of the properties file.

### ENV Variables (override any config value automatically)

| ENV Variable | Config key |
|-------------|------------|
| `BASE_URL` | `base.url` |
| `CART_URL` | `cart.url` |
| `LOGIN_EMAIL` | `login.email` |
| `LOGIN_PASSWORD` | `login.password` |
| `BROWSER` | `browser` |
| `HEADLESS` | `headless` |

Priority order: **ENV Variable → System Property (`-Dkey`) → config file**

---

## Running the Tests

### Run all tests (TestNG suite)
```bash
mvn clean install test
```

### Run with debug/slow-motion (500ms between steps)
```bash
mvn clean install test -P debug
```

### Run headless (for CI)
```bash
mvn clean install test -P ci
```

### Run specific test class
```bash
mvn clean install test -Dtest=E2ETest
```

### Run via TestNG XML
```bash
mvn clean install test -DsuiteXmlFile=src/test/resources/testng/testng.xml
```

---

## Profiles

| Profile | Command | Headless | ENV loaded |
|---------|---------|----------|------------|
| `local` *(default)* | `mvn clean install test` | false | `config.properties` |
| `dev` | `mvn clean install test -P dev` | false | `config-dev.properties` |
| `staging` | `mvn clean install test -P staging` | true | `config-staging.properties` |
| `prod` | `mvn clean install test -P prod` | true | `config-prod.properties` + ENV |
| `ci` | `mvn clean install test -P ci` | true | `config-staging.properties` |
| `debug` | `mvn clean install test -P debug` | false | `config.properties` + slow-motion |

---

## Generating Allure Report

After a test run, Allure results are written to `target/allure-results/`.

```bash
mvn allure:serve
```

---

## Difido Report

Difido report is generated **automatically** at the end of every test run — no additional command required.

The report is saved to:
```
test-output/difido/current/index.html
```

Open it directly in any browser after the tests finish.

---

## Project Architecture

```
src/
├── main/java/
│   ├── annotations/       # @TestData – marks data-driven test metadata
│   ├── assertion/         # KAssertion, AssertTrue – custom soft/hard assertion wrappers
│   ├── enums/             # DataSource (CSV/EXCEL/JSON), TextColor, TextStyle
│   ├── factory/           # BrowserFactory – creates Playwright Browser by config
│   ├── flows/             # ShoppingFlow – high-level orchestration (search → cart → assert)
│   ├── pages/             # Page Object Model classes:
│   │   ├── BasePagePlaywright   # shared helpers: click, navigate, getText, takeScreenshot
│   │   ├── HomePage
│   │   ├── LoginPage
│   │   ├── SearchResultsPage    # searchItemsByNameUnderPrice + paging
│   │   ├── ProductPage          # addItemsToCart + variant dropdown handling
│   │   └── CartPage             # openCart + getCartTotal
│   ├── report/            # KReportManager / Difido report integration
│   ├── test/
│   │   └── AbstractTestCase     # base lifecycle hooks (Difido)
│   └── utils/
│       ├── AssertUtils          # assertCartTotalNotExceeds
│       ├── CleanupManager       # not in use
│       ├── ConfigReader         # reads config-{env}.properties with ENV fallback
│       ├── DriverManager        # not in use
│       ├── FakerUtils           # not in use
│       ├── FileUtilities
│       └── FlexibleDataProvider # TestNG DataProvider: CSV / Excel / JSON
│
└── test/java/tests/
    ├── BaseTest                 # Playwright setup/teardown per test method
    └── E2ETest                  # data-driven E2E test (reads from CSV/Excel/JSON)

src/test/resources/
    ├── config.properties
    ├── config-dev.properties
    ├── config-staging.properties
    ├── config-prod.properties
    ├── data/
    │   ├── csvData.csv          # query, maxPrice, limit
    │   ├── excelData.xlsx
    │   └── jsonData.json
    └── testng/testng.xml
```

### Design Patterns

- **Page Object Model (POM):** every screen is a dedicated class under `pages/`
- **Flow Layer:** `ShoppingFlow` orchestrates multi-page steps, keeping tests clean
- **Data-Driven:** `FlexibleDataProvider` + `@TestData` annotation feeds test data from CSV/Excel/JSON
- **Factory:** `BrowserFactory` centralizes browser creation; switching browser type requires only a config change
- **Environment Config:** `ConfigReader` supports multiple environments with ENV Variable override

---

## Test Data

`src/test/resources/data/jsonData.json`:
```json
[
  { "query": "shoes",  "maxPrice": 220,  "limit": 5 },
  { "query": "laptop", "maxPrice": 1000, "limit": 3 },
  { "query": "phone",  "maxPrice": 500,  "limit": 2 }
]
```

`src/test/resources/data/csvData.csv`:
```
query,maxPrice,limit
shoes,220,5
laptop,1000,3
phone,500,2
```

To add more test cases, append rows to the CSV or JSON — no code changes required.

---

## CI / GitHub Actions

A `maven.yml` workflow is included under `.github/workflows/`.  
Runs automatically on push/PR to `main` or `master`, or manually via "Run workflow".

```bash
mvn clean install test -P ci
```

## CI / Jenkins

A `JenkinsFile` is included at the project root for pipeline integration.

```bash
mvn clean install test -P ci -Dsurefire.suiteXmlFiles=src/test/resources/testng/testng.xml
```

---

## Report Outputs

| Report | Location |
|--------|----------|
| Allure raw results | `target/allure-results/` |
| Difido HTML report | `test-output/difido/current/index.html` |
| Screenshots | embedded in Allure steps (per item added to cart) |