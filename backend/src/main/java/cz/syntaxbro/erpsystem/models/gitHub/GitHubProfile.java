package cz.syntaxbro.erpsystem.models.gitHub;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GitHubProfile {

    private String username;
    private String name;
    private String avatarUrl;
    private String url;
    private String userInformation;
    private List<Object> branches;



}
