package com.iconcile.expensemanager.service;

import com.iconcile.expensemanager.entity.Category;
import com.iconcile.expensemanager.repository.CategoryRepository;
import com.iconcile.expensemanager.repository.VendorCategoryRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategorizationService {

    private final VendorCategoryRuleRepository ruleRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Category determineCategory(String vendorName) {
        if (vendorName == null || vendorName.isBlank()) {
            return getFallbackCategory();
        }

        String normalizedVendor = vendorName.toLowerCase().trim().replaceAll("\\s+", " ");

        return ruleRepository.findBestMatch(normalizedVendor)
                .map(rule -> {
                    log.info("Matched vendor '{}' to category '{}'", vendorName, rule.getCategory().getName());
                    return rule.getCategory();
                })
                .orElseGet(() -> {
                    log.info("No rule matched for vendor '{}', falling back to Uncategorised", vendorName);
                    return getFallbackCategory();
                });
    }

    private Category getFallbackCategory() {
        return categoryRepository.findByName("Uncategorised")
                .orElseThrow(() -> new IllegalStateException("Critical: 'Uncategorised' category missing."));
    }
}