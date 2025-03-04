package cz.syntaxbro.erpsystem.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GitHubProfile {

    private String username;
    private String userInformation;
    private List<String> branches;
    private String commits;

}
