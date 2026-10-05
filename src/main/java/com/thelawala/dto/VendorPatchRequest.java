package com.thelawala.dto;

import jakarta.validation.constraints.Pattern;

/**
 * Partial-update payload. All fields optional; only non-null fields are applied.
 * Intentionally excludes likes/visits (stats) and trackerLink (its own endpoint).
 */
public class VendorPatchRequest {

    private String name;

    @Pattern(regexp = "^[0-9+\\-\\s]{7,20}$", message = "phone must be a valid number")
    private String phone;

    private String city;

    // Profile photo link.
    private String photoUrl;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
