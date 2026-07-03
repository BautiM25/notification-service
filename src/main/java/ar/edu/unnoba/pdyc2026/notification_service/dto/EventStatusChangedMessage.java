package ar.edu.unnoba.pdyc2026.notification_service.dto;

import java.util.List;

import ar.edu.unnoba.pdyc2026.notification_service.model.EventStatus;

public record EventStatusChangedMessage(
    Long eventId,
    String eventName,
    List<Long> artistIds,
    EventStatus previousState,
    EventStatus currentState
) {}