package com.thelawala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Payload to onboard a new vendor. */
public class VendorRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "phone is required")
    @Pattern(regexp = "^[0-9+\\-\\s]{7,20}$", message = "phone must be a valid number")
    private String phone;

    // Optional.
    private String city;

    // Optional at onboarding; can be set later via the update-link API.
    private String trackerLink;

    // Optional; a dummy placeholder is used if omitted.
    private String photoUrl;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getTrackerLink() { return trackerLink; }
    public void setTrackerLink(String trackerLink) { this.trackerLink = trackerLink; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
