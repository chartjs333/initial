package com.example.accessingdataneo4j;

import java.util.HashMap;
import java.util.Map;

public class DiseaseColumnMapping implements ColumnMapping {
    @Override
    public Map<String, String> getColumnPropertyMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("disease_abbrev", "abbreviation");
        // Добавьте другие маппинги здесь
        return mapping;
    }
}

