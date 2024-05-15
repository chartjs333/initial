package com.example.accessingdataneo4j;

import java.util.HashMap;
import java.util.Map;

public class GenericTranslationService<K, V> implements TranslationService<K, V> {
    private Map<K, V> translationMap = new HashMap<>();

    @Override
    public void addTranslation(K key, V value) {
        translationMap.put(key, value);
    }

    @Override
    public V getTranslation(K key) {
        return translationMap.getOrDefault(key, null);
    }
}

