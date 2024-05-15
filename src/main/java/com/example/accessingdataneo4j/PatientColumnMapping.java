package com.example.accessingdataneo4j;

import java.util.HashMap;
import java.util.Map;

public class PatientColumnMapping implements ColumnMapping {
    @Override
    public Map<String, String> getColumnPropertyMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("id", "id");
        mapping.put("name", "name");
        mapping.put("family_id", "family.id");
        mapping.put("study_id", "study.id");
        // Добавьте другие маппинги здесь
        return mapping;
    }
}
