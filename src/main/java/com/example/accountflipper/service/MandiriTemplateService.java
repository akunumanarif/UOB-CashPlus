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

    public Map<String, Object> processTemplate(
            MultipartFile mandiriFile, MultipartFile staticDataFile,
            int oldAccountNumberPosition, int newAccountNumberPosition,
            int lineSubstringStart, int lineSubstringEnd) throws IOException {

        loadStaticData(staticDataFile, oldAccountNumberPosition, newAccountNumberPosition);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        String outputFileName = "output_mandiri_" + formatter.format(date) + ".txt";
        Path outputPath = Paths.get("output_dir/" + outputFileName);


        int flippedAccounts = 0;
        int nonFlippedAccounts = -1;

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
                        flippedAccounts++;
                    } else {
                        // Keep the original :86: line if no update for account number
                        bw.write(line);
                        bw.newLine();
                        nonFlippedAccounts++;
                    }
                } else {
                    // Keep other lines as is
                    bw.write(line);
                    bw.newLine();

                }
            }
        }
        int totalRecords = flippedAccounts + nonFlippedAccounts;
        // Construct the response
        Map<String, Object> response = new HashMap<>();
        response.put("outputUrl", "localhost:8081/api/mandiri/download/" + outputFileName);
        response.put("totalRecords", totalRecords);
        response.put("flippedAccount", flippedAccounts);
        response.put("nonFlippedAccount", nonFlippedAccounts);
        response.put("message", "Success");
        response.put("statusCode", 200);

        return response;
    }



    private void loadStaticData(MultipartFile staticDataFile, int oldAccountNumberPosition, int newAccountNumberPosition) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(staticDataFile.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= newAccountNumberPosition) {
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



