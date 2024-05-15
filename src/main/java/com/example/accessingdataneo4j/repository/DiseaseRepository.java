package com.example.accessingdataneo4j.repository;

import java.util.List;

import com.example.accessingdataneo4j.domain.Disease;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface DiseaseRepository extends Neo4jRepository<Disease, String> {
    @Query("MATCH (disease:Disease) WHERE disease.name = $name RETURN disease")
    Disease findByName(String name);
    @Query("MATCH (disease:Disease)-[:PARENT]->(parent:Disease) WHERE parent.name = $name RETURN disease")
    List<Disease> findByParentDiseasesName(String name);

    @Query("MATCH (disease:Disease) WHERE disease.abbreviation = $abbreviation RETURN disease")
    Disease findByAbbreviation(String abbreviation);
}