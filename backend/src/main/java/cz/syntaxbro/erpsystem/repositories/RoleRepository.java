package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find a role by name
    Optional<Role> findByName(String name);

    // Check if a role exists (e.g. for validation when creating a user)
    boolean existsByName(String name);


}