package com.iconcile.expensemanager.dto.projection;

import java.math.BigDecimal;

public record VendorSpendView(
        String vendorName,
        BigDecimal totalAmount
) {}
