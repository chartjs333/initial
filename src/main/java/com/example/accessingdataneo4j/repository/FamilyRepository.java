package com.example.accessingdataneo4j.repository;

import com.example.accessingdataneo4j.domain.Family;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.repository.query.Query;

@Repository
public interface FamilyRepository extends Neo4jRepository<Family, String> {

    @Query("MATCH (f:Family)-[:STUDY_FOR]->(s:Study) WHERE f.id = $familyId AND s.id = $studyId RETURN f")
    Family findByIdAndStudyId(String familyId, String studyId);
}
