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
    private Map<String, Class<?>> classCache;

    public GenericExcelReader(String filepath, Class<T> type, ColumnMapping mapping) {
        this.filepath = filepath;
        this.type = type;
        this.mapping = mapping;
        this.classCache = new HashMap<>();
        initializeClassCache();
    }

    private void initializeClassCache() {
        classCache.put("study", com.example.accessingdataneo4j.domain.Study.class);
        classCache.put("family", com.example.accessingdataneo4j.domain.Family.class);
        // Add other classes as needed
    }

    public List<T> readData() throws Exception {
        List<T> results = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filepath);
             Workbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            DataFormatter dataFormatter = new DataFormatter();
            Map<Integer, List<Method>> columnMapping = new HashMap<>();
            Map<String, String> propertyMap = mapping.getColumnPropertyMapping();

            for (Cell cell : headerRow) {
                try {
                    String headerName = dataFormatter.formatCellValue(cell).trim();
                    String propertyName = propertyMap.get(headerName);

                    if (propertyName != null) {
                        String[] nestedProperties = propertyName.split("\\.");
                        List<Method> methods = new ArrayList<>();

                        Class<?> currentClass = type;
                        for (String prop : nestedProperties) {
                            String methodName = "set" + prop.substring(0, 1).toUpperCase() + prop.substring(1);
                            Method method = findMethod(currentClass, methodName);
                            if (method == null) {
                                Class<?> nestedClass = getNestedClass(currentClass, prop);
                                method = currentClass.getMethod(methodName, nestedClass);
                        }
                            methods.add(method);
                            currentClass = method.getParameterTypes()[0];
                        }

                        columnMapping.put(cell.getColumnIndex(), methods);
                    }

                } catch (IllegalStateException e) {
                    System.out.println("Invalid cell content");
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                T instance = type.newInstance();
                for (Map.Entry<Integer, List<Method>> entry : columnMapping.entrySet()) {
                    Cell cell = row.getCell(entry.getKey(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell != null) {
                        String cellValue = dataFormatter.formatCellValue(cell);
                        setNestedProperty(instance, entry.getValue(), cellValue);
                    }
                }
                results.add(instance);
            }
        }
        return results;
    }

    private Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    private void setNestedProperty(Object instance, List<Method> methods, String value) throws Exception {
        Object currentObject = instance;
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            if (i < methods.size() - 1) {
                Object nextObject = getNestedObject(currentObject, method);
                method.invoke(currentObject, nextObject);
                currentObject = nextObject;
            } else {
                if (Set.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    addToSet(currentObject, method, value);
                } else {
                Object paramValue = convertValue(method.getParameters()[0].getType(), value);
                method.invoke(currentObject, paramValue);
            }
        }
    }
    }

    private void addToSet(Object currentObject, Method method, String value) throws Exception {
        Class<?> nestedClass = method.getParameterTypes()[0];
        Method getMethod = findMethod(currentObject.getClass(), "get" + method.getName().substring(3));
        Set<Object> set = (Set<Object>) getMethod.invoke(currentObject);
        if (set == null) {
            set = new HashSet<>();
            method.invoke(currentObject, set);
        }
        Object nestedObject = convertValue(nestedClass, value);
        set.add(nestedObject);
    }

    private Object getNestedObject(Object currentObject, Method method) throws Exception {
        Object nestedObject = method.getParameterTypes()[0].newInstance();
        for (Method m : currentObject.getClass().getMethods()) {
            if (m.getName().equals("get" + method.getName().substring(3)) && m.getReturnType().equals(method.getParameterTypes()[0])) {
                Object existingObject = m.invoke(currentObject);
                if (existingObject != null) {
                    nestedObject = existingObject;
                }
                break;
            }
        }
        return nestedObject;
    }

    private Class<?> getNestedClass(Class<?> currentClass, String property) throws ClassNotFoundException {
        if (classCache.containsKey(property)) {
            return classCache.get(property);
        }
        String packageName = currentClass.getPackageName();
        String className = packageName + "." + property.substring(0, 1).toUpperCase() + property.substring(1);
        Class<?> nestedClass = Class.forName(className);
        classCache.put(property, nestedClass);
        return nestedClass;
    }

    private Object convertValue(Class<?> targetType, String value) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == Date.class) {
            // Add your date parsing logic here
            return new Date(); // Example placeholder
        }
        // Add other type conversions as needed
        throw new IllegalArgumentException("Unsupported target type: " + targetType.getName());
    }
}
