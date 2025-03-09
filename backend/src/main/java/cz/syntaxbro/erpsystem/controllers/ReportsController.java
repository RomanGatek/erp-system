package cz.syntaxbro.erpsystem.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling report-related operations.
 * Access to reports is restricted to users with ADMIN or MANAGER roles.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    /**
     * Retrieves all available reports.
     * Only users with ADMIN or MANAGER roles can access this endpoint.
     * 
     * @return A response with all reports data
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> getAllReports() {
        // In a real application, this would fetch reports from a service
        return ResponseEntity.ok("All reports found");
    }
} 