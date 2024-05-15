package com.example.accessingdataneo4j.service;

import com.example.accessingdataneo4j.domain.Patient;
import com.example.accessingdataneo4j.domain.Family;
import com.example.accessingdataneo4j.domain.Study;
import com.example.accessingdataneo4j.repository.PatientRepository;
import com.example.accessingdataneo4j.repository.FamilyRepository;
import com.example.accessingdataneo4j.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Transactional
    public void savePatient(Patient patient, String familyId, String studyId) {
        // Установка текущего времени для createdAt и updatedAt
        LocalDateTime now = LocalDateTime.now();
        patient.setCreatedAt(now);
        patient.setUpdatedAt(now);

        // Установка отношений с Family
        Optional<Family> familyOptional = familyRepository.findById(familyId);
        if (familyOptional.isPresent()) {
            patient.setFamily(familyOptional.get());
        } else {
            log.warn("Family with ID " + familyId + " not found");
        }

        // Установка отношений с Study
        Optional<Study> studyOptional = studyRepository.findById(studyId);
        if (studyOptional.isPresent()) {
            patient.setStudy(studyOptional.get());
        } else {
            log.warn("Study with ID " + studyId + " not found");
        }

        // Проверка уникальности пациента
        Optional<Patient> existingPatient = patientRepository.findByNameAndFamilyIdAndStudyId(patient.getName(), familyId, studyId);
        if (existingPatient.isPresent()) {
            log.warn("Patient with name " + patient.getName() + " already exists in family " + familyId + " and study " + studyId);
            return;
        }

        // Сохранение Patient
        patientRepository.save(patient);
        log.info("Patient saved: " + patient.getName());
    }

    public List<Patient> findAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> findPatientById(String id) {
        return patientRepository.findById(id);
    }

    public void deletePatient(String id) {
        patientRepository.deleteById(id);
        log.info("Patient with ID " + id + " deleted");
    }
}
