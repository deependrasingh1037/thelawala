package com.thelawala.service;

import com.thelawala.dto.VendorRequest;
import com.thelawala.exception.ResourceNotFoundException;
import com.thelawala.model.Vendor;
import com.thelawala.model.VendorStatus;
import com.thelawala.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VendorService {

    // Dummy placeholder photo used when the vendor has no photo yet.
    private static final String DEFAULT_PHOTO =
            "https://placehold.co/160x160?text=ThelaWala";

    private final VendorRepository repository;

    public VendorService(VendorRepository repository) {
        this.repository = repository;
    }

    /** Onboard a new vendor. Goes ONLINE immediately if a tracker link is provided. */
    @Transactional
    public Vendor onboard(VendorRequest req) {
        Vendor v = new Vendor();
        v.setName(req.getName().trim());
        v.setPhone(req.getPhone().trim());
        v.setPhotoUrl(hasText(req.getPhotoUrl()) ? req.getPhotoUrl().trim() : DEFAULT_PHOTO);
        if (hasText(req.getTrackerLink())) {
            v.setTrackerLink(req.getTrackerLink().trim());
            v.setStatus(VendorStatus.ONLINE);
        } else {
            v.setStatus(VendorStatus.OFFLINE);
        }
        return repository.save(v);
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

    /** Admin deletes a vendor permanently. */
    @Transactional
    public void delete(Long id) {
        Vendor v = get(id); // 404 if the vendor doesn't exist
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
        v.setVisitCount(v.getVisitCount() + 1);
        repository.save(v);
        return v.getTrackerLink();
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
