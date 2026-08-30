package com.braincampus.config;
import com.braincampus.auth.entity.Permission;
import com.braincampus.auth.entity.Role;
import com.braincampus.auth.repository.PermissionRepository;
import com.braincampus.common.enums.PermissionType;
import com.braincampus.common.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.braincampus.auth.repository.RoleRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    @Override
    public void run(String... args) {

        // 1. Create all permissions
        for (PermissionType permissionType : PermissionType.values()) {

            if (!permissionRepository.existsByName(permissionType)) {

                Permission permission = new Permission();

                permission.setName(permissionType);
                permission.setDescription(permissionType.name());

                permissionRepository.save(permission);
            }
        }

        // 2. Give all permissions to existing ADMIN roles
        List<Role> adminRoles =
                roleRepository.findByName(RoleType.ADMIN);

        Set<Permission> allPermissions =
                new HashSet<>(permissionRepository.findAll());

        for (Role role : adminRoles) {

            role.setPermissions(allPermissions);

            roleRepository.save(role);
        }
    }
}
