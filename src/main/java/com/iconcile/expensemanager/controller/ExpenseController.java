package com.iconcile.expensemanager.controller;

import com.iconcile.expensemanager.dto.ExpenseRequest;
import com.iconcile.expensemanager.dto.ExpenseResponse;
import com.iconcile.expensemanager.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createExpense(@Valid @RequestBody ExpenseRequest request) {
        return expenseService.createManualExpense(request);
    }

    @GetMapping
    public java.util.List<ExpenseResponse> getExpenses(
            @RequestParam(name = "month", required = false) String month) {

        // If the frontend doesn't send a month, default to the current month
        if (month == null || month.isBlank()) {
            month = java.time.YearMonth.now().toString();
        }

        return expenseService.getExpensesByMonth(month);
    }
}