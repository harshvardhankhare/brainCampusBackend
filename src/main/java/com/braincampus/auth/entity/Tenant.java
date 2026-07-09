package com.braincampus.auth.entity;

import com.braincampus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends BaseEntity {

    @Column(nullable = false)
    private String schoolName;

    @Column(nullable = false, unique = true)
    private String schoolCode;

    @Column(unique = true)
    private String subdomain;

    private String email;

    private String phone;

    private String logo;

    @Builder.Default
    private Boolean active = true;
}
