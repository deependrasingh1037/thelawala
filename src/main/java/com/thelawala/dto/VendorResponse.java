package com.thelawala.dto;

import com.thelawala.model.Vendor;

/**
 * Public-facing vendor view. Intentionally omits the raw trackerLink so the
 * live-location URL is never exposed to the browser. Clients open the link via
 * the /track redirect endpoint instead.
 */
public class VendorResponse {

    private Long id;
    private String name;
    private String phone;
    private String photoUrl;
    private String status;
    private long visitCount;
    private boolean hasLink;

    public static VendorResponse from(Vendor v) {
        VendorResponse r = new VendorResponse();
        r.id = v.getId();
        r.name = v.getName();
        r.phone = v.getPhone();
        r.photoUrl = v.getPhotoUrl();
        r.status = v.getStatus().name();
        r.visitCount = v.getVisitCount();
        r.hasLink = v.getTrackerLink() != null && !v.getTrackerLink().isBlank();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getPhotoUrl() { return photoUrl; }
    public String getStatus() { return status; }
    public long getVisitCount() { return visitCount; }
    public boolean isHasLink() { return hasLink; }
}
