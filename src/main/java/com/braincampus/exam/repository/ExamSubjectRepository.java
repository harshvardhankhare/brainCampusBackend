package com.braincampus.exam.repository;
import com.braincampus.exam.entity.ExamSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamSubjectRepository
        extends JpaRepository<ExamSubject, Long> {

    List<ExamSubject> findAllByTenantId(Long tenantId);

    List<ExamSubject> findAllByExamIdAndTenantId(
            Long examId,
            Long tenantId
    );

    Optional<ExamSubject> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    boolean existsByExamIdAndClassSubjectIdAndTenantId(
            Long examId,
            Long classSubjectId,
            Long tenantId
    );
}