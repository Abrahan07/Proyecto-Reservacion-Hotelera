package com.universidad.staytic.service;

import com.universidad.staytic.model.Notification;
import com.universidad.staytic.model.Payment;
import com.universidad.staytic.model.Reservation;
import com.universidad.staytic.model.ReservationDetail;
import com.universidad.staytic.model.User;
import com.universidad.staytic.repository.NotificationRepository;
import com.universidad.staytic.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
        saveNotification(reservation.getUser(), type, "Tu reservacion #" + reservation.getReservationId() + " fue " + action + ".");
    }

    @Transactional
    public void notifyReservationConfirmed(Reservation reservation) {
        if (reservation == null || reservation.getUser() == null) {
            return;
        }
        saveNotification(reservation.getUser(), "RESERVATION_CONFIRMED",
                "Tu reservacion #" + reservation.getReservationId() + " ha sido confirmada. " + reservationSummary(reservation));
    }

    @Transactional
    public void notifyReservationCancelled(Reservation reservation) {
        if (reservation == null || reservation.getUser() == null) {
            return;
        }
        saveNotification(reservation.getUser(), "RESERVATION_CANCELLED",
                "Tu reservacion #" + reservation.getReservationId() + " fue cancelada.");
    }

    @Transactional
    public void notifyReservationUpdated(Reservation reservation) {
        if (reservation == null || reservation.getUser() == null) {
            return;
        }
        saveNotification(reservation.getUser(), "RESERVATION_UPDATED",
                "Tu reservacion #" + reservation.getReservationId() + " fue actualizada. " + reservationSummary(reservation));
    }

    @Transactional
    public void notifyCheckOutReminder(Reservation reservation) {
        if (reservation == null || reservation.getUser() == null) {
            return;
        }
        saveNotification(reservation.getUser(), "CHECKOUT_REMINDER",
                "Recuerda realizar tu check-out. " + reservationSummary(reservation));
    }

    @Transactional
    public void notifyPaymentProcessed(Payment payment) {
        if (payment == null || payment.getReservation() == null || payment.getReservation().getUser() == null) {
            return;
        }
        String amount = NumberFormat.getCurrencyInstance(new Locale("es", "CO")).format(payment.getAmount());
        String reference = payment.getReference() == null || payment.getReference().isBlank()
                ? "Sin referencia"
                : payment.getReference();
        saveNotification(payment.getReservation().getUser(), "PAYMENT_PROCESSED",
                "Pago de " + amount + " confirmado. Referencia: " + reference + ".");
    }

    private void saveNotification(User user, String type, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setMessage(message);
        notification.setSentAt(LocalDateTime.now());
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Notification> findByUserEmail(String email) {
        return notificationRepository.findByUserEmailOrderBySentAtDesc(email);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Notification> findAll() {
        return notificationRepository.findAllByOrderBySentAtDesc();
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void markAsRead(Integer notificationId, String email) {
        markAsRead(notificationId, email, false);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void markAsRead(Integer notificationId, String email, boolean canManageAll) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificacion no encontrada"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!canManageAll && notification.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("No puedes modificar esta notificacion");
        }
        notification.setRead(true);
    }

    private String reservationSummary(Reservation reservation) {
        if (reservation.getDetails() == null || reservation.getDetails().isEmpty()) {
            return "";
        }
        ReservationDetail detail = reservation.getDetails().get(0);
        String checkIn = detail.getScheduledCheckIn() == null
                ? "-"
                : detail.getScheduledCheckIn().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String checkOut = detail.getScheduledCheckOut() == null
                ? "-"
                : detail.getScheduledCheckOut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String room = detail.getRoom() == null ? "-" : detail.getRoom().getNumber();
        return "Check-in: " + checkIn + ". Check-out: " + checkOut + ". Hab. " + room + ".";
    }
}
