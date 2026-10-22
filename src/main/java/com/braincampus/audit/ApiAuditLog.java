package com.braincampus.audit;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_audit_logs", indexes = {
    @Index(name = "idx_audit_tenant_created", columnList = "tenant_id, created_at"),
    @Index(name = "idx_audit_endpoint", columnList = "endpoint"),
    @Index(name = "idx_audit_status", columnList = "status_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id")
    private Long tenantId;

    private String username;

    @Column(nullable = false, length = 10)
    private String httpMethod; // GET, POST, PUT, DELETE

    @Column(nullable = false, length = 500)
    private String endpoint; // e.g. /students

    private String queryString;

    private int statusCode; // 200, 201, 400, 500, etc.

    private boolean isSuccess; // status < 400

    private long executionTimeMs; // Latency in milliseconds

    private String clientIp;

    @Column(length = 1000)
    private String errorMessage; // Exception message if failed

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}