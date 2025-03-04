package cz.syntaxbro.erpsystem.repositories.github;

import cz.syntaxbro.erpsystem.models.gitHub.Commit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitRepository extends CrudRepository<Commit, Long> {
}
