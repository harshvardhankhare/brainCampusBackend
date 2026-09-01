package com.braincampus.management.dto;
import com.braincampus.common.enums.PermissionType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RolePermissionRequest {

    @NotEmpty(message = "Permissions cannot be empty")
    private Set<PermissionType> permissions;
}