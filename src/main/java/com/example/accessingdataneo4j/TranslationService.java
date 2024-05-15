package com.example.accessingdataneo4j;

public interface TranslationService<K, V> {
    void addTranslation(K key, V value);
    V getTranslation(K key);
}