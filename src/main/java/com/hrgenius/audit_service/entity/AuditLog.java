package com.hrgenius.audit_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serviceSource; // E.g., ATTENDANCE_SERVICE, EMPLOYEE_SERVICE, PAYROLL_SERVICE

    @Column(nullable = false)
    private String action; // E.g., PUNCH_IN, PUNCH_OUT, PAYROLL_PROCESSED

    @Column(nullable = false)
    private String performedBy; // employeeCode or admin ID or SYSTEM

    @Lob
    @Column(columnDefinition = "TEXT")
    private String eventData; // Complete JSON payload for audit trail

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime loggedAt;
}