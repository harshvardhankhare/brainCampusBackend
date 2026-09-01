package com.braincampus.management.entity;

import com.braincampus.common.entity.BaseEntity;
import com.braincampus.management.staff.entity.Staff;
import com.braincampus.auth.entity.Tenant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "salary_structures",
        indexes = {
                @Index(
                        name = "idx_salary_structure_staff_tenant",
                        columnList = "tenant_id, staff_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructure extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(
            name = "basic_salary",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal basicSalary;

    @Builder.Default
    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal allowances = BigDecimal.ZERO;

    @Builder.Default
    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}