package com.braincampus.exam.repository;
import com.braincampus.exam.entity.StudentResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentResultRepository
        extends JpaRepository<StudentResult, Long> {

    List<StudentResult> findAllByTenantId(Long tenantId);

    List<StudentResult> findAllByStudentIdAndTenantId(
            Long studentId,
            Long tenantId
    );

    List<StudentResult> findAllByExamSubjectIdAndTenantId(
            Long examSubjectId,
            Long tenantId
    );

    Optional<StudentResult> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<StudentResult> findByStudentIdAndExamSubjectIdAndTenantId(
            Long studentId,
            Long examSubjectId,
            Long tenantId
    );

    boolean existsByStudentIdAndExamSubjectIdAndTenantId(
            Long studentId,
            Long examSubjectId,
            Long tenantId
    );
}