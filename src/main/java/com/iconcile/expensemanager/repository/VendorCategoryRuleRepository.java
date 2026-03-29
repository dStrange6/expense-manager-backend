package com.iconcile.expensemanager.repository;

import com.iconcile.expensemanager.entity.VendorCategoryRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface VendorCategoryRuleRepository extends JpaRepository<VendorCategoryRule, UUID> {

    @Query(value = """
        SELECT vcr.* FROM vendor_category_rules vcr
        WHERE 
            (vcr.match_type = 'EXACT' AND LOWER(:vendor) = vcr.vendor_pattern)
            OR (vcr.match_type = 'CONTAINS' AND LOWER(:vendor) LIKE '%' || vcr.vendor_pattern || '%')
        ORDER BY vcr.priority ASC
        LIMIT 1
        """, nativeQuery = true)
    Optional<VendorCategoryRule> findBestMatch(@Param("vendor") String vendorName);
}