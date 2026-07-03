package ar.edu.unnoba.pdyc2026.notification_service.dto;

import java.time.LocalDateTime;

import ar.edu.unnoba.pdyc2026.notification_service.model.EventStatus;

public record NotificationResponse(
    Long id,
    Long eventId,
    String eventName,
    EventStatus previousState,
    EventStatus currentState,
    LocalDateTime createdAt,
    boolean isRead
) {}
