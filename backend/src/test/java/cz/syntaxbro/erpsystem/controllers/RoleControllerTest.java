package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Role;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "ROLE_TEST", new HashSet<>());
    }

    @Test
    void getAllRoles_ShouldReturnListOfRoles() {
        when(roleService.getAllRoles()).thenReturn(List.of(role));

        ResponseEntity<List<Role>> response = roleController.getAllRoles();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull().hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("ROLE_TEST");

        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    void getRoleById_ShouldReturnRole() {
        when(roleService.getRoleById(1L)).thenReturn(role);

        ResponseEntity<Role> response = roleController.getRoleById(1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("ROLE_TEST");

        verify(roleService, times(1)).getRoleById(1L);
    }

    @Test
    void getRoleByName_ShouldReturnRole() {
        when(roleService.getRoleByName("ROLE_TEST")).thenReturn(role);

        ResponseEntity<Role> response = roleController.getRoleByName("ROLE_TEST");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("ROLE_TEST");

        verify(roleService, times(1)).getRoleByName("ROLE_TEST");
    }

    @Test
    void createRole_ShouldReturnCreatedRole() {
        when(roleService.createRole(role.getName())).thenReturn(role);

        ResponseEntity<Role> response = roleController.createRole(role);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("ROLE_TEST");

        verify(roleService, times(1)).createRole("ROLE_TEST");
    }

    @Test
    void deleteRole_ShouldReturnNoContent_WhenRoleExists() {
        doNothing().when(roleService).deleteRole(1L);

        ResponseEntity<Void> response = roleController.deleteRole(1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        verify(roleService, times(1)).deleteRole(1L);
    }

    @Test
    void addPermissionToRole_ShouldReturnUpdatedRole() {
        when(roleService.assignPermissionToRole(1L, 1L)).thenReturn(role);

        ResponseEntity<Role> response = roleController.addPermissionToRole(1L, 1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        verify(roleService, times(1)).assignPermissionToRole(1L, 1L);
    }

    @Test
    void removePermissionFromRole_ShouldReturnUpdatedRole() {
        when(roleService.removePermissionFromRole(1L, 1L)).thenReturn(role);

        ResponseEntity<Role> response = roleController.removePermissionFromRole(1L, 1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        verify(roleService, times(1)).removePermissionFromRole(1L, 1L);
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