package com.braincampus.student.attendance.entity;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.common.entity.BaseEntity;
import com.braincampus.student.attendance.AttendanceStatus;
import com.braincampus.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "student_attendance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attendance_student_date",
                        columnNames = {"student_id", "attendance_date"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(length = 500)
    private String remarks;
}
