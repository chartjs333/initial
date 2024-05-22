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
import java.util.Set;

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
                Set<Study> studies = family.getStudies();
                    for (Study study : studies) {
                        Optional<Study> existingStudy = studyRepository.findById(study.getId());
                        if (existingStudy.isPresent()) {
                        family.getStudies().remove(study);
                        family.getStudies().add(existingStudy.get());
                        } else {
                            log.error("Study not found: Study ID=" + study.getId());
                        }
                    }
                    processAndStoreFamily(family);
            }

        } catch (Exception e) {
            log.error("Error processing the Excel file: " + excelFile.getAbsolutePath(), e);
        }
    }

    private void processAndStoreFamily(Family family) {
        for (Study study : family.getStudies()) {
            Family existingFamily = familyRepository.findByIdAndStudyId(family.getId(), study.getId());
            if (existingFamily == null) {
                family.setCreatedAt(LocalDateTime.now());
                family.setUpdatedAt(LocalDateTime.now());
                familyRepository.save(family);
                log.info("Family created: " + family.getId() + " and associated with study " + study.getId());
            } else {
                log.info("Family already exists: " + family.getId() + " in study " + study.getId());
            }
        }
    }
}
