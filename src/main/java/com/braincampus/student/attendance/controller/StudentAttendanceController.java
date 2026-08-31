package com.braincampus.student.attendance.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.student.attendance.dto.AttendanceResponse;
import com.braincampus.student.attendance.dto.BulkAttendanceRequest;
import com.braincampus.student.attendance.service.StudentAttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentAttendanceController {

    private final StudentAttendanceService attendanceService;

    @PostMapping("/attendance")
    @PreAuthorize("hasAuthority('CREATE_STUDENT')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> markAttendance(
            @Valid @RequestBody BulkAttendanceRequest request
    ) {

        List<AttendanceResponse> attendance =
                attendanceService.markBulkAttendance(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<List<AttendanceResponse>>builder()
                                .success(true)
                                .message("Attendance marked successfully")
                                .data(attendance)
                                .build()
                );
    }

    @GetMapping("/{studentId}/attendance")
    @PreAuthorize("hasAuthority('VIEW_STUDENT')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendance(
            @PathVariable Long studentId
    ) {

        List<AttendanceResponse> attendance =
                attendanceService.getStudentAttendance(studentId);

        return ResponseEntity.ok(
                ApiResponse.<List<AttendanceResponse>>builder()
                        .success(true)
                        .message("Attendance fetched successfully")
                        .data(attendance)
                        .build()
        );
    }

    @GetMapping("/{studentId}/attendance/range")
    @PreAuthorize("hasAuthority('VIEW_STUDENT')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendanceBetween(
            @PathVariable Long studentId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {

        List<AttendanceResponse> attendance =
                attendanceService.getStudentAttendanceBetween(
                        studentId,
                        from,
                        to
                );

        return ResponseEntity.ok(
                ApiResponse.<List<AttendanceResponse>>builder()
                        .success(true)
                        .message("Attendance fetched successfully")
                        .data(attendance)
                        .build()
        );
    }
}
