package com.example.accessingdataneo4j;

import java.util.HashMap;
import java.util.Map;

public class FamilyColumnMapping implements ColumnMapping {
    @Override
    public Map<String, String> getColumnPropertyMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("family_ID", "name");  // Имя семьи
        mapping.put("famhx", "history");
        mapping.put("num_het_mut_affected", "numHetMutAffected");
        mapping.put("num_hom_mut_affected", "numHomMutAffected");
        mapping.put("num_het_mut_unaffected", "numHetMutUnaffected");
        mapping.put("num_hom_mut_unaffected", "numHomMutUnaffected");
        mapping.put("num_wildtype_affected", "numWildtypeAffected");
        mapping.put("num_wildtype_unaffected", "numWildtypeUnaffected");
        mapping.put("PMID", "study.id");  // Ensure we map the study ID as well
        // Добавьте другие маппинги здесь
        return mapping;
    }
}
