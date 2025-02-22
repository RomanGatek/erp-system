package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Role;

import java.util.List;

public interface RoleService {

    List<Role> getAllRoles();

    Role getRoleById(Long id);

    Role getRoleByName(String name);

    Role createRole(String name);

    Role updateRole(Long id, String newName);

    void deleteRole(Long id);

    void deleteRoleByName(String name);

    Role assignPermissionToRole(Long roleId, Long permissionId);

    Role removePermissionFromRole(Long roleId, Long permissionId);

    Role assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}