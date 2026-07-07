package ar.edu.unnoba.pdyc2026.notification_service.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unnoba.pdyc2026.notification_service.dto.NotificationResponse;
import ar.edu.unnoba.pdyc2026.notification_service.dto.UpdateNotificationRequest;
import ar.edu.unnoba.pdyc2026.notification_service.service.NotificationService;

@RestController
@RequestMapping("/me/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private String getUsernameFromToken(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            return (String) jwtToken.getTokenAttributes().get("preferred_username");
        }
        return authentication.getName();
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(Authentication authentication) 
            throws ExecutionException, InterruptedException {
        String username = getUsernameFromToken(authentication);
        List<NotificationResponse> notifications = notificationService.getMyNotifications(username).get();
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateNotification(
            @PathVariable Long id, 
            @RequestBody UpdateNotificationRequest request, 
            Authentication authentication) throws ExecutionException, InterruptedException {
        
        String username = getUsernameFromToken(authentication);
        if (request.isRead()) {
            notificationService.markAsRead(id, username).get();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id, Authentication authentication) 
            throws ExecutionException, InterruptedException {
        String username = getUsernameFromToken(authentication);
        notificationService.deleteNotification(id, username).get();
        return ResponseEntity.noContent().build();
    }
}