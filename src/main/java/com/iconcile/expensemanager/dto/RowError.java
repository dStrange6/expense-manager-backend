package com.iconcile.expensemanager.dto;

public record RowError(
        int rowNumber,
        String errorMessage
) {}