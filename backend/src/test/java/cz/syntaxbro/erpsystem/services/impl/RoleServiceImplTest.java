package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Permission;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.repositories.PermissionRepository;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private Permission permission;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "ROLE_TEST", new HashSet<>());
        permission = new Permission(1L, "TEST_PERMISSION", new HashSet<>());
    }

    @Test
    void getAllRoles_ShouldReturnListOfRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(role));

        List<Role> roles = roleService.getAllRoles();

        assertThat(roles).isNotNull().hasSize(1);
        assertThat(roles.get(0).getName()).isEqualTo("ROLE_TEST");

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void getRoleById_ShouldReturnRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role foundRole = roleService.getRoleById(1L);

        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getName()).isEqualTo("ROLE_TEST");

        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void getRoleById_ShouldThrowException_WhenRoleDoesNotExist() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roleService.getRoleById(1L));

        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void getRoleByName_ShouldReturnRole() {
        when(roleRepository.findByName("ROLE_TEST")).thenReturn(Optional.of(role));

        Role foundRole = roleService.getRoleByName("ROLE_TEST");

        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getName()).isEqualTo("ROLE_TEST");

        verify(roleRepository, times(1)).findByName("ROLE_TEST");
    }

    @Test
    void getRoleByName_ShouldThrowException_WhenRoleDoesNotExist() {
        when(roleRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roleService.getRoleByName("UNKNOWN"));

        verify(roleRepository, times(1)).findByName("UNKNOWN");
    }

    @Test
    void createRole_ShouldReturnNewRole() {
        when(roleRepository.existsByName("ROLE_NEW")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(new Role(2L, "ROLE_NEW", new HashSet<>()));

        Role createdRole = roleService.createRole("ROLE_NEW");

        assertThat(createdRole).isNotNull();
        assertThat(createdRole.getName()).isEqualTo("ROLE_NEW");

        verify(roleRepository, times(1)).existsByName("ROLE_NEW");
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void createRole_ShouldThrowException_WhenRoleAlreadyExists() {
        when(roleRepository.existsByName("ROLE_TEST")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> roleService.createRole("ROLE_TEST"));

        verify(roleRepository, times(1)).existsByName("ROLE_TEST");
    }

    @Test
    void deleteRoleByName_ShouldDeleteRole_WhenRoleExists() {
        when(roleRepository.findByName("ROLE_TEST")).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).delete(role);

        roleService.deleteRoleByName("ROLE_TEST");

        verify(roleRepository, times(1)).findByName("ROLE_TEST");
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void assignPermissionToRole_ShouldAddPermission() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role updatedRole = roleService.assignPermissionToRole(1L, 1L);

        assertThat(updatedRole.getPermissions()).contains(permission);

        verify(roleRepository, times(1)).findById(1L);
        verify(permissionRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void assignPermissionsToRole_ShouldAssignMultiplePermissions() {
        List<Long> permissionIds = List.of(1L, 2L);
        Permission permission2 = new Permission(2L, "ANOTHER_PERMISSION", new HashSet<>());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findAllById(permissionIds)).thenReturn(List.of(permission, permission2));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role updatedRole = roleService.assignPermissionsToRole(1L, permissionIds);

        assertThat(updatedRole.getPermissions()).contains(permission, permission2);

        verify(roleRepository, times(1)).findById(1L);
        verify(permissionRepository, times(1)).findAllById(permissionIds);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void removePermissionFromRole_ShouldRemovePermission() {
        role.getPermissions().add(permission);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role updatedRole = roleService.removePermissionFromRole(1L, 1L);

        assertThat(updatedRole.getPermissions()).doesNotContain(permission);

        verify(roleRepository, times(1)).findById(1L);
        verify(permissionRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void shouldThrowExceptionWhenRoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roleService.getRoleById(1L));
    }
}