package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.gitHub.Branch;
import cz.syntaxbro.erpsystem.models.gitHub.Commit;
import cz.syntaxbro.erpsystem.models.gitHub.GitHubProfile;
import cz.syntaxbro.erpsystem.repositories.github.BranchRepository;
import cz.syntaxbro.erpsystem.repositories.github.CommitRepository;
import cz.syntaxbro.erpsystem.repositories.github.GitHubProfileRepository;
import cz.syntaxbro.erpsystem.services.GitHubProfileService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubProfileServiceImpl implements GitHubProfileService {

    private final GitHubProfileRepository gitHubProfileRepository;
    private final BranchRepository branchRepository;
    private final CommitRepository commitRepository;

    private final WebClient webClient = WebClient.create("https://api.github.com");

    private String commitAuthor;
    private String commitMessage;

    @Autowired
    public GitHubProfileServiceImpl(GitHubProfileRepository gitHubProfileRepository, BranchRepository branchRepository, CommitRepository commitRepository) {
        this.gitHubProfileRepository = gitHubProfileRepository;
        this.branchRepository = branchRepository;
        this.commitRepository = commitRepository;
    }

    @Override
    //Collect all gitHub Data by branch author and commits. Finally save all gitHubProfiles.
    public void createGitHubProfile(){
        List<Branch> projectBranches = projectBranches();
        List<String> partners = projectPartners();
        for(String partner : partners){
            for (Branch branch : projectBranches) {
                if(branch.getAuthor().equals(partner)){
                    GitHubProfile gitHubProfile = gitHubProfile(partner);
                    gitHubProfile.getBranch().add(branch);
                }
            }
            gitHubProfileRepository.save(gitHubProfile(partner));
        }
    }

    //get users whits are working with project
    private List<String> projectPartners(){
        return webClient.get()
                .uri("/repos/RomanGatek/erp-system/assignees")
                .retrieve()
                .bodyToMono(String.class)
                .map( body -> {
                    return getPartnersFromJsonObject(new JSONArray(body), "login");
                }
                )
                .block();
    }

    //Create a list with partners of project
    private List<String> getPartnersFromJsonObject(JSONArray jsonArray, String key) {
        List<String> objectList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String value = jsonObject.getString(key);
            objectList.add(value);
        }
        return objectList;
    }

    //Get all branches from db
    private List<Branch> projectBranches() {
        return webClient.get()
                .uri("repos/RomanGatek/erp-system/branches")
                .retrieve()
                .bodyToMono(String.class)
                .publishOn(Schedulers.boundedElastic())
                .map( body -> {
                    JSONArray jsonArray = new JSONArray(body);
                    //save all branches to db
                    for (int i = 0; i < jsonArray.length(); i++) {
                        getBranchFromJsonArray(jsonArray, i);
                    }return branchRepository.findAll();
                }).block();
    }

    //Get Branch from JsonArray with index, build commit by sha and save them to DB
    private void getBranchFromJsonArray(JSONArray jsonArray, int indexOfObject){
        JSONObject jsonObject = jsonArray.getJSONObject(indexOfObject);
        String branchName = jsonObject.getString("name");
        String commitSha = jsonObject.getJSONObject("commit").getString("sha");
        //Set commitAuthor and commitMessage for use
        setCommitDetails(commitSha);
        String branchAuthor = this.commitAuthor;
        String commitMessage = this.commitMessage;
        Commit savedCommit = createCommit(commitSha, commitMessage);
        createBranch(branchName, branchAuthor, savedCommit);
    }

    //Connect to GitHub Api with repos and set commitAuthor and commitMessage for use
    private void setCommitDetails(String sha) {
        webClient.get()
                .uri("/repos/RomanGatek/erp-system/commits/" + sha)
                .retrieve()
                .bodyToMono(String.class)
                .mapNotNull(body -> {
                    try {
                        JSONObject shaJsonObject = new JSONObject(body);
                        JSONObject commitObject = shaJsonObject.optJSONObject("commit");
                        if (commitObject != null) {
                            this.commitAuthor = getAuthorNameFromCommit(commitObject);
                            this.commitMessage = getCommitShaMessage(commitObject);
                        } else {
                            System.err.println("Commit object is missing in JSON response.");
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing commit JSON: " + e.getMessage());
                    }
                    return null;
                })
                .subscribe();
    }

    //Get a commit author name with sha name from JSONObject:commit / JSONObject:author/ name
    //https://api.github.com/repos/RomanGatek/erp-system/commits/ + sha
    private String getAuthorNameFromCommit(JSONObject shaCommitObject){
        JSONObject authorObject = shaCommitObject.getJSONObject("author");
        return authorObject.optString("name");
    }

    //Get a commit message with sha name from JSONObject:commit / message
    //https://api.github.com/repos/RomanGatek/erp-system/commits/ + sha
    private String getCommitShaMessage(JSONObject shaCommitObject) {
        return shaCommitObject.optString("message");
    }

    //Build a commit and save it to DB
    private Commit createCommit(String commitSha, String commitMessage){
        Commit commit = Commit.builder()
                .sha(commitSha)
                .message(commitMessage)
                .build();
        return commitRepository.save(commit);
    }

    //Build Branch and save it to DB
    private void createBranch(String branchName, String branchAuthor, Commit commit){
        Branch branch = Branch.builder()
                .name(branchName)
                .author(branchAuthor)
                .commit(commit)
                .build();
        branchRepository.save(branch);
    }

    //Connect to GitHub Api https://api.github.com/users/ + username
    private GitHubProfile gitHubProfile(String username) {
        return webClient.get()
                .uri("/users/" + username)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> {
                    return gitHubProfile(new JSONObject(body));
                        }
                )
                .block();
    }

    //build GitHubProfile object for partner from Api
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
