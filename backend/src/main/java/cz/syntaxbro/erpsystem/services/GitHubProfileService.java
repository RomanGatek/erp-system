package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.gitHub.Branch;
import cz.syntaxbro.erpsystem.models.gitHub.GitHubProfile;

import java.util.List;

public interface GitHubProfileService {
    public List<String> projectPartners();
    public List<Branch> projectBranches();
    public GitHubProfile gitHubProfile(String username);
}
