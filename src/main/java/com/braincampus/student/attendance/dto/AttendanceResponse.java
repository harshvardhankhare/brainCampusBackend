package com.braincampus.student.attendance.dto;
import com.braincampus.student.attendance.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AttendanceResponse {

    private Long id;

    private Long studentId;

    private String roleNumber;

    private String studentName;

    private LocalDate attendanceDate;

    private AttendanceStatus status;

    private String remarks;

    private String schoolCode;
}
