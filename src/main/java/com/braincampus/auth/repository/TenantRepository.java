package com.braincampus.auth.repository;
import com.braincampus.auth.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findBySchoolCode(String schoolCode);

    Optional<Tenant> findBySubdomain(String subdomain);
}