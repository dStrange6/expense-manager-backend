package com.iconcile.expensemanager.repository;

import com.iconcile.expensemanager.entity.UploadBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UploadBatchRepository extends JpaRepository<UploadBatch, UUID> {
}