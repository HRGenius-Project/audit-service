package com.hrgenius.audit_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceEvent implements Serializable {
    private String eventType;
    private String employeeCode;
    private LocalDate attendanceDate;
    private LocalDateTime timestamp;
    private Double totalHours;
    private String status;
}