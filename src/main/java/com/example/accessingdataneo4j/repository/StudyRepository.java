package com.example.accessingdataneo4j.repository;

import com.example.accessingdataneo4j.domain.Study;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends Neo4jRepository<Study, String> {
}
