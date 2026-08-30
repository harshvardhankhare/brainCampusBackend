package com.braincampus.student.repository;
import com.braincampus.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByTenantId(Long tenantId);

    Optional<Student> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<Student> findByRoleNumberAndTenantId(
            String roleNumber,
            Long tenantId
    );

    boolean existsByRoleNumberAndTenantId(
            String roleNumber,
            Long tenantId
    );
}