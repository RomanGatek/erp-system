package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Permission;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.repositories.PermissionRepository;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private Role role;
    private Permission permission;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "ROLE_TEST", new HashSet<>());
        permission = new Permission(1L, "TEST_PERMISSION");
    }

    @Test
    void getAllRoles_ShouldReturnListOfRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(role));

        ResponseEntity<List<Role>> response = roleController.getAllRoles();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull().hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("ROLE_TEST");

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void createRole_ShouldReturnCreatedRole() {
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        ResponseEntity<Role> response = roleController.createRole(role);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("ROLE_TEST");

        verify(roleRepository, times(1)).findByName("ROLE_TEST");
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void createRole_ShouldReturnBadRequest_WhenRoleExists() {
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));

        ResponseEntity<Role> response = roleController.createRole(role);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();

        verify(roleRepository, times(1)).findByName("ROLE_TEST");
        verify(roleRepository, times(0)).save(any(Role.class));
    }

    @Test
    void deleteRole_ShouldReturnNoContent_WhenRoleExists() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(roleRepository).deleteById(1L);

        ResponseEntity<Void> response = roleController.deleteRole(1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        verify(roleRepository, times(1)).existsById(1L);
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRole_ShouldReturnNotFound_WhenRoleDoesNotExist() {
        when(roleRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> response = roleController.deleteRole(1L);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();

        verify(roleRepository, times(1)).existsById(1L);
        verify(roleRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void addPermissionToRole_ShouldReturnUpdatedRole() {
        role.setPermissions(new HashSet<>());

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        ResponseEntity<Role> response = roleController.addPermissionToRole(1L, 1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPermissions()).contains(permission);
    }

    @Test
    void addPermissionToRole_ShouldReturnNotFound_WhenRoleDoesNotExist() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = null;
        try {
            roleController.addPermissionToRole(1L, 1L);
        } catch (RuntimeException e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Role not found");

        verify(roleRepository, times(1)).findById(1L);
        verify(permissionRepository, times(0)).findById(anyLong());
        verify(roleRepository, times(0)).save(any(Role.class));
    }

    @Test
    void removePermissionFromRole_ShouldReturnUpdatedRole() {
        role.setPermissions(new HashSet<>());
        role.getPermissions().add(permission);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        ResponseEntity<Role> response = roleController.removePermissionFromRole(1L, 1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPermissions()).doesNotContain(permission);
    }

    @Test
    void assignPermissionsToRole_ShouldReturnUpdatedRole() {
        List<Long> permissionIds = List.of(1L, 2L);
        when(roleService.assignPermissionsToRole(1L, permissionIds)).thenReturn(role);

        ResponseEntity<Role> response = roleController.assignPermissionsToRole(1L, permissionIds);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        verify(roleService, times(1)).assignPermissionsToRole(1L, permissionIds);
    }
}