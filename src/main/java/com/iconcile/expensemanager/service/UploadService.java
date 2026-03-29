package com.iconcile.expensemanager.service;

import com.iconcile.expensemanager.dto.BatchUploadResult;
import com.iconcile.expensemanager.dto.RowError;
import com.iconcile.expensemanager.entity.Category;
import com.iconcile.expensemanager.entity.Expense;
import com.iconcile.expensemanager.entity.UploadBatch;
import com.iconcile.expensemanager.repository.ExpenseRepository;
import com.iconcile.expensemanager.repository.UploadBatchRepository;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final UploadBatchRepository batchRepository;
    private final ExpenseRepository expenseRepository;
    private final CategorizationService categorizationService;
    private final AnomalyService anomalyService;

    @Transactional
    public BatchUploadResult processCsv(MultipartFile file) {
        // create a tracking record in the database for this upload attempt
        UploadBatch batch = new UploadBatch();
        batch.setFilename(file.getOriginalFilename());
        batch.setStatus("PROCESSING");
        batch = batchRepository.save(batch);

        List<Expense> validExpenses = new ArrayList<>();
        List<RowError> errors = new ArrayList<>();
        int totalRows = 0;

        // read the CSV file
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            String[] header = csvReader.readNext(); // Skip the header row
            if (header == null) throw new IllegalArgumentException("CSV file is empty");

            String[] line;
            int rowNum = 1; // Start at 1 because row 1 was the header so we exclude it from the count

            // Loop through every row in the file
            while ((line = csvReader.readNext()) != null) {
                rowNum++;
                totalRows++;

                // If a row is missing columns, log an error and skip to the next row
                if (line.length < 3) {
                    errors.add(new RowError(rowNum, "Missing required columns (Date, Amount, Vendor)"));
                    continue;
                }

                // Try to parse and validate the row
                try {
                    LocalDate date = LocalDate.parse(line[0].trim());
                    BigDecimal amount = new BigDecimal(line[1].trim());
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();

                    String vendor = line[2].trim();
                    if (vendor.isBlank()) throw new IllegalArgumentException("Vendor name cannot be blank");

                    String description = line.length > 3 ? line[3].trim() : null;

                    // Apply our Business Logic (Categorization & Anomaly check)
                    Category category = categorizationService.determineCategory(vendor);
                    boolean isAnomaly = anomalyService.isAnomaly(amount, category.getId());

                    // Build the Entity
                    Expense expense = new Expense();
                    expense.setExpenseDate(date);
                    expense.setAmount(amount);
                    expense.setVendorName(vendor);
                    expense.setDescription(description);
                    expense.setSource("CSV");
                    expense.setCategory(category);
                    expense.setAnomaly(isAnomaly);
                    expense.setUploadBatch(batch);

                    // Add to our list of good rows
                    validExpenses.add(expense);

                } catch (DateTimeParseException e) {
                    errors.add(new RowError(rowNum, "Invalid date format. Use YYYY-MM-DD"));
                } catch (NumberFormatException e) {
                    errors.add(new RowError(rowNum, "Amount must be a valid positive number"));
                } catch (Exception e) {
                    errors.add(new RowError(rowNum, e.getMessage()));
                }
            }

            // Bulk save all valid rows to the db
            if (!validExpenses.isEmpty()) {
                expenseRepository.saveAll(validExpenses);
            }

            // Update the batch record with final success/failure stats
            batch.setTotalRows(totalRows);
            batch.setSuccessRows(validExpenses.size());
            batch.setFailedRows(errors.size());
            batch.setStatus(errors.isEmpty() ? "COMPLETED" : "COMPLETED_WITH_ERRORS");
            batchRepository.save(batch);

            // Return the "receipt" to the frontend
            return new BatchUploadResult(
                    batch.getId(),
                    totalRows,
                    validExpenses.size(),
                    errors.size(),
                    errors
            );

        } catch (Exception e) {
            log.error("Failed to process CSV file", e);
            batch.setStatus("FAILED");
            batchRepository.save(batch);
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
        }
    }
}