package com.thelawala.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 80)
    private String city;

    // Dummy/placeholder photo URL for now.
    @Column(name = "photo_url")
    private String photoUrl;

    // Google Maps live-location share link. Hidden from the public UI.
    @Column(name = "tracker_link", length = 1000)
    private String trackerLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VendorStatus status = VendorStatus.OFFLINE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ===== Getters / setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getTrackerLink() { return trackerLink; }
    public void setTrackerLink(String trackerLink) { this.trackerLink = trackerLink; }

    public VendorStatus getStatus() { return status; }
    public void setStatus(VendorStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
