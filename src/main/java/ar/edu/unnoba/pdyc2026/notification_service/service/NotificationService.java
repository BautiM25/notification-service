package ar.edu.unnoba.pdyc2026.notification_service.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unnoba.pdyc2026.notification_service.dto.NotificationResponse;
import ar.edu.unnoba.pdyc2026.notification_service.model.Notification;
import ar.edu.unnoba.pdyc2026.notification_service.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    /* * TODO: Este método será refactorizado en el próximo paso de la Práctica 5
     * para que reciba un mensaje asíncrono desde RabbitMQ / Kafka con la lista
     * de usuarios a notificar cada vez que el Catálogo emita un evento.
     */
    // public void createNotificationsFromBrokerMessage(...) { ... }

    @Transactional(readOnly = true)
    public CompletableFuture<List<NotificationResponse>> getMyNotifications(String username) {
        return CompletableFuture.supplyAsync(() -> {
            return notificationRepository.findByUsernameOrderByCreatedAtDesc(username).stream()
                    .map(n -> new NotificationResponse(
                            n.getId(),
                            n.getEventId(),
                            n.getEventName(),
                            n.getPreviousState(),
                            n.getCurrentState(),
                            n.getCreatedAt(),
                            n.isRead()
                    ))
                    .collect(Collectors.toList());
        });
    }

    @Transactional
    public CompletableFuture<Void> markAsRead(Long notificationId, String username) {
        return CompletableFuture.runAsync(() -> {
            Notification notification = notificationRepository.findByIdAndUsername(notificationId, username)
                    .orElseThrow(() -> new RuntimeException("Notification not found or unauthorized"));

            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public CompletableFuture<Void> deleteNotification(Long notificationId, String username) {
        return CompletableFuture.runAsync(() -> {
            Notification notification = notificationRepository.findByIdAndUsername(notificationId, username)
                    .orElseThrow(() -> new RuntimeException("Notification not found or unauthorized"));

            notificationRepository.delete(notification);
        });
    }
}