package com.example.accessingdataneo4j.repository;

import com.example.accessingdataneo4j.domain.Family;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository extends Neo4jRepository<Family, String> {

    Family findByIdAndStudyId(String familyId, String studyId);
}
