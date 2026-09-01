package com.braincampus.teacher.repository;

import com.braincampus.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findAllByTenantId(Long tenantId);

    Optional<Teacher> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<Teacher> findByEmployeeCodeAndTenantId(
            String employeeCode,
            Long tenantId
    );

    Optional<Teacher> findByEmailAndTenantId(
            String email,
            Long tenantId
    );

    boolean existsByEmployeeCodeAndTenantId(
            String employeeCode,
            Long tenantId
    );

    boolean existsByEmailAndTenantId(
            String email,
            Long tenantId
    );
}