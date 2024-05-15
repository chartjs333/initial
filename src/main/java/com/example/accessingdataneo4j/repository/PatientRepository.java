package com.example.accessingdataneo4j.repository;

import com.example.accessingdataneo4j.domain.Patient;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends Neo4jRepository<Patient, String> {

    Optional<Patient> findByNameAndFamilyId(String name, String familyId);

    Optional<Patient> findByNameAndFamilyIdAndStudyId(String name, String familyId, String studyId);
}
