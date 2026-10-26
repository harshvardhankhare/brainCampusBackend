package com.braincampus.management.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;
@Getter
@Setter
public class RolePermissionRequest {

    @NotEmpty(message = "Permissions cannot be empty")
    private Set<String> permissions;
}