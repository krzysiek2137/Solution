package se.visionmate.solution.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.visionmate.solution.exposure.model.RoleRepresentation;
import se.visionmate.solution.model.Permission;
import se.visionmate.solution.model.Role;
import se.visionmate.solution.repository.PermissionRepository;
import se.visionmate.solution.repository.RoleRepository;
import se.visionmate.solution.utils.ResourceException;

@Service
public class RoleService {
    @Value("${admin.role.name}")
    private String adminName;

    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public void createRole(RoleRepresentation roleRepresentation) {
        Role role = new Role();
        role.setRoleName(roleRepresentation.getRoleName());
        role.setPermissions(preparePermissionListToModify(roleRepresentation.getPermissions()));
        roleRepository.save(role);
    }

    public List<RoleRepresentation> getRoles() {
        List<RoleRepresentation> roleRepresentations = new ArrayList();
        roleRepository.findAll().forEach(role -> {
            List<String> permissionRepresentations = new ArrayList();
            role.getPermissions().forEach(permission -> {
                permissionRepresentations.add(permission.getPermissionName());
            });
            roleRepresentations.add(new RoleRepresentation(role.getRoleName(), permissionRepresentations));
        });
        return roleRepresentations;
    }

    @Transactional
    public void deleteRole(String roleName) {
        if (roleName.equals(adminName)) {
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR, "Not allow to remove ADMIN role");
        }
        roleRepository.deleteByRoleName(roleName);
    }

    @Transactional
    public void updateRole(RoleRepresentation roleRepresentation){
        Optional<Role> role = roleRepository.findRoleByRoleName(roleRepresentation.getRoleName());
        if (!role.isPresent()) {
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR, "Role " + roleRepresentation.getRoleName() + " not found");
        }
        role.get().setPermissions(preparePermissionListToModify(roleRepresentation.getPermissions()));
        roleRepository.save(role.get());
    }

    private List<Permission> preparePermissionListToModify(List<String> permissionRepresenstations) {
        List<Permission> rolePermissions = new ArrayList<>();
        List<Permission> dbPermissions = new ArrayList<>();
        permissionRepository.findAll().forEach(dbPermissions::add);
        dbPermissions.forEach(permission -> {
            if (permissionRepresenstations.contains(permission.getPermissionName())) {
                rolePermissions.add(permission);
            }
        });
        return rolePermissions;
    }

}
