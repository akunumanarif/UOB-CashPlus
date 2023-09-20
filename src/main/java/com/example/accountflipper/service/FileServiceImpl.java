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
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Override
    public boolean hasCSVFormat(MultipartFile file) {
        String type = "text/csv";
        return type.equals(file.getContentType());
    }

    @Override
    public void processAndSaveData(MultipartFile inputFile, MultipartFile refFile) {
        try {
            List<InputEntity> inputData = processFiles(inputFile, refFile);
            fileRepository.saveAll(inputData);
        } catch (IOException e) {
            throw new RuntimeException("Error processing and saving data.", e);
        }
    }

    private List<InputEntity> processFiles(MultipartFile inputFile, MultipartFile refFile) throws IOException {
        List<InputEntity> inputEntities = new ArrayList<>();

        List<String> inputRows = readCSVRows(inputFile);
//        System.out.println(inputRows);
        List<String> refRows = readCSVRows(refFile);

        for (int i = 0; i < inputRows.size(); i++) {
            String[] inputRowValues = inputRows.get(i).split(",");
            String[] refRowValues = refRows.get(i).split(",");

            if (refRowValues.length >= 3) {
                String inputAccountNumber = inputRowValues[3].trim();
                Long inputAN = Long.parseLong(inputAccountNumber);

                String refAccountNumber = refRowValues[3].trim();
                Long refAN = Long.parseLong(refAccountNumber);

                String InputAmount = inputRowValues[4].trim();
                String InputAmountResult = InputAmount.substring(0, InputAmount.length() - 2);


                // Check if account numbers match, update the account number if they do
                if (inputAN.equals(refAN)) {
                    String amount = refRowValues[4];  // Update with refFile's third row value
                    String amountFinal = null;
                    Long amountL = null;
                    if (amount.endsWith("]]")) {
                        amountFinal = amount.substring(0, amount.length() - 2);

                    }
                    amountL = Long.parseLong(amountFinal.trim());
                    InputEntity inputEntity = new InputEntity(inputRowValues[1], amountL, InputAmountResult, true);
                    inputEntities.add(inputEntity);
                } else {
                    // If account numbers don't match, use the original account number
                    InputEntity inputEntity = new InputEntity(inputRowValues[0], inputAN, InputAmountResult, false);
                    inputEntities.add(inputEntity);
                }
            }
        }
        System.out.println(inputEntities);
        return inputEntities;
    }


//    private List<InputEntity> processFiles(MultipartFile inputFile, MultipartFile refFile) throws IOException {
//        List<InputEntity> inputEntities = new ArrayList<>();
//
//        List<String> inputRows = readCSVRows(inputFile);
//        List<String> refRows = readCSVRows(refFile);
//
//        for (int i = 0; i < inputRows.size(); i++) {
//            String[] inputRowValues = inputRows.get(i).split(",");
//            String[] refRowValues = refRows.get(i).split(",");
//
//            if (refRowValues.length >= 3) {
//                String accountNumber = inputRowValues[1];
//                String amount = refRowValues[2];  // Update with refFile's third row value
//                System.out.println(accountNumber);
//
//                InputEntity inputEntity = new InputEntity(accountNumber, amount);
//                inputEntities.add(inputEntity);
//            }
//        }
//
//        return inputEntities;
//    }

    private List<String> readCSVRows(MultipartFile file) throws IOException {
        List<String> rows = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            for (CSVRecord csvRecord : csvParser.getRecords()) {
                rows.add(csvRecord.toString());
            }
        }

        return rows;
    }
}





















//package com.example.accountflipper.service;
//
//import com.example.accountflipper.entity.InputEntity;
//import com.example.accountflipper.repository.FileRepository;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVParser;
//import org.apache.commons.csv.CSVRecord;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class FileServiceImpl implements FileService{
//
//
//    @Autowired
//    private FileRepository fileRepository;
//
//
//    @Override
//    public boolean hasCSVFormat(MultipartFile file) {
//        String type="text/csv";
//        if(!type.equals(file.getContentType()))
//            return false;
//        return true;
//    }
//
//    @Override
//    public void processAndSaveData(MultipartFile file) {
//        try {
//            List<InputEntity> inputData = csvToDatabase(file.getInputStream());
//            fileRepository.saveAll(inputData);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private List<InputEntity> csvToDatabase(InputStream inputStream) {
//        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
//            List<InputEntity> inputEntities = new ArrayList<>();
//            List<CSVRecord> records = csvParser.getRecords();
//            for (CSVRecord csvRecord : records) {
//                InputEntity inputEntity = new InputEntity(Long.parseLong(csvRecord.get("Index")), csvRecord.get("AccountNumber"), csvRecord.get("Amount"));
//                inputEntities.add(inputEntity);
//            }
//            return inputEntities;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
////        return null;
//    }
//}
