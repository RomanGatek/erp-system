package cz.syntaxbro.erpsystem.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cz.syntaxbro.erpsystem.constants.Roles.ADMIN;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping
    @PreAuthorize("hasRole('" + ADMIN + "')")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("Welcome, ADMIN!");
    }

}
