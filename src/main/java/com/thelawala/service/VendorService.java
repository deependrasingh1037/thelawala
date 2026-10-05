package com.thelawala.service;

import com.thelawala.dto.CommentRequest;
import com.thelawala.dto.VendorDetailResponse;
import com.thelawala.dto.VendorPatchRequest;
import com.thelawala.dto.VendorRequest;
import com.thelawala.exception.ResourceNotFoundException;
import com.thelawala.model.Vendor;
import com.thelawala.model.VendorComment;
import com.thelawala.model.VendorStats;
import com.thelawala.model.VendorStatus;
import com.thelawala.repository.VendorCommentRepository;
import com.thelawala.repository.VendorRepository;
import com.thelawala.repository.VendorStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VendorService {

    // Dummy placeholder photo used when the vendor has no photo yet.
    private static final String DEFAULT_PHOTO =
            "https://placehold.co/160x160?text=ThelaWala";

    private final VendorRepository repository;
    private final VendorStatsRepository statsRepository;
    private final VendorCommentRepository commentRepository;

    public VendorService(VendorRepository repository,
                         VendorStatsRepository statsRepository,
                         VendorCommentRepository commentRepository) {
        this.repository = repository;
        this.statsRepository = statsRepository;
        this.commentRepository = commentRepository;
    }

    /** Onboard a new vendor. Goes ONLINE immediately if a tracker link is provided. */
    @Transactional
    public Vendor onboard(VendorRequest req) {
        Vendor v = new Vendor();
        v.setName(req.getName().trim());
        v.setPhone(req.getPhone().trim());
        if (hasText(req.getCity())) v.setCity(req.getCity().trim());
        // No profile link? Fall back to a placeholder showing the name's initials.
        v.setPhotoUrl(hasText(req.getPhotoUrl())
                ? req.getPhotoUrl().trim()
                : initialsPhoto(v.getName()));
        if (hasText(req.getTrackerLink())) {
            v.setTrackerLink(req.getTrackerLink().trim());
            v.setStatus(VendorStatus.ONLINE);
        } else {
            v.setStatus(VendorStatus.OFFLINE);
        }
        Vendor saved = repository.save(v);
        // Create the companion stats row (0 visits / likes).
        statsRepository.save(new VendorStats(saved.getId()));
        return saved;
    }

    /** Admin updates a vendor's live tracker link. Setting a link brings them ONLINE. */
    @Transactional
    public Vendor updateLink(Long id, String trackerLink) {
        Vendor v = get(id);
        v.setTrackerLink(trackerLink.trim());
        v.setStatus(VendorStatus.ONLINE);
        return repository.save(v);
    }

    /** Admin marks a vendor ONLINE. Requires an existing tracker link. */
    @Transactional
    public Vendor markOnline(Long id) {
        Vendor v = get(id);
        if (!hasText(v.getTrackerLink())) {
            throw new IllegalStateException(
                    "Vendor has no tracker link; set a link before going online: " + id);
        }
        v.setStatus(VendorStatus.ONLINE);
        return repository.save(v);
    }

    /** Admin marks a vendor OFFLINE. */
    @Transactional
    public Vendor markOffline(Long id) {
        Vendor v = get(id);
        v.setStatus(VendorStatus.OFFLINE);
        return repository.save(v);
    }

    /** Admin deletes a vendor permanently, along with its stats and comments. */
    @Transactional
    public void delete(Long id) {
        Vendor v = get(id); // 404 if the vendor doesn't exist
        commentRepository.deleteByVendorId(id);
        statsRepository.deleteById(id);
        repository.delete(v);
    }

    /** Vendors currently visible on the public site. */
    @Transactional(readOnly = true)
    public List<Vendor> listOnline() {
        return repository.findByStatusOrderByNameAsc(VendorStatus.ONLINE);
    }

    @Transactional(readOnly = true)
    public List<Vendor> listAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Vendor get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found: " + id));
    }

    /** Full detail view for the popup: profile + stats + comments. */
    @Transactional(readOnly = true)
    public VendorDetailResponse getDetail(Long id) {
        Vendor v = get(id);
        VendorStats stats = statsRepository.findById(id).orElse(null);
        List<VendorComment> comments = commentRepository.findByVendorIdOrderByCreatedAtDesc(id);
        return VendorDetailResponse.from(v, stats, comments);
    }

    /**
     * Partial update of vendor metadata. Only applies fields that are present
     * (non-null). Never touches stats (likes/visits) or the tracker link.
     */
    @Transactional
    public Vendor patch(Long id, VendorPatchRequest req) {
        Vendor v = get(id);
        if (hasText(req.getName())) v.setName(req.getName().trim());
        if (hasText(req.getPhone())) v.setPhone(req.getPhone().trim());
        if (req.getCity() != null) v.setCity(req.getCity().isBlank() ? null : req.getCity().trim());
        if (req.getPhotoUrl() != null) {
            v.setPhotoUrl(req.getPhotoUrl().isBlank() ? initialsPhoto(v.getName()) : req.getPhotoUrl().trim());
        }
        return repository.save(v);
    }

    /** Adds a visitor comment to a vendor. */
    @Transactional
    public VendorComment addComment(Long id, CommentRequest req) {
        get(id); // 404 if the vendor doesn't exist
        VendorComment c = new VendorComment();
        c.setVendorId(id);
        c.setAuthor(req.getAuthor().trim());
        c.setText(req.getText().trim());
        return commentRepository.save(c);
    }

    /**
     * Admin: adds {@code delta} (which may be negative) to a vendor's like count.
     * Starts from 0 if no stats row exists yet, and never drops below 0.
     * Returns the updated like count.
     */
    @Transactional
    public long adjustLikes(Long id, int delta) {
        get(id); // 404 if the vendor doesn't exist
        VendorStats stats = statsRepository.findById(id).orElseGet(() -> new VendorStats(id));
        long updated = Math.max(0, stats.getLikesCount() + delta);
        stats.setLikesCount(updated);
        statsRepository.save(stats);
        return updated;
    }

    /**
     * Records a visit and returns the tracker link to redirect to.
     * Throws if the vendor has no link or is offline.
     */
    @Transactional
    public String recordVisitAndGetLink(Long id) {
        Vendor v = get(id);
        if (v.getStatus() != VendorStatus.ONLINE || !hasText(v.getTrackerLink())) {
            throw new ResourceNotFoundException("Vendor is not online: " + id);
        }
        // Visit write hits only the stats table, not the vendor profile row.
        VendorStats stats = statsRepository.findById(id).orElseGet(() -> new VendorStats(id));
        stats.setVisitCount(stats.getVisitCount() + 1);
        statsRepository.save(stats);
        return v.getTrackerLink();
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    /**
     * Builds a placeholder photo showing the capitalized first letter of each
     * word of the name, e.g. "Ram chaat bhandhar" -> "RCB". Falls back to the
     * generic placeholder if no usable letters are found.
     */
    private static String initialsPhoto(String name) {
        StringBuilder initials = new StringBuilder();
        if (name != null) {
            for (String word : name.trim().split("\\s+")) {
                if (!word.isEmpty()) initials.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return initials.length() == 0
                ? DEFAULT_PHOTO
                : "https://placehold.co/160x160?text=" + initials;
    }
}
