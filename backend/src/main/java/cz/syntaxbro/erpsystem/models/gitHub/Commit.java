package cz.syntaxbro.erpsystem.models.gitHub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Commit {
    private String sha;
    private String message;
}
