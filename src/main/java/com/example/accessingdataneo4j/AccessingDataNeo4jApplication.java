package com.example.accessingdataneo4j;

import java.io.*;
import java.util.List;

import com.example.accessingdataneo4j.repository.StudyRepository;
import com.example.accessingdataneo4j.service.DiseaseService;
import com.example.accessingdataneo4j.repository.DiseaseRepository;
import com.example.accessingdataneo4j.repository.FileUploadRepository;
import com.example.accessingdataneo4j.service.StudyService;
import com.example.accessingdataneo4j.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
public class AccessingDataNeo4jApplication {

	@Value("${xlsx.directory.path}")
	private String excelDirectoryPath;
	private static final Logger log = LoggerFactory.getLogger(AccessingDataNeo4jApplication.class);

	@Autowired
	private DiseaseService diseaseService;

	@Autowired
	private StudyService studyService;

	@Autowired
	private DiseaseRepository diseaseRepository;

	@Autowired
	private FileUploadRepository fileUploadRepository;

	@Autowired
	private StudyRepository studyRepository;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(AccessingDataNeo4jApplication.class, args);
		System.exit(0);
	}

	@Bean
    CommandLineRunner initData(ResourceLoader resourceLoader) {
		return args -> {
			diseaseRepository.deleteAll();
			fileUploadRepository.deleteAll();
            studyRepository.deleteAll();

			// Define the directory containing the Excel files
			File directory = new File(excelDirectoryPath);

			// Filter to select only Excel files
			FilenameFilter excelFileFilter = (dir, name) -> name.toLowerCase().endsWith(".xlsx");

			// Get all Excel files in the directory and its subdirectories
			List<File> excelFiles = FileUtils.getExcelFiles(directory, excelFileFilter);

			// Check if there are any Excel files
            if (excelFiles == null || excelFiles.isEmpty()) {
                log.error("No Excel files found in the directory: " + directory.getAbsolutePath());
				return;
			}

            // Process disease data
			for (File excelFile : excelFiles) {
                log.info("Processing Excel file for diseases: " + excelFile.getAbsolutePath());
				diseaseService.setExcelFile(excelFile);
				diseaseService.execute();
			}

            // Process study data
            for (File excelFile : excelFiles) {
                log.info("Processing Excel file for studies: " + excelFile.getAbsolutePath());
                studyService.setExcelFile(excelFile);
                studyService.execute();
            }
		};
	}
}