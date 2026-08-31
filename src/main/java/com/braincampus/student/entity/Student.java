package com.braincampus.student.entity;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.braincampus.schoolClass.entity.SchoolClass;

import java.time.LocalDate;

@Entity
@Table(
        name = "students",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_role_tenant",
                        columnNames = {"tenant_id", "role_number"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {

    @Column(name = "role_number", nullable = false, length = 50)
    private String roleNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 500)
    private String address;

    @Column(name = "parent_name", length = 150)
    private String parentName;

    @Column(name = "parent_phone", length = 20)
    private String parentPhone;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;
}
