package com.iconcile.expensemanager.service;

import com.iconcile.expensemanager.dto.ExpenseRequest;
import com.iconcile.expensemanager.dto.ExpenseResponse;
import com.iconcile.expensemanager.entity.Category;
import com.iconcile.expensemanager.entity.Expense;
import com.iconcile.expensemanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategorizationService categorizationService;
    private final AnomalyService anomalyService;

    @Transactional
    public ExpenseResponse createManualExpense(ExpenseRequest request) {
        Category category = categorizationService.determineCategory(request.vendorName());
        boolean isAnomaly = anomalyService.isAnomaly(request.amount(), category.getId());

        Expense expense = new Expense();
        expense.setExpenseDate(request.expenseDate());
        expense.setAmount(request.amount());
        expense.setVendorName(request.vendorName().trim());
        expense.setDescription(request.description());
        expense.setSource("MANUAL");
        expense.setCategory(category);
        expense.setAnomaly(isAnomaly);

        Expense savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense);
    }

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

    public java.util.List<ExpenseResponse> getExpensesByMonth(String yearMonth) {
        java.time.YearMonth ym = java.time.YearMonth.parse(yearMonth);
        java.time.LocalDate startDate = ym.atDay(1);
        java.time.LocalDate endDate = ym.atEndOfMonth();

        return expenseRepository.findByExpenseDateBetweenOrderByExpenseDateDesc(startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}