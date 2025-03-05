package cz.syntaxbro.erpsystem.models.gitHub;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String author;
    @OneToMany(mappedBy = "branch")
    private List<Commit> commits;
    @ManyToOne
    @JoinColumn(name = "gitHubProfile_id", referencedColumnName = "id")
    private GitHubProfile gitHubProfile;
}
