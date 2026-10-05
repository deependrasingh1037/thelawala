package com.thelawala.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * A visitor comment on a vendor. Kept in its own table (one row per comment).
 * Read-only in the UI for now; seeded manually. Public posting is a later step.
 */
@Entity
@Table(name = "vendor_comments")
public class VendorComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(nullable = false, length = 80)
    private String author;

    @Column(nullable = false, length = 1000)
    private String text;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Instant getCreatedAt() { return createdAt; }
}
