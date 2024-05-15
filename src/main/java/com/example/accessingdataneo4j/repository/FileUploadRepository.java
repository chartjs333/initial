package com.example.accessingdataneo4j.repository;

import com.example.accessingdataneo4j.domain.FileUpload;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends Neo4jRepository<FileUpload, String> {
    // Дополнительные методы запросов при необходимости
}