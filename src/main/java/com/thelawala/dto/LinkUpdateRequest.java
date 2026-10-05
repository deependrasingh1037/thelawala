package com.thelawala.dto;

import jakarta.validation.constraints.NotBlank;

/** Payload for admin to update a vendor's live tracker link. */
public class LinkUpdateRequest {

    @NotBlank(message = "trackerLink is required")
    private String trackerLink;

    public String getTrackerLink() { return trackerLink; }
    public void setTrackerLink(String trackerLink) { this.trackerLink = trackerLink; }
}
