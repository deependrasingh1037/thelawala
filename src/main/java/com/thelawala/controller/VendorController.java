package com.thelawala.controller;

import com.thelawala.dto.LinkUpdateRequest;
import com.thelawala.dto.VendorRequest;
import com.thelawala.dto.VendorResponse;
import com.thelawala.model.Vendor;
import com.thelawala.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin
public class VendorController {

    private final VendorService service;

    public VendorController(VendorService service) {
        this.service = service;
    }

    /** Onboard a new vendor. */
    @PostMapping
    public ResponseEntity<VendorResponse> onboard(@Valid @RequestBody VendorRequest req) {
        Vendor v = service.onboard(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(VendorResponse.from(v));
    }

    /** Admin: update a vendor's live tracker link. */
    @PutMapping("/{id}/link")
    public VendorResponse updateLink(@PathVariable Long id,
                                     @Valid @RequestBody LinkUpdateRequest req) {
        return VendorResponse.from(service.updateLink(id, req.getTrackerLink()));
    }

    /** Admin: mark a vendor online (requires an existing tracker link). */
    @PutMapping("/{id}/online")
    public VendorResponse markOnline(@PathVariable Long id) {
        return VendorResponse.from(service.markOnline(id));
    }

    /** Admin: mark a vendor offline. */
    @PutMapping("/{id}/offline")
    public VendorResponse markOffline(@PathVariable Long id) {
        return VendorResponse.from(service.markOffline(id));
    }

    /** Admin: delete a vendor permanently. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Public: vendors currently online (shown on the website). */
    @GetMapping("/online")
    public List<VendorResponse> online() {
        return service.listOnline().stream().map(VendorResponse::from).toList();
    }

    /** Admin: list all vendors regardless of status. */
    @GetMapping
    public List<VendorResponse> all() {
        return service.listAll().stream().map(VendorResponse::from).toList();
    }

    /** Admin: fetch a single vendor. */
    @GetMapping("/{id}")
    public VendorResponse one(@PathVariable Long id) {
        return VendorResponse.from(service.get(id));
    }
}
