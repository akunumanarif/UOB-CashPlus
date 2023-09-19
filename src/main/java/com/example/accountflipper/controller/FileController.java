package com.example.accountflipper.controller;


import com.example.accountflipper.response.ResponseMessage;
import com.example.accountflipper.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file")MultipartFile file) {
        if(fileService.hasCSVFormat(file)) {
            fileService.processAndSaveData(file);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(file.getOriginalFilename() + "Successfully Uploaded"));
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Failed"));
    }
}
