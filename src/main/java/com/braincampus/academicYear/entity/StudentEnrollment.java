package com.braincampus.academicYear.entity;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.common.entity.BaseEntity;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "student_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_academic_year_tenant",
                        columnNames = {
                                "tenant_id",
                                "student_id",
                                "academic_year_id"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEnrollment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}