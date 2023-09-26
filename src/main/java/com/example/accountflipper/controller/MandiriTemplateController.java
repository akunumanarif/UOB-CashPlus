package com.example.accountflipper.controller;

import com.example.accountflipper.service.MandiriTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;


@RestController
@RequestMapping("/api/mandiri")
public class MandiriTemplateController {

    @Autowired
    private MandiriTemplateService mandiriTemplateService;

    @PostMapping("/process")
    public ResponseEntity<?> processTemplate(
            @RequestParam("mandiriFile") MultipartFile mandiriFile,
            @RequestParam("staticDataFile") MultipartFile staticDataFile) {
        try {
            String resultFilePath = mandiriTemplateService.processTemplate(mandiriFile, staticDataFile);

            // Create a downloadable URL for the result file
            String downloadUrl = "/api/mandiri/download/" + resultFilePath;

            return ResponseEntity.status(HttpStatus.OK).body(downloadUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
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
