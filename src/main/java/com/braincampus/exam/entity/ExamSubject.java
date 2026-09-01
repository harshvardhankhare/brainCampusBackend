package com.braincampus.exam.entity;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.classSubject.entity.ClassSubject;
import com.braincampus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "exam_subjects",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_exam_class_subject_tenant",
                        columnNames = {
                                "tenant_id",
                                "exam_id",
                                "class_subject_id"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubject extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_subject_id", nullable = false)
    private ClassSubject classSubject;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(name = "max_marks", nullable = false)
    private Integer maxMarks;

    @Column(name = "passing_marks", nullable = false)
    private Integer passingMarks;

    @Column(length = 500)
    private String remarks;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}