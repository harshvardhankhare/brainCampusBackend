package com.braincampus.management.dto;
import lombok.Builder;
import lombok.Getter;
import java.util.Set;

@Getter
@Builder
public class RolePermissionResponse {

    private Long roleId;

    private String role;

    private Set<String> permissions;
}