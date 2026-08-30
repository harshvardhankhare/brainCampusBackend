package com.braincampus.auth.controller;
import com.braincampus.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.braincampus.security.SecurityUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@RestController
public class AuthorizationTestController {

    @GetMapping("/test/student-view")
    @PreAuthorize("hasAuthority('VIEW_STUDENT')")
    public ResponseEntity<ApiResponse<String>> studentView() {

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("You have VIEW_STUDENT permission")
                        .data("Student view access granted")
                        .build()
        );
    }

    @GetMapping("/test/student-create")
    @PreAuthorize("hasAuthority('CREATE_STUDENT')")
    public ResponseEntity<ApiResponse<String>> studentCreate() {

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("You have CREATE_STUDENT permission")
                        .data("Student create access granted")
                        .build()
        );
    }

    @GetMapping("/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminOnly() {

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("You are an ADMIN")
                        .data("Admin access granted")
                        .build()
        );
    }
    @GetMapping("/test/authorities")
    public ResponseEntity<ApiResponse<Collection<? extends GrantedAuthority>>> authorities() {

        return ResponseEntity.ok(
                ApiResponse.<Collection<? extends GrantedAuthority>>builder()
                        .success(true)
                        .message("Current authorities")
                        .data(SecurityUtils.getCurrentUser().getAuthorities())
                        .build()
        );
    }
}
