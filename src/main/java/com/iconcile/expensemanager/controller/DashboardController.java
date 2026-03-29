package com.iconcile.expensemanager.controller;

import com.iconcile.expensemanager.dto.DashboardSummary;
import com.iconcile.expensemanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummary getDashboardSummary(@RequestParam(required = false) String month) {

        // If frontend doesn't pass a month, default to the current month safely.
        String targetMonth = (month == null || month.isBlank())
                ? YearMonth.now().toString()
                : month;

        return dashboardService.getDashboardData(targetMonth);
    }
}