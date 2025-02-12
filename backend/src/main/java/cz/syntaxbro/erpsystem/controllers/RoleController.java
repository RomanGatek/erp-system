package cz.syntaxbro.erpsystem.controllers;


import cz.syntaxbro.erpsystem.models.Permission;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.repositories.PermissionRepository;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.services.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleService roleService;

    public RoleController(RoleRepository roleRepository, PermissionRepository permissionRepository, RoleService roleService) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(roleRepository.save(role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        if (!roleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        roleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<Role> addPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        role.getPermissions().add(permission);
        return ResponseEntity.ok(roleRepository.save(role));
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<Role> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        return ResponseEntity.ok(roleRepository.save(role));
    }

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<Role> assignPermissionsToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        return ResponseEntity.ok(roleService.assignPermissionsToRole(roleId, permissionIds));
    }
}