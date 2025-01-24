// REVIEWED

package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}