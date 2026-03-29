package com.iconcile.expensemanager.dto;

import java.util.List;
import java.util.UUID;

public record BatchUploadResult(
        UUID batchId,
        int totalRows,
        int successfulRows,
        int failedRows,
        List<RowError> errors
) {}