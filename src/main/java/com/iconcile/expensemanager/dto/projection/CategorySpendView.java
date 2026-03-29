package com.iconcile.expensemanager.dto.projection;

import java.math.BigDecimal;

public record CategorySpendView(
        String categoryName,
        BigDecimal totalAmount
) {}
