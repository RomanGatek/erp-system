package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.services.GitHubProfileService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GitHubProfileServiceImpl implements GitHubProfileService {

    private final WebClient webClient = WebClient.create("https://api.github.com");


    @Override
    public boolean apiConnect() {
        return false;
    }

    @Override
    public String getUsername(String username) {
        return webClient.get()
                .uri("/users/" + username)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> new JSONObject(body).optString("name", "No name found"))
                .block(); // Blocking call, use async in reactive apps
    }

    @Override
    public String getUserInformation() {
        return "";
    }

    @Override
    public List<String> getBranches() {
        return List.of();
    }

    @Override
    public String getCommits() {
        return "";
    }
}
