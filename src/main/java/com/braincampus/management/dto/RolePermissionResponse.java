package com.braincampus.management.dto;

import com.braincampus.common.enums.PermissionType;
import lombok.Builder;
import lombok.Getter;
import java.util.Set;

@Getter
@Builder
public class RolePermissionResponse {

    private Long roleId;

    private String role;

    private Set<PermissionType> permissions;
}