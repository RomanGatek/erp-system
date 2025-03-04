package cz.syntaxbro.erpsystem.repositories.github;

import cz.syntaxbro.erpsystem.models.gitHub.GitHubProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitHubProfileRepository extends JpaRepository<GitHubProfile, Long> {
}
