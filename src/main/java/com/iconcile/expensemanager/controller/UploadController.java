package com.iconcile.expensemanager.controller;

import com.iconcile.expensemanager.dto.BatchUploadResult;
import com.iconcile.expensemanager.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/expenses/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping
    public ResponseEntity<BatchUploadResult> uploadCsv(@RequestParam("file") MultipartFile file) {

        // Basic safety check to ensure we have a file and it's a CSV. We can add more checks here if needed (e.g. file size limits).
        if (file.isEmpty() || file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest().build();
        }

        BatchUploadResult result = uploadService.processCsv(file);
        return ResponseEntity.ok(result);
    }
}