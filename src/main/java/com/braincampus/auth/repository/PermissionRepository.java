package com.braincampus.auth.repository;
import com.braincampus.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);

    // Used when updating role permissions: fetches all permissions that match the provided list of names
    List<Permission> findAllByNameIn(Set<String> names);

    // Used when seeding defaults, or checking if a permission already exists before creating a new one
    boolean existsByName(String name);
    
}