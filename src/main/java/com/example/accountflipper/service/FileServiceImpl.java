package com.example.accountflipper.service;

import com.example.accountflipper.entity.InputEntity;
import com.example.accountflipper.repository.FileRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService{


    @Autowired
    private FileRepository fileRepository;


    @Override
    public boolean hasCSVFormat(MultipartFile file) {
        String type="text/csv";
        if(!type.equals(file.getContentType()))
            return false;
        return true;
    }

    @Override
    public void processAndSaveData(MultipartFile file) {
        try {
            List<InputEntity> inputData = csvToDatabase(file.getInputStream());
            fileRepository.saveAll(inputData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<InputEntity> csvToDatabase(InputStream inputStream) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
            List<InputEntity> inputEntities = new ArrayList<>();
            List<CSVRecord> records = csvParser.getRecords();
            for (CSVRecord csvRecord : records) {
                InputEntity inputEntity = new InputEntity(Long.parseLong(csvRecord.get("Index")), csvRecord.get("AccountNumber"), csvRecord.get("Amount"));
                inputEntities.add(inputEntity);
            }
            return inputEntities;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        return null;
    }
}
