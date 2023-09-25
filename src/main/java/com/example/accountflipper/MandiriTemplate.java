package com.example.accountflipper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MandiriTemplate {

    private final String mandiriInput = "src/main/java/com/example/accountflipper/Mandiri_input.txt";
    private final String staticDataReference = "src/main/java/com/example/accountflipper/STATIC_REFERENCE.txt";
    private final Map<String, String> accountNumberMap = new HashMap<>();

    public void loadStaticData() {
        try (BufferedReader br = new BufferedReader(new FileReader(staticDataReference))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    String oldAccountNumber = parts[2].trim(); // Extracting old account number
                    System.out.println("Old account Number is : " + oldAccountNumber);
                    String newAccountNumber = parts[11].trim(); // Extracting new account number
                    System.out.println("New account Number is : " + newAccountNumber);
                    accountNumberMap.put(oldAccountNumber, newAccountNumber);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flipProcess() {
        loadStaticData();

        try (BufferedReader br = new BufferedReader(new FileReader(mandiriInput))) {
            String line;
            Pattern pattern = Pattern.compile(":86:.{11}(\\d{16})");

            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String extractedAccountNumber = matcher.group(1);
                    String newAccountNumber = accountNumberMap.get(extractedAccountNumber);
                    if (newAccountNumber != null) {
                        line = line.replace(extractedAccountNumber, newAccountNumber);
                        System.out.println("Updated line: " + line);
                    } else {
                        System.out.println("No update for account number: " + extractedAccountNumber);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MandiriTemplate mandiriTemplate = new MandiriTemplate();
        mandiriTemplate.flipProcess();
    }
}










//package com.example.accountflipper;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class MandiriTemplate {
//
//
//    private final String mandiriInput = "src/main/java/com/example/accountflipper/Mandiri_input.txt";
//
//    public void flipProcess() {
//        System.out.println(mandiriInput);
//        try (BufferedReader br = new BufferedReader(new FileReader(mandiriInput))) {
//            String line;
//            Pattern pattern = Pattern.compile(":86:.{11}(\\d{16})");
//
//            while ((line = br.readLine()) != null) {
//                Matcher matcher = pattern.matcher(line);
//                if (matcher.find()) {
//                    String digits = matcher.group(1);
//                    System.out.println("Account Number is: " + digits);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//
//        MandiriTemplate mandiriTemplate = new MandiriTemplate();
//        mandiriTemplate.flipProcess();
//    }
//}
//