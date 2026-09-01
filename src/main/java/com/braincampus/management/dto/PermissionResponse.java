package com.braincampus.management.dto;
import com.braincampus.common.enums.PermissionType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionResponse {

    private PermissionType name;

    private String description;
}