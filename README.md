# eBay Automation E2E Project

Playwright-based Java automation suite for eBay — search, cart, and assertion flows with Data-Driven support (CSV/Excel), Allure reporting, and a clean POM architecture.

---

---

## Setup

### 1. Clone the repository
```bash
git clone https://github.com/asherKaltura/automation_ebay_project
cd automation_ebay_project
```



### 2 Configure credentials
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

## Running the Tests

### Run all tests (TestNG suite)
```bash
mvn test
```

### Run with debug/slow-motion (500ms between steps)
```bash
mvn test -Ddebug=true
```

### Run headless (for CI)
```bash
mvn test -Dheadless=true
```

### Run specific test class
```bash
mvn test -Dtest=E2ETest
mvn test -Dtest=E2EDataDrivenTest
```

### Run via TestNG XML
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng/testng.xml
```

---

## Generating Allure Report

After a test run, Allure results are written to `target/allure-results/`.

```maven

mvn allure:serve
```

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
│   └── utils/
│       ├── AssertUtils          # assertCartTotalNotExceeds
│       ├── CleanupManager       #not in use
│       ├── ConfigReader         # reads config.properties
│       ├── DriverManager        #not in use
│       ├── FakerUtils           #not in use
│       ├── FileUtilities
│       └── FlexibleDataProvider # TestNG DataProvider: CSV / Excel / JSON
│
└── test/java/tests/
    ├── AbstractTestCase         # base lifecycle hooks (Difido)
    ├── BaseTest                 # Playwright setup/teardown per test method
    ├── E2ETest                  # simple hard-coded E2E test
    ├── E2EDataDrivenTest        # data-driven test (reads from CSV/Excel)
    └── DataProviderSample       # example usage of FlexibleDataProvider

src/test/resources/
    ├── config.properties
    ├── data/
    │   ├── csvData.csv          # query, maxPrice, limit
    │   └── excelData.xlsx
    └── testng/tests.xml
```

### Design Patterns

- **Page Object Model (POM):** every screen is a dedicated class under `pages/`
- **Flow Layer:** `ShoppingFlow` orchestrates multi-page steps, keeping tests clean
- **Data-Driven:** `FlexibleDataProvider` + `@TestData` annotation feeds test data from CSV/Excel/JSON
- **Factory:** `BrowserFactory` centralizes browser creation; switching browser type requires only a config change

---

## Test Data
`src/test/resources/data/jsonData.json`:
[
{
"query": "shoes",
"maxPrice": 220,
"limit": 5
},
{
"query": "laptop",
"maxPrice": 1000,
"limit": 3
},
{
"query": "phone",
"maxPrice": 500,
"limit": 2
}
]

`src/test/resources/data/csvData.csv`:
```
query,maxPrice,limit
shoes,220,5
laptop,1000,3
phone,500,2
```

To add more test cases, append rows to the CSV — no code changes required.

---

## Limitations & Assumptions


## CI / Jenkins

A `JenkinsFile` is included at the project root for pipeline integration.  
Key Maven flags for CI runs:

```bash
mvn test -Dheadless=true -Dsurefire.suiteXmlFiles=src/test/resources/testng/tests.xml
```

---

## Report Outputs

| Report | Location |
|--------|----------|
| Allure raw results | `target/allure-results/` |
| Difido HTML report | `test-output/difido/current/index.html` |
| Screenshots | embedded in Allure steps (per item added to cart) |
