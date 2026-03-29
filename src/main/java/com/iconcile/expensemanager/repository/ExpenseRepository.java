package com.iconcile.expensemanager.repository;

import com.iconcile.expensemanager.dto.projection.CategorySpendView;
import com.iconcile.expensemanager.dto.projection.VendorSpendView;
import com.iconcile.expensemanager.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.category.id = :categoryId AND e.isAnomaly = false")
    long countNonAnomalousByCategory(@Param("categoryId") UUID categoryId);

    @Query("SELECT AVG(e.amount) FROM Expense e WHERE e.category.id = :categoryId AND e.isAnomaly = false")
    BigDecimal getAverageAmountByCategoryExcludingAnomalies(@Param("categoryId") UUID categoryId);

    // --- dashboard queries ---

    @Query("""
        SELECT e.category.name AS categoryName, SUM(e.amount) AS totalAmount
        FROM Expense e
        WHERE e.expenseDate BETWEEN :startDate AND :endDate
        GROUP BY e.category.name
        ORDER BY SUM(e.amount) DESC
    """)
    List<CategorySpendView> findCategorySpendForMonth(
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate);

    @Query("""
        SELECT e.vendorName AS vendorName, SUM(e.amount) AS totalAmount
        FROM Expense e
        WHERE e.expenseDate BETWEEN :startDate AND :endDate
        GROUP BY e.vendorName
        ORDER BY SUM(e.amount) DESC
    """)
    List<VendorSpendView> findTopVendorsForMonth(
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalSpendForMonth(
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    long countExpensesForMonth(
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate AND e.isAnomaly = true")
    long countAnomaliesForMonth(
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate);

    List<com.iconcile.expensemanager.entity.Expense> findByIsAnomalyTrueAndExpenseDateBetweenOrderByExpenseDateDesc(
            java.time.LocalDate startDate, java.time.LocalDate endDate);

    List<Expense> findByExpenseDateBetweenOrderByExpenseDateDesc(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate
    );
}