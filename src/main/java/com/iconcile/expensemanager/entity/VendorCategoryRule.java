package com.iconcile.expensemanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "vendor_category_rules")
@Getter
@Setter
@NoArgsConstructor
public class VendorCategoryRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "vendor_pattern", nullable = false, length = 100)
    private String vendorPattern;

    @Column(name = "match_type", nullable = false, length = 10)
    private String matchType;

    @Column(nullable = false)
    private int priority = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}