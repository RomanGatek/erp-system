package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Permission;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.repositories.PermissionRepository;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.services.RoleService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;

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
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + id));
    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with name: " + name));
    }

    @Override
    public Role createRole(String name) {
        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Role with this name already exists: " + name);
        }
        Role role = new Role(name);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role updateRole(Long id, String newName) {
        Role role = getRoleById(id);

        if (!role.getName().equals(newName)) {
            throw new IllegalArgumentException("Role with this name already exists: " + newName);
        }

        role.setName(newName);
        return roleRepository.save(role);
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
        Role role = getRoleById(roleId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }

        role.getPermissions().add(permission);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = getRoleById(roleId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        if (role.getPermissions() != null) {
            role.getPermissions().remove(permission);
        }
        return roleRepository.save(role);
    }

    // Method for assigning multiple permissions to a role
    @Override
    @Transactional
    public Role assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = getRoleById(roleId);
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }

        role.getPermissions().addAll(permissions);
        return roleRepository.save(role);
    }
}