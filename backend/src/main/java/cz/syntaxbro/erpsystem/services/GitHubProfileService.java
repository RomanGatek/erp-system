package cz.syntaxbro.erpsystem.services;

import java.util.List;

public interface GitHubProfileService {
    public boolean apiConnect();
    public String getUsername(String username);
    public String getUserInformation();
    public List<String> getBranches();
    public String getCommits();
}
