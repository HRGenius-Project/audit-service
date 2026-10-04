package com.hrgenius.audit_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrgenius.audit_service.dto.LeaveApprovedEvent;
import com.hrgenius.audit_service.entity.AuditLog;
import com.hrgenius.audit_service.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveEventConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${app.rabbitmq.leave-queue}")
    public void consumeLeaveApprovedEvent(LeaveApprovedEvent event) {
        log.info("Received leave.approved event: Employee = {}, Days = {}, ApprovedBy = {}",
                event.getEmployeeCode(), event.getNumberOfDays(), event.getApprovedBy());

        try {
            String eventJson = objectMapper.writeValueAsString(event);

            AuditLog auditLog = AuditLog.builder()
                    .serviceSource("LEAVE_SERVICE")
                    .action("LEAVE_APPROVED")
                    .performedBy(event.getApprovedBy())
                    .eventData(eventJson)
                    .build();

            AuditLog savedLog = auditLogRepository.save(auditLog);
            log.info("Successfully persisted Leave AuditLog record ID: {}", savedLog.getId());

        } catch (Exception e) {
            log.error("Failed to process leave audit event: {}", e.getMessage(), e);
        }
    }
}