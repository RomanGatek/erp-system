package cz.syntaxbro.erpsystem.repositories.github;

import cz.syntaxbro.erpsystem.models.gitHub.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    @Query("select b from Branch b where b.name = :name")
    Optional<Branch> findByName(@Param("name") String name);
}
