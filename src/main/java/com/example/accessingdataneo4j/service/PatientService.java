package com.example.accessingdataneo4j.service;

import com.example.accessingdataneo4j.GenericExcelReader;
import com.example.accessingdataneo4j.PatientColumnMapping;
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
import org.springframework.util.StringUtils;

import java.io.File;
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

    private File excelFile;

    public void setExcelFile(File excelFile) {
        this.excelFile = excelFile;
    }

    public void execute() {
        try {
            // Create a reader for the Excel file
            GenericExcelReader<Patient> excelReader = new GenericExcelReader<>(
                    excelFile.getAbsolutePath(),
                    Patient.class,
                    new PatientColumnMapping()
            );

            // Read the data from the Excel file
            List<Patient> patients = excelReader.readData();
            for (Patient patient : patients) {
                String patientName = patient.getName();
                if (StringUtils.isEmpty(patientName)) {
                    continue;
                }
                String familyId = patient.getFamily().getId();
                String studyId = patient.getStudy().getId();

                // Ensure that the family and study are already present
                Optional<Family> family = familyRepository.findById(familyId);
                Optional<Study> study = studyRepository.findById(studyId);
                if (family.isPresent() && study.isPresent()) {
                    patient.setFamily(family.get());
                    patient.setStudy(study.get());
                    savePatient(patient);
                } else {
                    log.error("Family or Study not found: Family ID=" + familyId + ", Study ID=" + studyId);
                }
            }

        } catch (Exception e) {
            log.error("Error processing the Excel file: " + excelFile.getAbsolutePath(), e);
        }
    }


    public void savePatient(Patient patient) {
        // Установка текущего времени для createdAt и updatedAt
        LocalDateTime now = LocalDateTime.now();
        patient.setCreatedAt(now);
        patient.setUpdatedAt(now);

        // Проверка уникальности пациента внутри семьи
        Optional<Patient> existingPatient = patientRepository.findByNameAndFamilyId(patient.getName(), patient.getFamily().getId());
        if (existingPatient.isPresent()) {
            log.warn("Patient with name " + patient.getName() + " already exists in family " + patient.getFamily().getId());
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
