package com.braincampus.audit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiAuditService {

    private final ApiAuditLogRepository auditLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLogAsync(ApiAuditLog auditLog) {
        try {
            auditLogRepository.save(auditLog);
        } catch (Exception ex) {
            log.error("Failed to persist API audit log: {}", ex.getMessage());
        }
    }
}