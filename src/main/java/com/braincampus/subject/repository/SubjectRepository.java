package com.braincampus.subject.repository;
import com.braincampus.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findAllByTenantId(Long tenantId);

    Optional<Subject> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<Subject> findByCodeAndTenantId(
            String code,
            Long tenantId
    );

    boolean existsByCodeAndTenantId(
            String code,
            Long tenantId
    );
}