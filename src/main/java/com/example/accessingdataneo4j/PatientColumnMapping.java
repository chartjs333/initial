package com.example.accessingdataneo4j;

import java.util.HashMap;
import java.util.Map;

public class PatientColumnMapping implements ColumnMapping {
    @Override
    public Map<String, String> getColumnPropertyMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("individual_ID", "name");  // Имя пациента
        mapping.put("family_ID", "family.id");
        mapping.put("disease_abbrev", "study.diseaseAbbreviation");
        mapping.put("PMID", "study.id");  // Ensure we map the study ID as well
        // Добавьте другие маппинги здесь
        return mapping;
    }
}
