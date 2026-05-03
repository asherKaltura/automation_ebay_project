package utils;

import annotations.TestData;
import enums.DataSource;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FlexibleDataProvider {

    // Cache for data sources (Excel / CSV / JSON)
    private static final Map<String, Object[][]> DATA_CACHE = new ConcurrentHashMap<>();

    @DataProvider(name = "tabularData")
    public static Object[][] getData(Method method, ITestContext context) throws Exception {

        TestData testData = method.getAnnotation(TestData.class);

        // ===== XML PARAMETERS =====
        String xmlFile = context.getCurrentXmlTest().getParameter("dataFile");
        String xmlLimit = context.getCurrentXmlTest().getParameter("limit");
        String xmlSource = context.getCurrentXmlTest().getParameter("source");
        String xmlSheetIndex = context.getCurrentXmlTest().getParameter("sheetIndex");
        String xmlParams = context.getCurrentXmlTest().getParameter("params");

        // ===== RESOLVE VALUES (XML OVERRIDE → ANNOTATION FALLBACK) =====
        String value = (xmlFile != null) ? xmlFile : testData.value();

        int limit = (xmlLimit != null)
                ? Integer.parseInt(xmlLimit)
                : testData.limit();

        int sheetIndex = (xmlSheetIndex != null)
                ? Integer.parseInt(xmlSheetIndex)
                : testData.sheetIndex();

        DataSource source = (xmlSource != null)
                ? DataSource.valueOf(xmlSource.toUpperCase())
                : testData.source();

        String paramsRaw = (xmlParams != null)
                ? xmlParams
                : testData.params();

        Map<String, String> dynamicParams = parseDynamicParams(paramsRaw);

        Object[][] data;

        // ===== SWITCH SOURCE =====
        switch (source) {
            case CSV:
                data = readFromCsv(value, limit, dynamicParams);
                break;

            case EXCEL:
                data = readFromExcel(value, sheetIndex, limit, dynamicParams);
                break;

            case JSON:
                data = readFromJson(value, limit, dynamicParams);
                break;

            case DB:
                data = readFromDB(value, limit, dynamicParams);
                break;

            default:
                throw new IllegalArgumentException("Unknown data source: " + source);
        }

        return data;
    }

    // ---------------- CSV ----------------
    private static Object[][] readFromCsv(String fileName, int limit, Map<String, String> dynamicParams) throws Exception {
        String cacheKey = fileName + limit + dynamicParams;
        if (DATA_CACHE.containsKey(cacheKey)) return DATA_CACHE.get(cacheKey);

        File file = FileUtilities.getResourceFile(fileName);
        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        if (lines.isEmpty()) return new Object[0][0];

        String[] headers = lines.get(0).split(",");
        List<Map<String, String>> rows = new ArrayList<>();

        int count = 0;
        for (int i = 1; i < lines.size(); i++) {
            if (limit > 0 && count >= limit) break;

            String[] values = lines.get(i).split(",");
            Map<String, String> row = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                row.put(headers[j], j < values.length ? values[j] : "");
            }

            if (matchesParams(row, dynamicParams)) {
                rows.add(row);
                count++;
            }
        }

        Object[][] result = convertToDataProvider(rows);
        DATA_CACHE.put(cacheKey, result);
        return result;
    }

    // ---------------- EXCEL ----------------
    private static Object[][] readFromExcel(String fileName, int sheetIndex, int limit, Map<String, String> dynamicParams) throws Exception {
        String cacheKey = fileName + sheetIndex + limit + dynamicParams;
        if (DATA_CACHE.containsKey(cacheKey)) return DATA_CACHE.get(cacheKey);

        File file = FileUtilities.getResourceFile(fileName);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        if (sheet == null) return new Object[0][0];

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return new Object[0][0];

        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) headers.add(cell.getStringCellValue());

        List<Map<String, String>> rows = new ArrayList<>();
        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            if (limit > 0 && count >= limit) break;

            Row row = sheet.getRow(i);
            if (row == null) continue;

            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                map.put(headers.get(j), cell != null ? cell.toString() : "");
            }

            if (matchesParams(map, dynamicParams)) {
                rows.add(map);
                count++;
            }
        }

        workbook.close();
        Object[][] result = convertToDataProvider(rows);
        DATA_CACHE.put(cacheKey, result);
        return result;
    }

    // ---------------- JSON ----------------
    private static Object[][] readFromJson(String fileName, int limit, Map<String, String> dynamicParams) throws Exception {
        String cacheKey = fileName + limit + dynamicParams;
        if (DATA_CACHE.containsKey(cacheKey)) return DATA_CACHE.get(cacheKey);

        File file = FileUtilities.getResourceFile(fileName);
        if (file.length() == 0) return new Object[0][0];

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> rows = new ArrayList<>();

        if (mapper.readTree(file).isArray()) {
            List<Map<String, Object>> jsonList = mapper.readValue(file, List.class);
            rows = filterJsonList(jsonList, dynamicParams, limit);
        } else {
            Map<String, Object> singleObj = mapper.readValue(file, Map.class);
            if (matchesParams(singleObj, dynamicParams)) {
                Map<String, String> map = singleObj.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
                rows.add(map);
            }
        }

        Object[][] result = convertToDataProvider(rows);
        DATA_CACHE.put(cacheKey, result);
        return result;
    }

    private static List<Map<String, String>> filterJsonList(List<Map<String, Object>> jsonList, Map<String, String> dynamicParams, int limit) {
        List<Map<String, String>> rows = new ArrayList<>();
        int count = 0;
        for (Map<String, Object> item : jsonList) {
            if (limit > 0 && count >= limit) break;
            if (matchesParams(item, dynamicParams)) {
                Map<String, String> map = item.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
                rows.add(map);
                count++;
            }
        }
        return rows;
    }

    // ---------------- DB ----------------
    private static Object[][] readFromDB(String query, int limit, Map<String, String> dynamicParams) throws Exception {
        // Example JDBC – replace with your real DB config
        Connection conn = null;///DriverManager.getConnection(
               // "jdbc:mysql://localhost:3306/dbname", "user", "pass"
       // );

        // Replace :param → ? for PreparedStatement
        List<String> keys = new ArrayList<>(dynamicParams.keySet());
        for (String key : keys) query = query.replace(":" + key, "?");

        PreparedStatement ps = conn.prepareStatement(query);
        for (int i = 0; i < keys.size(); i++) {
            ps.setString(i + 1, dynamicParams.get(keys.get(i)));
        }

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData meta = rs.getMetaData();

        List<Map<String, String>> rows = new ArrayList<>();
        int count = 0;
        while (rs.next() && (limit <= 0 || count < limit)) {
            Map<String, String> row = new HashMap<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                row.put(meta.getColumnName(i), rs.getString(i));
            }
            rows.add(row);
            count++;
        }

        rs.close();
        ps.close();
        conn.close();

        return convertToDataProvider(rows);
    }

    // ---------------- Helpers ----------------
    private static boolean matchesParams(Map<?, ?> row, Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!entry.getValue().equals(String.valueOf(row.get(entry.getKey()))))return  false;
        }
        return true;
    }

    private static Map<String, String> parseDynamicParams(String params) {
        Map<String, String> map = new HashMap<>();
        if (params != null && !params.isEmpty()) {
            String[] entries = params.split(",");
            for (String entry : entries) {
                String[] kv = entry.split("=");
                if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
            }
        }
        // Override with system properties
        for (String key : map.keySet()) {
            String sysVal = System.getProperty(key);
            if (sysVal != null) map.put(key, sysVal);
        }
        return map;
    }

    private static Object[][] convertToDataProvider(List<Map<String, String>> rows) {
        Object[][] data = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) data[i][0] = rows.get(i);
        return data;
    }
}
