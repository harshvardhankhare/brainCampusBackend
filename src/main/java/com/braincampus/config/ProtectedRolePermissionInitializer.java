package com.braincampus.config;

import com.braincampus.management.service.ProtectedRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProtectedRolePermissionInitializer implements ApplicationRunner {

    private final ProtectedRolePermissionService protectedRolePermissionService;

    @Override
    public void run(ApplicationArguments args) {
        protectedRolePermissionService.synchronizeAllPermissions();
    }
}
