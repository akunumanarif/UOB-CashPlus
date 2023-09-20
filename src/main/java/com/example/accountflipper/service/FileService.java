package com.example.accountflipper.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    boolean hasCSVFormat(MultipartFile file);

    void processAndSaveData(MultipartFile inputFile, MultipartFile refFile);
}
