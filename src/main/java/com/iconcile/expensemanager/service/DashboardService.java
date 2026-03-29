package com.iconcile.expensemanager.service;

import com.iconcile.expensemanager.dto.*;
import com.iconcile.expensemanager.dto.projection.CategorySpendView;
import com.iconcile.expensemanager.dto.projection.VendorSpendView;
import com.iconcile.expensemanager.entity.Expense;
import com.iconcile.expensemanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public DashboardSummary getDashboardData(String monthStr) {

        // Determine Date Range (First day to Last day of the month)
        YearMonth month = YearMonth.parse(monthStr);
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        // Fetch Top-Level Metrics
        BigDecimal totalSpend = expenseRepository.getTotalSpendForMonth(startDate, endDate);
        long totalExpenses = expenseRepository.countExpensesForMonth(startDate, endDate);
        long totalAnomalies = expenseRepository.countAnomaliesForMonth(startDate, endDate);

        // Fetch Chart Data
        List<CategorySpendView> categorySpend = expenseRepository.findCategorySpendForMonth(startDate, endDate);
        List<VendorSpendView> topVendors = expenseRepository.findTopVendorsForMonth(startDate, endDate, PageRequest.of(0, 5));

        // Fetch Anomalies List
        List<ExpenseResponse> recentAnomalies = expenseRepository
                .findByIsAnomalyTrueAndExpenseDateBetweenOrderByExpenseDateDesc(startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .toList();

        // combine and Return
        return new DashboardSummary(
                totalSpend,
                totalExpenses,
                categorySpend.size(),
                totalAnomalies,
                categorySpend,
                topVendors,
                recentAnomalies
        );
    }

    // private mapping method here so this service doesn't depend on ExpenseService
    private ExpenseResponse mapToResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getExpenseDate(),
                expense.getAmount(),
                expense.getVendorName(),
                expense.getDescription(),
                expense.getCategory().getName(),
                expense.isAnomaly()
        );
    }
}