package com.braincampus.auth.entity;
import com.braincampus.common.entity.BaseEntity;
import com.braincampus.common.enums.PermissionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PermissionType name;

    @Column(length = 255)
    private String description;
}
