package com.example.accountflipper.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



@Service
public class MandiriTemplateService {

    private final Map<String, String> accountNumberMap = new HashMap<>();

    public String processTemplate(MultipartFile mandiriFile, MultipartFile staticDataFile,
                                  int oldAccountNumberPosition, int newAccountNumberPosition,
                                  int lineSubstringStart, int lineSubstringEnd) throws IOException {
        loadStaticData(staticDataFile, oldAccountNumberPosition, newAccountNumberPosition);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        String outputFileName = "output_" + formatter.format(date) + ".txt";

        Path outputPath = Paths.get("output_dir/" + outputFileName);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(mandiriFile.getInputStream()));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath.toFile()))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(":86:")) {
                    String extractedAccountNumber = extractAccountNumber(line, lineSubstringStart, lineSubstringEnd);
                    String newAccountNumber = accountNumberMap.get(extractedAccountNumber);
                    if (newAccountNumber != null) {
                        // Replace old account number with new account number
                        String updatedLine = line.replace(extractedAccountNumber, newAccountNumber);
                        bw.write(updatedLine);
                        bw.newLine();
                    } else {
                        // Keep the original :86: line if no update for account number
                        bw.write(line);
                        bw.newLine();
                    }
                } else {
                    // Keep other lines as is
                    bw.write(line);
                    bw.newLine();
                }
            }
        }

        return outputFileName;
    }

    private void loadStaticData(MultipartFile staticDataFile, int oldAccountNumberPosition, int newAccountNumberPosition) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(staticDataFile.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");                if (parts.length >= newAccountNumberPosition) {
                    String oldAccountNumber = parts[oldAccountNumberPosition].trim(); // Extracting old account number
                    String newAccountNumber = parts[newAccountNumberPosition].trim(); // Extracting new account number
                    accountNumberMap.put(oldAccountNumber, newAccountNumber);
                }
            }
        }
    }

    private String extractAccountNumber(String line, int start, int end) {
        return line.substring(start, end).trim();
    }
}


















//@Service
//public class MandiriTemplateService {
//
//    @Value("${outputFilePath}")
//    private String outputFilePath;
//
//    private final Map<String, String> accountNumberMap = new HashMap<>();
//
//    public String processTemplate(MultipartFile mandiriFile, MultipartFile staticDataFile) throws IOException {
//        loadStaticData(staticDataFile);
//
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        Date date = new Date();
//        String outputFileName = "output_" + formatter.format(date) + ".txt";
//        Path outputPath = Paths.get(outputFilePath, outputFileName);
//
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(mandiriFile.getInputStream()));
//             BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
//
//            String line;
//            while ((line = br.readLine()) != null) {
//                if (line.startsWith(":86:")) {
//                    String extractedAccountNumber = extractAccountNumber(line);
//                    String newAccountNumber = accountNumberMap.get(extractedAccountNumber);
//                    if (newAccountNumber != null) {
//                        // Replace old account number with new account number
//                        String updatedLine = line.replace(extractedAccountNumber, newAccountNumber);
//                        bw.write(updatedLine);
//                        bw.newLine();
//                    } else {
//                        // Print the original :86: line if no update for account number
//                        bw.write(line);
//                        bw.newLine();
//                    }
//                } else {
//                    // Print other lines as is
//                    bw.write(line);
//                    bw.newLine();
//                }
//            }
//        }
//
//        return outputPath.toString();
//    }
//
//    private void loadStaticData(MultipartFile staticDataFile) throws IOException {
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(staticDataFile.getInputStream()))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split(";");
//                if (parts.length >= 3) {
//                    String oldAccountNumber = parts[2].trim(); // Extracting old account number
//                    String newAccountNumber = parts[11].trim(); // Extracting new account number
//                    accountNumberMap.put(oldAccountNumber, newAccountNumber);
//                }
//            }
//        }
//    }
//
//    private String extractAccountNumber(String line) {
//        // Assuming the account number is the 15th character onwards in the :86: line
//        return line.substring(15, 31).trim();
//    }
//}

