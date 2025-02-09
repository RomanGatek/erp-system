package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Permission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Test: Save and load permissions by ID.
     */
    @Test
    void saveAndFindById_shouldReturnPermission() {
        Permission permission = new Permission();
        permission.setName("READ_PRIVILEGES");

        Permission savedPermission = permissionRepository.save(permission);
        Optional<Permission> foundPermission = permissionRepository.findById(savedPermission.getId());

        assertThat(foundPermission).isPresent();
        assertThat(foundPermission.get().getName()).isEqualTo("READ_PRIVILEGES");
    }

    /**
     * Test: Find permission by name.
     */
    @Test
    void findByName_shouldReturnPermission_whenExists() {
        Permission permission = new Permission();
        permission.setName("WRITE_PRIVILEGES");
        permissionRepository.save(permission);

        Optional<Permission> foundPermission = permissionRepository.findByName("WRITE_PRIVILEGES");

        assertThat(foundPermission).isPresent();
        assertThat(foundPermission.get().getName()).isEqualTo("WRITE_PRIVILEGES");
    }

    /**
     * Test: Find permission by name if it doesn't exist.
     */
    @Test
    void findByName_shouldReturnEmpty_whenNotExists() {
        Optional<Permission> foundPermission = permissionRepository.findByName("NON_EXISTENT_PRIVILEGE");

        assertThat(foundPermission).isEmpty();
    }

    /**
     * Test: Deleting permissions.
     */
    @Test
    void deletePermission_shouldRemoveFromDatabase() {
        Permission permission = new Permission();
        permission.setName("DELETE_PRIVILEGES");
        Permission savedPermission = permissionRepository.save(permission);

        permissionRepository.deleteById(savedPermission.getId());

        Optional<Permission> foundPermission = permissionRepository.findById(savedPermission.getId());
        assertThat(foundPermission).isEmpty();
    }
}
