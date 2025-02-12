package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<Role> getAllRoles();

    Optional<Role> getRoleById(Long id);

    Optional<Role> getRoleByName(String name);

    Optional<Role> createRole(String name);

    Optional<Role> updateRole(Long id, String newName);

    void deleteRole(Long id);

    void deleteRoleByName(String name);

    Role assignPermissionToRole(Long roleId, Long permissionId);

    Role removePermissionFromRole(Long roleId, Long permissionId);

    Role assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}