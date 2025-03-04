package cz.syntaxbro.erpsystem.models.gitHub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Branch {
    private String name;
    private String author;
    private Commit commit;
}
