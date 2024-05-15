package com.example.accessingdataneo4j;

import java.util.HashMap;
import java.util.Map;

public class StudyColumnMapping implements ColumnMapping {
    @Override
    public Map<String, String> getColumnPropertyMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("disease_abbrev", "diseaseAbbreviation");
        mapping.put("PMID", "id");
        // Добавьте другие маппинги здесь
        return mapping;
    }
}

