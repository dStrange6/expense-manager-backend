package com.iconcile.expensemanager.service;

import com.iconcile.expensemanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnomalyService {

    private final ExpenseRepository expenseRepository;

    @Value("${app.anomaly.multiplier:3.0}")
    private BigDecimal multiplier;

    @Value("${app.anomaly.min-sample-size:3}")
    private long minSampleSize;

    @Transactional(readOnly = true)
    public boolean isAnomaly(BigDecimal amount, UUID categoryId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return false;

        long validCount = expenseRepository.countNonAnomalousByCategory(categoryId);
        if (validCount < minSampleSize) return false;

        BigDecimal average = expenseRepository.getAverageAmountByCategoryExcludingAnomalies(categoryId);
        if (average == null) return false;

        BigDecimal threshold = average.multiply(multiplier);
        boolean isFlagged = amount.compareTo(threshold) > 0;

        if (isFlagged) {
            log.warn("ANOMALY DETECTED: Amount {} exceeds threshold {}", amount, threshold);
        }

        return isFlagged;
    }
}