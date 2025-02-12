package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Permission;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.repositories.PermissionRepository;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.services.RoleService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Optional<Role> createRole(String name) {
        if (roleRepository.existsByName(name)) {
            return Optional.empty();
        }
        Role role = new Role(name, null);
        return Optional.of(roleRepository.save(role));
    }

    @Override
    public Optional<Role> updateRole(Long id, String newName) {
        return roleRepository.findById(id)
                .map(role -> {
                    role.setName(newName);
                    return roleRepository.save(role);
                });
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    public void deleteRoleByName(String name) {
        roleRepository.findByName(name)
                .ifPresent(roleRepository::delete);
    }

    @Override
    @Transactional
    public Role assignPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        role.addPermission(permission);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        role.removePermission(permission);
        return roleRepository.save(role);
    }

    // Method for assigning multiple permissions to a role
    @Transactional
    public Role assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        role.getPermissions().addAll(permissions);

        return roleRepository.save(role);
    }
}