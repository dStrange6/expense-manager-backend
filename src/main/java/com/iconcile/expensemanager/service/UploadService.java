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
        UploadBatch batch = new UploadBatch();
        batch.setFilename(file.getOriginalFilename());
        batch.setStatus("PROCESSING");
        batch = batchRepository.save(batch);

        List<Expense> validExpenses = new ArrayList<>();
        List<RowError> errors = new ArrayList<>();
        int totalRows = 0;

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            String[] header = csvReader.readNext();
            if (header == null) throw new IllegalArgumentException("CSV file is empty");

            String[] line;
            int rowNum = 1;

            while ((line = csvReader.readNext()) != null) {
                rowNum++;
                totalRows++;

                if (line.length < 3) {
                    errors.add(new RowError(rowNum, "Missing required columns (Date, Amount, Vendor)"));
                    continue;
                }

                try {
                    LocalDate date = LocalDate.parse(line[0].trim());
                    BigDecimal amount = new BigDecimal(line[1].trim());
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();

                    String vendor = line[2].trim();
                    if (vendor.isBlank()) throw new IllegalArgumentException("Vendor name cannot be blank");

                    String description = line.length > 3 ? line[3].trim() : null;

                    Category category = categorizationService.determineCategory(vendor);

                    // anomaly check runs before save so this expense isn't included in its own average
                    boolean isAnomaly = anomalyService.isAnomaly(amount, category.getId());

                    Expense expense = new Expense();
                    expense.setExpenseDate(date);
                    expense.setAmount(amount);
                    expense.setVendorName(vendor);
                    expense.setDescription(description);
                    expense.setSource("CSV");
                    expense.setCategory(category);
                    expense.setAnomaly(isAnomaly);
                    expense.setUploadBatch(batch);

                    validExpenses.add(expense);

                } catch (DateTimeParseException e) {
                    errors.add(new RowError(rowNum, "Invalid date format. Use YYYY-MM-DD"));
                } catch (NumberFormatException e) {
                    errors.add(new RowError(rowNum, "Amount must be a valid positive number"));
                } catch (Exception e) {
                    errors.add(new RowError(rowNum, e.getMessage()));
                }
            }

            if (!validExpenses.isEmpty()) {
                expenseRepository.saveAll(validExpenses);
            }

            batch.setTotalRows(totalRows);
            batch.setSuccessRows(validExpenses.size());
            batch.setFailedRows(errors.size());
            batch.setStatus(errors.isEmpty() ? "COMPLETED" : "COMPLETED_WITH_ERRORS");
            batchRepository.save(batch);

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