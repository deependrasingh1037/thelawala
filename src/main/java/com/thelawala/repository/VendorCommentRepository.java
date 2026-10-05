package com.thelawala.repository;

import com.thelawala.model.VendorComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorCommentRepository extends JpaRepository<VendorComment, Long> {
    List<VendorComment> findByVendorIdOrderByCreatedAtDesc(Long vendorId);
    void deleteByVendorId(Long vendorId);
}
