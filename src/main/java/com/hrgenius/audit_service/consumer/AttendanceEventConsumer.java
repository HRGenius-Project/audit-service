package com.hrgenius.audit_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrgenius.audit_service.dto.AttendanceEvent;
import com.hrgenius.audit_service.entity.AuditLog;
import com.hrgenius.audit_service.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceEventConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumeAttendanceEvent(AttendanceEvent event) {
        log.info("Received event from RabbitMQ: Type = {}, Employee = {}",
                event.getEventType(), event.getEmployeeCode());

        try {
            String eventJson = objectMapper.writeValueAsString(event);

            AuditLog auditLog = AuditLog.builder()
                    .serviceSource("ATTENDANCE_SERVICE")
                    .action(event.getEventType())
                    .performedBy(event.getEmployeeCode())
                    .eventData(eventJson)
                    .build();

            AuditLog savedLog = auditLogRepository.save(auditLog);
            log.info("Successfully persisted AuditLog record ID: {}", savedLog.getId());

        } catch (Exception e) {
            log.error("Failed to process and persist attendance audit event: {}", e.getMessage(), e);
        }
    }
}