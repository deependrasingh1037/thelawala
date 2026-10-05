package com.thelawala.model;

import jakarta.persistence.*;

/**
 * High-churn counters kept in their own table, separate from the vendor profile.
 * Visits (and likes) update far more often than onboarding details, so isolating
 * them keeps writes off the profile row. Primary key is the vendor id (1:1).
 */
@Entity
@Table(name = "vendor_stats")
public class VendorStats {

    @Id
    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "visit_count", nullable = false)
    private long visitCount = 0;

    @Column(name = "likes_count", nullable = false)
    private long likesCount = 0;

    public VendorStats() {}

    public VendorStats(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public long getVisitCount() { return visitCount; }
    public void setVisitCount(long visitCount) { this.visitCount = visitCount; }

    public long getLikesCount() { return likesCount; }
    public void setLikesCount(long likesCount) { this.likesCount = likesCount; }
}
