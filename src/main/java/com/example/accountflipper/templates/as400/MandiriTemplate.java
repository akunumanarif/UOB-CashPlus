package com.example.accountflipper.templates.as400;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MandiriTemplate {

    private final String mandiriInput = "src/main/java/com/example/accountflipper/files_sources/Mandiri_input.txt";
    private final String staticDataReference = "src/main/java/com/example/accountflipper/files_sources/STATIC_REFERENCE.txt";
    private final Map<String, String> accountNumberMap = new HashMap<>();

    public void loadStaticData() {
        try (BufferedReader br = new BufferedReader(new FileReader(staticDataReference))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    String oldAccountNumber = parts[2].trim(); // Extracting old account number
                    String newAccountNumber = parts[11].trim(); // Extracting new account number
                    accountNumberMap.put(oldAccountNumber, newAccountNumber);
//                    System.out.println("This is acMAP : ++++++ " + accountNumberMap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GET RESULT AND GENERATE THE AS/400 TEMPLATE (MANDIRI)
    public void flipProcess() {
        loadStaticData();

        try (BufferedReader br = new BufferedReader(new FileReader(mandiriInput));
             BufferedWriter bw = new BufferedWriter(new FileWriter("result.txt"))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (line.startsWith(":86:")) {
                    String extractedAccountNumber = extractAccountNumber(line);
                    String newAccountNumber = accountNumberMap.get(extractedAccountNumber);
                    if (newAccountNumber != null) {
                        // Replace old account number with new account number
                        String updatedLine = line.replace(extractedAccountNumber, newAccountNumber);
                        bw.write(updatedLine);
                        bw.newLine();
                    } else {
                        // Print the original :86: line if no update for account number
                        bw.write(line);
                        bw.newLine();
                    }
                } else {
                    // Print other lines as is
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    // ONLY PRINT OUT THE RESULT NOT MAKING NEW TEMPLATE
//    public void flipProcess() {
//        loadStaticData();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(mandiriInput))) {
//            String line;
//
//            while ((line = br.readLine()) != null) {
//                if (line.startsWith(":86:")) {
//                    String extractedAccountNumber = extractAccountNumber(line);
//                    String newAccountNumber = accountNumberMap.get(extractedAccountNumber);
//                    if (newAccountNumber != null) {
//                        // Replace old account number with new account number
//                        String updatedLine = line.replace(extractedAccountNumber, newAccountNumber);
//                        System.out.println(updatedLine);
//                    } else {
//                        // Print the original :86: line if no update for account number
//                        System.out.println(line);
//                    }
//                } else {
//                    // Print other lines as is
//                    System.out.println(line);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    private String extractAccountNumber(String line) {
        // Assuming the account number is the 15th character onwards in the :86: line
        return line.substring(15, 31).trim();
    }

    public static void main(String[] args) {
        MandiriTemplate mandiriTemplate = new MandiriTemplate();
        mandiriTemplate.flipProcess();
    }

}










