package com.universidad.staytic.service;

import com.universidad.staytic.model.Notification;
import com.universidad.staytic.model.Reservation;
import com.universidad.staytic.model.User;
import com.universidad.staytic.repository.NotificationRepository;
import com.universidad.staytic.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void notifyReservation(Reservation reservation, String type, String action) {
        if (reservation == null || reservation.getUser() == null) {
            return;
        }
        Notification notification = new Notification();
        notification.setUser(reservation.getUser());
        notification.setType(type);
        notification.setMessage("Tu reservacion #" + reservation.getReservationId() + " fue " + action + ".");
        notification.setSentAt(LocalDateTime.now());
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Notification> findByUserEmail(String email) {
        return notificationRepository.findByUserEmailOrderBySentAtDesc(email);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void markAsRead(Integer notificationId, String email) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificacion no encontrada"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (notification.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("No puedes modificar esta notificacion");
        }
        notification.setRead(true);
    }
}
