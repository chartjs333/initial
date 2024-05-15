package com.example.accessingdataneo4j.service;

import com.example.accessingdataneo4j.domain.Disease;
import com.example.accessingdataneo4j.domain.FileUpload;
import com.example.accessingdataneo4j.repository.FileUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    private FileUploadRepository fileUploadRepository;

    public void saveFileUpload(FileUpload fileUpload, Set<Disease> diseases) {
        // Установка текущего времени для createdAt и updatedAt
        LocalDateTime now = LocalDateTime.now();
        fileUpload.setCreatedAt(now);
        fileUpload.setUpdatedAt(now);

        // Установка отношений с Disease
        fileUpload.setDiseases(diseases);

        // Сохранение FileUpload
        fileUploadRepository.save(fileUpload);
        log.info("File upload saved: " + fileUpload.getTsvFileFileName());
    }
}
