package com.braincampus.academicYear.entity;

import com.braincampus.auth.entity.Tenant;
import com.braincampus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "academic_years",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_academic_year_tenant",
                        columnNames = {"tenant_id", "name"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYear extends BaseEntity {

    @Column(nullable = false, length = 20)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}