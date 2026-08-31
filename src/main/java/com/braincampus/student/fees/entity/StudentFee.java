package com.braincampus.student.fees.entity;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.common.entity.BaseEntity;
import com.braincampus.student.entity.Student;
import com.braincampus.student.fees.FeeType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "student_fees",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_fee_period",
                        columnNames = {
                                "tenant_id",
                                "student_id",
                                "academic_year",
                                "fee_type",
                                "fee_month"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFee extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, length = 30)
    private FeeType feeType;

    @Column(name = "fee_month")
    private Integer feeMonth;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(length = 500)
    private String description;
}
