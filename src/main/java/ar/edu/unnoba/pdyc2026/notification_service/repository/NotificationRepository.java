package ar.edu.unnoba.pdyc2026.notification_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.unnoba.pdyc2026.notification_service.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Spring automáticamente arma la query: SELECT n FROM Notification n WHERE n.username = ? ORDER BY n.createdAt DESC
    List<Notification> findByUsernameOrderByCreatedAtDesc(String username);

    // Busca una notificación específica asegurándose de que le pertenezca al usuario
    Optional<Notification> findByIdAndUsername(Long id, String username);
}