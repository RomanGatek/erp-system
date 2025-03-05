package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.services.GitHubProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github")
@EnableMethodSecurity()
public class GitHubController {

    private final GitHubProfileService gitHubProfileService;

    @Autowired
    public GitHubController(GitHubProfileService gitHubProfileService) {
        this.gitHubProfileService = gitHubProfileService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<String> createGitHubProfile() {
        gitHubProfileService.createGitHubProfile();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
