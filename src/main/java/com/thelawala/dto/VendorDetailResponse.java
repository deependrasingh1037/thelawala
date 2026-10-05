package com.thelawala.dto;

import com.thelawala.model.Vendor;
import com.thelawala.model.VendorComment;
import com.thelawala.model.VendorStats;

import java.time.Instant;
import java.util.List;

/**
 * Full vendor view for the popup card: profile + stats (from vendor_stats) +
 * comments (from vendor_comments). Still omits the raw tracker link.
 */
public class VendorDetailResponse {

    private Long id;
    private String name;
    private String phone;
    private String city;
    private String photoUrl;
    private String status;
    private boolean hasLink;
    private long visitCount;
    private long likes;
    private List<CommentView> comments;

    public static VendorDetailResponse from(Vendor v, VendorStats stats, List<VendorComment> comments) {
        VendorDetailResponse r = new VendorDetailResponse();
        r.id = v.getId();
        r.name = v.getName();
        r.phone = v.getPhone();
        r.city = v.getCity();
        r.photoUrl = v.getPhotoUrl();
        r.status = v.getStatus().name();
        r.hasLink = v.getTrackerLink() != null && !v.getTrackerLink().isBlank();
        r.visitCount = stats != null ? stats.getVisitCount() : 0;
        r.likes = stats != null ? stats.getLikesCount() : 0;
        r.comments = comments.stream().map(CommentView::from).toList();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public String getPhotoUrl() { return photoUrl; }
    public String getStatus() { return status; }
    public boolean isHasLink() { return hasLink; }
    public long getVisitCount() { return visitCount; }
    public long getLikes() { return likes; }
    public List<CommentView> getComments() { return comments; }

    public static class CommentView {
        private String author;
        private String text;
        private Instant createdAt;

        public static CommentView from(VendorComment c) {
            CommentView v = new CommentView();
            v.author = c.getAuthor();
            v.text = c.getText();
            v.createdAt = c.getCreatedAt();
            return v;
        }

        public String getAuthor() { return author; }
        public String getText() { return text; }
        public Instant getCreatedAt() { return createdAt; }
    }
}
