package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private Role testRole;

    /**
     * Setup: Creates a role before each test.
     */
    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setName("ROLE_USER");
        roleRepository.save(testRole);
    }

    /**
     * Test case: Find a role by name.
     * Expected result: The role should be found.
     */
    @Test
    void findByName_shouldReturnRole_whenExists() {
        Optional<Role> foundRole = roleRepository.findByName("ROLE_USER");

        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo("ROLE_USER");
    }

    /**
     * Test case: Find a non-existing role.
     * Expected result: The result should be empty.
     */
    @Test
    void findByName_shouldReturnEmpty_whenRoleDoesNotExist() {
        Optional<Role> foundRole = roleRepository.findByName("ROLE_ADMIN");

        assertThat(foundRole).isEmpty();
    }

    /**
     * Test case: Save a new role.
     * Expected result: The role should be saved and retrievable.
     */
    @Test
    void save_shouldPersistRole() {
        Role newRole = new Role();
        newRole.setName("ROLE_MANAGER");

        roleRepository.save(newRole);

        Optional<Role> foundRole = roleRepository.findByName("ROLE_MANAGER");
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo("ROLE_MANAGER");
    }

    /**
     * Test case: Delete an existing role.
     * Expected result: The role should be removed from the database.
     */
    @Test
    void delete_shouldRemoveRole() {
        roleRepository.delete(testRole);

        Optional<Role> foundRole = roleRepository.findByName("ROLE_USER");
        assertThat(foundRole).isEmpty();
    }
}