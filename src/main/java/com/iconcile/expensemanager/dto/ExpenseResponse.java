package com.iconcile.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        LocalDate expenseDate,
        BigDecimal amount,
        String vendorName,
        String description,
        String categoryName,
        boolean isAnomaly
) {}