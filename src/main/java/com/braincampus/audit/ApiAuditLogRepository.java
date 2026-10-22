package com.braincampus.audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ApiAuditLogRepository extends JpaRepository<ApiAuditLog, Long> {
    Page<ApiAuditLog> findAllByTenantIdOrderByCreatedAtDesc(Long tenantId, Pageable pageable);
    @Query("""
        SELECT a.statusCode AS status, COUNT(a.id) AS count
        FROM ApiAuditLog a
        WHERE a.tenantId = :tenantId
          AND a.createdAt >= :startDate
        GROUP BY a.statusCode
    """)
    List<Map<String, Object>> getStatusCodeDistribution(
            @Param("tenantId") Long tenantId,
            @Param("startDate") LocalDateTime startDate
    );

    @Query("""
        SELECT a.endpoint AS endpoint, AVG(a.executionTimeMs) AS avgLatency, COUNT(a.id) AS totalCalls
        FROM ApiAuditLog a
        WHERE a.tenantId = :tenantId
          AND a.createdAt >= :startDate
        GROUP BY a.endpoint
        ORDER BY avgLatency DESC
    """)
    List<Map<String, Object>> getEndpointLatencyStats(
            @Param("tenantId") Long tenantId,
            @Param("startDate") LocalDateTime startDate
    );

    @Query(
            value = """
            SELECT a FROM ApiAuditLog a
            WHERE a.tenantId = :tenantId
              AND (:success IS NULL OR a.isSuccess = :success)
              AND (
                  CAST(:search AS string) IS NULL 
                  OR LOWER(a.endpoint) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                  OR LOWER(a.username) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
              )
            """,
            countQuery = """
            SELECT COUNT(a) FROM ApiAuditLog a
            WHERE a.tenantId = :tenantId
              AND (:success IS NULL OR a.isSuccess = :success)
              AND (
                  CAST(:search AS string) IS NULL 
                  OR LOWER(a.endpoint) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                  OR LOWER(a.username) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
              )
            """
    )
    Page<ApiAuditLog> findLogsWithFilters(
            @Param("tenantId") Long tenantId,
            @Param("success") Boolean success,
            @Param("search") String search,
            Pageable pageable
    );
}