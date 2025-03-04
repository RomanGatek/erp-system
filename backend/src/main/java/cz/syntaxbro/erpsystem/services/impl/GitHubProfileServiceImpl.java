package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.gitHub.Branch;
import cz.syntaxbro.erpsystem.models.gitHub.Commit;
import cz.syntaxbro.erpsystem.models.gitHub.GitHubProfile;
import cz.syntaxbro.erpsystem.services.GitHubProfileService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubProfileServiceImpl implements GitHubProfileService {

    private final WebClient webClient = WebClient.create("https://api.github.com");

    public List<String> projectPartners(){
        return webClient.get()
                .uri("/repos/RomanGatek/erp-system/assignees")
                .retrieve()
                .bodyToMono(String.class)
                .map( body -> {
                    return jsonObjectList(new JSONArray(body), "login");
                }
                )
                .block();
    }

    @Override
    public List<Branch> projectBranches() {
        List<Branch> projectBranches = new ArrayList<>();
        return webClient.get()
                .uri("repos/RomanGatek/erp-system/branches")
                .retrieve()
                .bodyToMono(String.class)
                .map( body -> {
                    JSONArray jsonArray = new JSONArray(body);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String branchName = (jsonObject.getString("name"));
                        String branchAuthor = "";
                        String commitSha = (jsonObject.getJSONObject("commit").getString("sha"));
                        String commitMessage = commitShaMessage();
                        Branch branch = Branch.builder()
                                .name(branchName)
                                .author(branchAuthor)
                                .commit(Commit.builder()
                                        .sha(commitSha)
                                        .message(commitMessage)
                                        .build())
                                .build();
                        projectBranches.add(branch);
                    }return projectBranches;
                }).block();
    }

    private String commitShaMessage() {
        return "";
    }

    private List<String> jsonObjectList (JSONArray jsonArray, String key) {
        List<String> objectList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String value = jsonObject.getString(key);
            objectList.add(value);
        }
        return objectList;
    }

    @Override
    public GitHubProfile gitHubProfile(String username) {
        return webClient.get()
                .uri("/users/" + username)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> {
                    return gitHubProfile(new JSONObject(body));
                        }
                )
                .block(); // Blocking call, use async in reactive apps
    }

    private GitHubProfile gitHubProfile(JSONObject json) {
        return GitHubProfile.builder()
                .username(json.optString("login"))
                .name(json.optString("name"))
                .avatarUrl(json.optString("avatar_url"))
                .url(json.optString("html_url"))
                .userInformation(json.optString("bio"))
                .build();
    }


}
