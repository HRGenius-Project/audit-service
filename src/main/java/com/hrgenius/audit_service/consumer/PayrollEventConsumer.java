package com.hrgenius.audit_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrgenius.audit_service.dto.PayrollProcessedEvent;
import com.hrgenius.audit_service.entity.AuditLog;
import com.hrgenius.audit_service.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayrollEventConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "payroll.processed.queue")
    public void consumePayrollProcessedEvent(PayrollProcessedEvent event) {
        log.info("Received payroll.processed event for employee: {}, payrollId: {}",
                event.getEmployeeCode(), event.getPayrollId());

        String eventDataJson;
        try {
            eventDataJson = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payroll event data to JSON", e);
            eventDataJson = event.toString();
        }

        AuditLog auditLog = AuditLog.builder()
                .serviceSource("PAYROLL_SERVICE")
                .action("PAYROLL_PROCESSED")
                .performedBy("SYSTEM")
                .eventData(eventDataJson)
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit log saved successfully for payroll ID: {}", event.getPayrollId());
    }
}