package com.thelawala.repository;

import com.thelawala.model.Vendor;
import com.thelawala.model.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    List<Vendor> findByStatusOrderByNameAsc(VendorStatus status);
}
