package com.example.accessingdataneo4j.service;

import com.example.accessingdataneo4j.GenericExcelReader;
import com.example.accessingdataneo4j.FamilyColumnMapping;
import com.example.accessingdataneo4j.domain.Family;
import com.example.accessingdataneo4j.domain.Study;
import com.example.accessingdataneo4j.repository.FamilyRepository;
import com.example.accessingdataneo4j.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FamilyService {

    private static final Logger log = LoggerFactory.getLogger(FamilyService.class);

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
            GenericExcelReader<Family> excelReader = new GenericExcelReader<>(
                    excelFile.getAbsolutePath(),
                    Family.class,
                    new FamilyColumnMapping()
            );

            // Read the data from the Excel file
            List<Family> families = excelReader.readData();
            for (Family family : families) {
                String familyId = family.getId();
                if (StringUtils.isEmpty(familyId)) {
                    continue;
                }
                String studyId = family.getStudy().getId();

                // Ensure that the study is already present
                Optional<Study> study = studyRepository.findById(studyId);
                if (study.isPresent()) {
                    family.setStudy(study.get());
                    processAndStoreFamily(family);
                } else {
                    log.error("Study not found: Study ID=" + studyId);
                }
            }

        } catch (Exception e) {
            log.error("Error processing the Excel file: " + excelFile.getAbsolutePath(), e);
        }
    }

    private void processAndStoreFamily(Family family) {
        // Ensure the family is unique within the context of the study
        family.setStudy(family.getStudy());

        // Check if Family already exists within the context of the study
        Family existingFamily = familyRepository.findByIdAndStudyId(family.getId(), family.getStudy().getId());
        if (existingFamily == null) {
            family.setCreatedAt(LocalDateTime.now());
            family.setUpdatedAt(LocalDateTime.now());
            familyRepository.save(family);
            log.info("Family created: " + family.getId() + " and associated with study " + family.getStudy().getId());
        } else {
            log.info("Family already exists: " + family.getId() + " in study " + family.getStudy().getId());
        }
    }
}
