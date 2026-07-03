package ar.edu.unnoba.pdyc2026.notification_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateNotificationRequest(
    @JsonProperty("is_read") boolean isRead
) {}