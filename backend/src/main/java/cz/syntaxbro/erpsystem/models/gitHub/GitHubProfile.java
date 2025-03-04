package cz.syntaxbro.erpsystem.models.gitHub;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;



@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class GitHubProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String name;
    @Column(name = "avatar_url")
    private String avatarUrl;
    private String url;
    @Column(name = "user_information")
    private String userInformation;
    @OneToMany(mappedBy = "git_hub_profile")
    private List<Branch> branch = new ArrayList<>();

}
