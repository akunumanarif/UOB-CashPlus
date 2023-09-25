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
                    String newAccountNumber = parts[11].trim(); // Extracting new account number
                    accountNumberMap.put(oldAccountNumber, newAccountNumber);
//                    System.out.println("This is acMAP : ++++++ " + accountNumberMap);
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

            while ((line = br.readLine()) != null) {
                if (line.startsWith(":86:")) {
                    String extractedAccountNumber = extractAccountNumber(line);
                    String newAccountNumber = accountNumberMap.get(extractedAccountNumber);
                    if (newAccountNumber != null) {
                        // Replace old account number with new account number
                        String updatedLine = line.replace(extractedAccountNumber, newAccountNumber);
                        System.out.println(updatedLine);
                    } else {
                        // Print the original :86: line if no update for account number
                        System.out.println(line);
                    }
                } else {
                    // Print other lines as is
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String extractAccountNumber(String line) {
        // Assuming the account number is the 15th character onwards in the :86: line
        return line.substring(15, 31).trim();
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
