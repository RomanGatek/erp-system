package cz.syntaxbro.erpsystem.models.gitHub;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String name;
    private String author;
    @ManyToOne
    @JoinColumn(name = "commit_id", referencedColumnName = "id")
    private Commit commit;
    @ManyToOne
    @JoinColumn(name = "git_hub_profile_id", referencedColumnName = "id")
    private GitHubProfile gitHubProfile;
}
