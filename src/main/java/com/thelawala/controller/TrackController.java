package com.thelawala.controller;

import com.thelawala.service.VendorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Keeps the raw live-location link off the UI. The browser hits this endpoint,
 * which counts the visit and then 302-redirects to the vendor's Google Maps link.
 * The frontend opens this URL in a new tab.
 */
@RestController
public class TrackController {

    private final VendorService service;

    public TrackController(VendorService service) {
        this.service = service;
    }

    @GetMapping("/track/{id}")
    public RedirectView track(@PathVariable Long id) {
        String link = service.recordVisitAndGetLink(id);
        return new RedirectView(link);
    }
}
