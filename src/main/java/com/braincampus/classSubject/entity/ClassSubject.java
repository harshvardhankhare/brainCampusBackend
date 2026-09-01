package com.braincampus.classSubject.entity;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.common.entity.BaseEntity;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.subject.entity.Subject;
import com.braincampus.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "class_subjects",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_class_subject_year_tenant",
                        columnNames = {
                                "tenant_id",
                                "class_id",
                                "subject_id",
                                "academic_year"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSubject extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Builder.Default
    @Column(name = "weekly_periods", nullable = false)
    private Integer weeklyPeriods = 5;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}