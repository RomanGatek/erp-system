package cz.syntaxbro.erpsystem.models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for the Role entity.
 * Ensures that validation constraints work correctly and that the Role behaves as expected.
 */
class RoleTest {

    private Validator validator;

    /**
     * Initializes the validator before each test execution.
     * This validator will check if the constraints on Role attributes are respected.
     */
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test to verify that a valid role is created without validation errors.
     */
    @Test
    void shouldCreateValidRole() {
        // Arrange: Create a valid Role object
        Role role = new Role("ROLE_USER");

        // Act: Validate the role
        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        // Assert: No validation errors should be found
        assertThat(violations).isEmpty();
    }

    /**
     * Test to verify that a role with a blank name fails validation.
     */
    @Test
    void shouldFailWhenRoleNameIsBlank() {
        // Arrange: Create a Role object with an invalid blank name
        Role role = new Role("");

        // Act: Validate the role
        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        // Assert: The validation should fail due to an empty role name
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Role name cannot be empty");
    }

    /**
     * Test to verify that a role can store and retrieve permissions correctly.
     */
    @Test
    void shouldAddPermissionsToRole() {
        // Arrange: Create permissions
        Permission readPermission = new Permission("READ_PRIVILEGES");
        Permission writePermission = new Permission("WRITE_PRIVILEGES");

        Set<Permission> permissions = new HashSet<>();
        permissions.add(readPermission);
        permissions.add(writePermission);

        // Create a Role object and assign permissions
        Role role = new Role("ROLE_ADMIN", permissions);

        // Act & Assert: Verify the role correctly stores permissions
        assertThat(role.getPermissions()).isNotNull();
        assertThat(role.getPermissions()).hasSize(2);
        assertThat(role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder("READ_PRIVILEGES", "WRITE_PRIVILEGES");
    }

    /**
     * Test to ensure that a role without explicitly set permissions initializes an empty permission set.
     */
    @Test
    void shouldInitializeWithEmptyPermissionSet() {
        // Arrange: Create a role without any permissions
        Role role = new Role("ROLE_MANAGER");

        // Act & Assert: Ensure the permissions set is initialized and empty
        assertThat(role.getPermissions()).isNotNull();
        assertThat(role.getPermissions()).isEmpty();
    }
}