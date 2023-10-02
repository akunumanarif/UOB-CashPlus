package com.example.accountflipper.controller;

import com.example.accountflipper.service.BNITemplateService;
import com.example.accountflipper.service.MandiriTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/bni")
public class BNITemplateController {

    @Autowired
    private BNITemplateService bniTemplateService;

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processTemplate(
            @RequestParam("bniFile") MultipartFile bniFile,
            @RequestParam("staticDataFile") MultipartFile staticDataFile,
            @RequestParam("oldAccountNumberPosition") int oldAccountNumberPosition,
            @RequestParam("newAccountNumberPosition") int newAccountNumberPosition,
            @RequestParam("accountNumberPosition") int accountNumberPosition
    ) {
        try {
            Map<String, Object> response = bniTemplateService.processTemplate(
                    bniFile, staticDataFile,
                    oldAccountNumberPosition, newAccountNumberPosition,
                    accountNumberPosition
            );
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error and return an appropriate response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error processing the template");
            errorResponse.put("statusCode", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadResult(@PathVariable("fileName") String fileName) {

        try {
            // Load the file content
            InputStreamResource content = new InputStreamResource(new FileInputStream("output_dir/" + fileName));

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
