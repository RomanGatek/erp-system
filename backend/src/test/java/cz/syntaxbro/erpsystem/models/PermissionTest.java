package cz.syntaxbro.erpsystem.models;

import cz.syntaxbro.erpsystem.repositories.PermissionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class PermissionTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Test: Ensures that a permission can be saved and retrieved from the database.
     */
    @Test
    void saveAndRetrievePermission_ShouldPersistCorrectly() {
        // Arrange: Create and save a new permission
        Permission permission = new Permission("APPROVE_BUDGETS");
        Permission savedPermission = permissionRepository.save(permission);
        entityManager.flush();

        // Act: Retrieve the permission from the database
        Permission foundPermission = permissionRepository.findById(savedPermission.getId()).orElse(null);

        // Assert: Verify the permission was correctly saved and retrieved
        assertThat(foundPermission).isNotNull();
        assertThat(foundPermission.getId()).isEqualTo(savedPermission.getId());
        assertThat(foundPermission.getName()).isEqualTo("APPROVE_BUDGETS");
    }

    /**
     * Test: Ensures that the unique constraint on 'name' prevents duplicates.
     */
    @Test
    void saveDuplicatePermission_ShouldFailDueToUniqueConstraint() {
        // Arrange: Save the first permission
        Permission permission1 = new Permission("WRITE_PRIVILEGES");
        permissionRepository.save(permission1);
        entityManager.flush();

        // Act & Assert: Try to save a duplicate permission and expect a DataIntegrityViolationException
        Permission permission2 = new Permission("WRITE_PRIVILEGES");

        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            permissionRepository.save(permission2);
            entityManager.flush();
        });

        assertThat(exception).isNotNull();
    }
}
