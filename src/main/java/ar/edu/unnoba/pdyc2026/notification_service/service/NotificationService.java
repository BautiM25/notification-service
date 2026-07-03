package ar.edu.unnoba.pdyc2026.notification_service.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unnoba.pdyc2026.notification_service.client.UserClient;
import ar.edu.unnoba.pdyc2026.notification_service.dto.EventStatusChangedMessage;
import ar.edu.unnoba.pdyc2026.notification_service.dto.NotificationResponse;
import ar.edu.unnoba.pdyc2026.notification_service.model.Notification;
import ar.edu.unnoba.pdyc2026.notification_service.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserClient userClient;

    // Este listener crea automáticamente la cola y se suscribe al exchange del catálogo
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "notification.queue", durable = "true"),
            exchange = @Exchange(value = "event.exchange", type = "topic"),
            key = "event.status.changed"
    ))
    @Transactional
    public void createNotificationsFromBrokerMessage(EventStatusChangedMessage message) {
        
        // 1. Le pedimos al user-service la lista de interesados
        List<String> usernamesToNotify = userClient.getInterestedUsers(message.eventId(), message.artistIds());

        // 2. Creamos las notificaciones
        List<Notification> notifications = usernamesToNotify.stream().map(username -> {
            Notification n = new Notification();
            n.setEventId(message.eventId());
            n.setEventName(message.eventName());
            n.setUsername(username);
            n.setPreviousState(message.previousState());
            n.setCurrentState(message.currentState());
            return n;
        }).collect(Collectors.toList());

        // 3. Guardamos en la BD de notificaciones
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
    }

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