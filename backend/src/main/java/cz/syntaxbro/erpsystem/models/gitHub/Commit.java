package cz.syntaxbro.erpsystem.models.gitHub;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String sha;
    private String message;
    @OneToOne(mappedBy = "commit")
    private Commit commit;
}
