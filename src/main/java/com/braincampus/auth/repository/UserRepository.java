package com.braincampus.auth.repository;

import com.braincampus.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndTenant_Id(String email, Long tenantId);

    boolean existsByEmailAndTenant_Id(String email, Long tenantId);
    Optional<User> findByEmailAndTenant_SchoolCode(String email, String schoolCode);
    boolean existsByEmailAndTenant_SchoolCode(String email, String schoolCode);
}
