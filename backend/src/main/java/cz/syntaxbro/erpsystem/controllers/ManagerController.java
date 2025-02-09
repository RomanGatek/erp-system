package cz.syntaxbro.erpsystem.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cz.syntaxbro.erpsystem.constants.Roles.MANAGER;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> managerAccess() {
        return ResponseEntity.ok("Welcome, MANAGER!");
    }
}
