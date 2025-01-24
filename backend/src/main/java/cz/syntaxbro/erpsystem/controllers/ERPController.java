// REVIEWED

package cz.syntaxbro.erpsystem.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static cz.syntaxbro.erpsystem.constants.Permissions.*;
import static cz.syntaxbro.erpsystem.constants.Roles.*;

@RestController
public class ERPController {

    @GetMapping("/api/reports")
    @PreAuthorize("hasAuthority('" + READ_REPORTS + "')")
    public ResponseEntity<String> accessReports() {
        return ResponseEntity.ok("Accessing Reports!");
    }

    @GetMapping("/api/approve-budget")
    @PreAuthorize("hasAuthority('" + APPROVE_BUDGETS + "')")
    public ResponseEntity<String> approveBudget() {
        return ResponseEntity.ok("Budget Approved!");
    }

    @GetMapping("/api/user/profile")
    @PreAuthorize("hasAuthority('" + VIEW_PROFILE + "')")
    public ResponseEntity<String> viewProfile() {
        return ResponseEntity.ok("Viewing Profile!");
    }

    @GetMapping("/api/admin")
    @PreAuthorize("hasRole('" + ADMIN + "')")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("Welcome, Admin!");
    }

    @GetMapping("/api/manager")
    @PreAuthorize("hasRole('" + MANAGER + "')")
    public ResponseEntity<String> managerAccess() {
        return ResponseEntity.ok("Welcome, Manager!");
    }

    @GetMapping("/api/user")
    @PreAuthorize("hasRole('" + USER + "')")
    public ResponseEntity<String> userAccess() {
        return ResponseEntity.ok("Welcome, User!");
    }
}