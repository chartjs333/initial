package com.example.accessingdataneo4j.service;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.accessingdataneo4j.*;
import com.example.accessingdataneo4j.domain.Disease;
import com.example.accessingdataneo4j.domain.FileUpload;
import com.example.accessingdataneo4j.repository.DiseaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DiseaseService {
    private static final Logger log = LoggerFactory.getLogger(DiseaseService.class);

    private File excelFile;
    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private PatientService patientService;

    private GenericTranslationService<String, String> translationService = new GenericTranslationService<>();

    public void setExcelFile(File excelFile) {
        this.excelFile = excelFile;
    }

    // This function takes control instead of Constructor
    public void initTranslationService() {
        // Create a translation service and add a translation
        translationService.addTranslation("PARK", "Parkinsonism");
    }

    public void execute() {
        try {
            // Create a reader for the Excel file
            GenericExcelReader<Disease> excelReader = new GenericExcelReader<>(
                    excelFile.getAbsolutePath(),
                    Disease.class,
                    new DiseaseColumnMapping()
            );

            // Read the data from the Excel file
            List<Disease> diseases = excelReader.readData();
            Set<Disease> allDiseases = new HashSet<>();
            for (Disease disease : diseases) {
                String diseaseAbbreviation = disease.getAbbreviation();
                if (StringUtils.isEmpty(diseaseAbbreviation)) {
                    continue;
                }
                allDiseases.addAll(processAndStoreDiseases(disease));
            }

            // Save file upload information
            FileUpload fileUpload = new FileUpload();
            fileUpload.setTsvFileFileName(excelFile.getAbsolutePath());
            fileUpload.setTsvFileContentType("application/vnd.ms-excel"); // Установите правильный MIME-тип
            fileUpload.setTsvFileFileSize((int) excelFile.length());

            fileUploadService.saveFileUpload(fileUpload, allDiseases);
        } catch (Exception e) {
            log.error("Error processing the Excel file: " + excelFile.getAbsolutePath(), e);
        }
    }

    private Set<Disease> processAndStoreDiseases(Disease disease) {
        //check if disease abbreviation is valid
        Set<Disease> result = new HashSet<>();
        String diseaseAbbreviation = disease.getAbbreviation();
        if (StringUtils.isEmpty(diseaseAbbreviation)) {
            return result;
        }
        //if it contains digitals then remove them and check if the abbreviation is valid
        if (diseaseAbbreviation.matches(".*\\d.*")) {
            diseaseAbbreviation = diseaseAbbreviation.replaceAll("\\d", "");
            if (StringUtils.isEmpty(diseaseAbbreviation)) {
                return result;
            }
        }
        //split the abbreviation if it contains '/'
        if (diseaseAbbreviation.contains("/")) {
            String[] abbreviations = diseaseAbbreviation.split("/");
            String parentDiseaseAbbreviation = "";
            for (String abbreviation : abbreviations) {
                //check if parent disease is presented otherwise create a new parent
                Disease parentDisease = diseaseRepository.findByAbbreviation(parentDiseaseAbbreviation);
                if (parentDisease == null) {
                    parentDisease = new Disease();
                    parentDisease.setAbbreviation(parentDiseaseAbbreviation);
                    parentDisease.setName(translationService.getTranslation(parentDiseaseAbbreviation));
                    //write info log here
                    log.info("Parent disease created: " + parentDiseaseAbbreviation);
                    result.add(diseaseRepository.save(parentDisease));
                } else {
                    result.add(parentDisease);
                }
                // find the disease with the abbreviation
                Disease newDisease = diseaseRepository.findByAbbreviation(abbreviation);
                if (newDisease == null) {
                    newDisease = new Disease();
                    newDisease.setParentDiseases(new HashSet<>(List.of(parentDisease)));
                    newDisease.setAbbreviation(abbreviation);
                    newDisease.setName(translationService.getTranslation(abbreviation));
                    //write info log here
                    log.info("Disease created: " + abbreviation);
                    result.add(diseaseRepository.save(newDisease));
                } else {
                    //add parent disease to the list of parent diseases
                    log.info("Parent disease added to the disease: " + abbreviation);
                    newDisease.getParentDiseases().add(parentDisease);
                    result.add(diseaseRepository.save(newDisease));
                }
            }
        }
        // Process each disease
        if (diseaseRepository.findByAbbreviation(disease.getAbbreviation()) == null) {
            disease.setName(translationService.getTranslation(disease.getAbbreviation()));
            log.info("Disease created: " + disease.getAbbreviation());
            result.add(diseaseRepository.save(disease));
        } else {
            log.info("Disease already exists: " + disease.getAbbreviation());
            result.add(diseaseRepository.findByAbbreviation(disease.getAbbreviation()));
        }
        return result;
    }
}