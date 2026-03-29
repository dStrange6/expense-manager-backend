package com.iconcile.expensemanager.dto;

import com.iconcile.expensemanager.dto.projection.CategorySpendView;
import com.iconcile.expensemanager.dto.projection.VendorSpendView;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummary(
        BigDecimal totalSpendThisMonth,
        long totalExpensesCount,
        int categoriesUsedCount,
        long anomaliesCount,
        List<CategorySpendView> spendByCategory,
        List<VendorSpendView> topVendors,
        List<ExpenseResponse> recentAnomalies
) {}