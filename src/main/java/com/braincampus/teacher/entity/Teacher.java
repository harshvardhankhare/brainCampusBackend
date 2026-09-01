package com.braincampus.teacher.entity;

import com.braincampus.auth.entity.Tenant;
import com.braincampus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "teachers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_teacher_employee_code_tenant",
                        columnNames = {
                                "tenant_id",
                                "employee_code"
                        }
                ),
                @UniqueConstraint(
                        name = "uk_teacher_email_tenant",
                        columnNames = {
                                "tenant_id",
                                "email"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends BaseEntity {

    @Column(name = "employee_code", nullable = false, length = 50)
    private String employeeCode;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}