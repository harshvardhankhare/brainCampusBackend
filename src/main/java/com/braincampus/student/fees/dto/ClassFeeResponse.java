package com.braincampus.student.fees.dto;

import com.braincampus.student.fees.FeeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassFeeResponse {

    private Long classId;
    private String className;
    private String section;
    private String academicYear;
    private FeeType feeType;
    private Integer feeMonth;
    private BigDecimal amount;
    private Integer totalStudents;
    private Integer feesCreated;
    private Integer feesSkipped;
    private List<StudentFeeResponse> fees;
}
