package com.example.accessingdataneo4j.service;

import com.example.accessingdataneo4j.GenericExcelReader;
import com.example.accessingdataneo4j.StudyColumnMapping;
import com.example.accessingdataneo4j.domain.Study;
import com.example.accessingdataneo4j.domain.Disease;
import com.example.accessingdataneo4j.repository.StudyRepository;
import com.example.accessingdataneo4j.repository.DiseaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StudyService {

    private static final Logger log = LoggerFactory.getLogger(StudyService.class);

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private FileUploadService fileUploadService;

    private File excelFile;

    public void setExcelFile(File excelFile) {
        this.excelFile = excelFile;
    }

    public void execute() {
        try {
            // Create a reader for the Excel file
            GenericExcelReader<Study> excelReader = new GenericExcelReader<>(
                    excelFile.getAbsolutePath(),
                    Study.class,
                    new StudyColumnMapping()
            );

            // Read the data from the Excel file
            List<Study> studies = excelReader.readData();
            for (Study study : studies) {
                String studyId = study.getId();
                if (StringUtils.isEmpty(studyId)) {
                    continue;
                }
                processAndStoreStudies(study);
            }

        } catch (Exception e) {
            log.error("Error processing the Excel file: " + excelFile.getAbsolutePath(), e);
        }
    }

    private void processAndStoreStudies(Study study) {
        // Check if Study already exists
        Study existingStudy = studyRepository.findById(study.getId()).orElse(null);
        if (existingStudy == null) {
            study.setCreatedAt(LocalDateTime.now());
            study.setUpdatedAt(LocalDateTime.now());

            // Establish relationships with diseases
            Set<Disease> relatedDiseases = getRelatedDiseases(study);
            study.setDiseases(relatedDiseases);

            studyRepository.save(study);
            log.info("Study created: " + study.getId());
        } else {
            log.info("Study already exists: " + study.getId());

            // Establish relationships with diseases
            Set<Disease> relatedDiseases = getRelatedDiseases(study);

            // Add new diseases to the existing study's relationships
            if (existingStudy.getDiseases() == null) {
                existingStudy.setDiseases(relatedDiseases);
            } else {
                existingStudy.getDiseases().addAll(relatedDiseases);
            }

            existingStudy.setUpdatedAt(LocalDateTime.now());
            studyRepository.save(existingStudy);
            log.info("Study updated with new relationships: " + study.getId());
        }
    }

    private Set<Disease> getRelatedDiseases(Study study) {
        Set<Disease> relatedDiseases = new HashSet<>();
        String diseaseAbbreviation = study.getDiseaseAbbreviation(); // assuming there's a method or field to get the disease abbreviation

        if (!StringUtils.isEmpty(diseaseAbbreviation)) {
            String[] abbreviations = diseaseAbbreviation.split("/");
            for (String abbreviation : abbreviations) {
                Disease disease = diseaseRepository.findByAbbreviation(abbreviation);
                if (disease == null) {
                    disease = new Disease();
                    disease.setAbbreviation(abbreviation);
                    disease.setName("Unknown Disease"); // or use a translation service
                    diseaseRepository.save(disease);
                    log.info("Disease created: " + abbreviation);
                }
                relatedDiseases.add(disease);
            }
        }
        return relatedDiseases;
    }
}
