package com.example.accountflipper.service;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class BNITemplateService {

    private final Map<String, String> accountNumberMap = new HashMap<>();
    private Logger logger;

    public Map<String, Object> processTemplate(
            MultipartFile bniFile, MultipartFile staticDataFile,
            int oldAccountNumberPosition, int newAccountNumberPosition,
            int accountNumberPosition) throws IOException {

        loadStaticData(staticDataFile, oldAccountNumberPosition, newAccountNumberPosition, accountNumberPosition);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        String outputFileName = "output_bni_" + formatter.format(date) + ".csv";
        Path outputPath = Paths.get("output_dir/" + outputFileName);

        int flippedAccounts = 0;
        int nonFlippedAccounts = 0;


        try (BufferedReader br = new BufferedReader(new InputStreamReader(bniFile.getInputStream()));
             FileWriter fw = new FileWriter(outputPath.toFile())) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("\"2;")) {
                    String extractedAccountNumber = extractAccountNumber(line, accountNumberPosition);
                    String newAccountNumber = accountNumberMap.get(extractedAccountNumber);
                    if (newAccountNumber != null) {
                        flippedAccounts++;
                        // Replace old account number with new account number
                        String[] updatedLineArray = line.split(";");
                        updatedLineArray[accountNumberPosition] = newAccountNumber;
                        fw.write(String.join(";", updatedLineArray) + "\n");
                    } else {
                        nonFlippedAccounts++;
                        // Keep the original line if no update for account number
                        fw.write(line + "\n");
                    }
                } else {
                    // Keep other lines as is
                    fw.write(line + "\n");
                }
            }
        }

        int totalRecords = flippedAccounts + nonFlippedAccounts;
        // Construct the response
        Map<String, Object> response = new HashMap<>();
        response.put("outputUrl", "URL to download the output file");
        response.put("totalRecords", totalRecords);
        response.put("flippedAccount", flippedAccounts);
        response.put("nonFlippedAccount", nonFlippedAccounts);
        response.put("message", "Success");
        response.put("statusCode", 200);

        return response;
    }

    private void loadStaticData(MultipartFile staticDataFile, int oldAccountNumberPosition, int newAccountNumberPosition, int accountNumberPosition) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(staticDataFile.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length > accountNumberPosition) {
                    String oldAccountNumber = parts[oldAccountNumberPosition].trim(); // Extracting old account number
                    String newAccountNumber = parts[newAccountNumberPosition].trim(); // Extracting new account number
                    accountNumberMap.put(oldAccountNumber, newAccountNumber);
                }
            }
        }
    }

    private String extractAccountNumber(String line, int accountNumberPosition) {
        String[] parts = line.split(";");
        if (parts.length > accountNumberPosition) {
            return parts[accountNumberPosition].trim();
        }
        return "";
    }
}
