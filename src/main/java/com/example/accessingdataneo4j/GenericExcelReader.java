package com.example.accessingdataneo4j;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.*;

public class GenericExcelReader<T> {
    private String filepath;
    private Class<T> type;
    private ColumnMapping mapping;

    public GenericExcelReader(String filepath, Class<T> type, ColumnMapping mapping) {
        this.filepath = filepath;
        this.type = type;
        this.mapping = mapping;
    }

    public List<T> readData() throws Exception {
        List<T> results = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filepath);
             Workbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            DataFormatter dataFormatter = new DataFormatter();
            Map<Integer, Method> columnMapping = new HashMap<>();
            Map<String, String> propertyMap = mapping.getColumnPropertyMapping();

            for (Cell cell : headerRow) {
                try {
                    String headerName = dataFormatter.formatCellValue(cell).trim();
                    String propertyName = propertyMap.get(headerName);

                    if (propertyName != null) {
                        String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                        try {
                            Method method = type.getMethod(methodName, String.class);
                            columnMapping.put(cell.getColumnIndex(), method);
                        } catch (NoSuchMethodException e) {
                            System.out.println("No setter for column: " + headerName);
                        }
                    }

                } catch (IllegalStateException e) {
                    System.out.println("Invalid cell content");
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                T instance = type.newInstance();
                for (Map.Entry<Integer, Method> entry : columnMapping.entrySet()) {
                    Cell cell = row.getCell(entry.getKey(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell != null) {
                        String cellValue = dataFormatter.formatCellValue(cell);
                        entry.getValue().invoke(instance, cellValue);
                    }
                }
                results.add(instance);
            }
        }
        return results;
    }
}
