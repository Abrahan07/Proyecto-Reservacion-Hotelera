package com.universidad.staytic.service;

import com.universidad.staytic.model.Payment;
import com.universidad.staytic.model.PaymentMethod;
import com.universidad.staytic.model.Reservation;
import com.universidad.staytic.repository.PaymentRepository;
import com.universidad.staytic.repository.ReservationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;

    public PaymentService(PaymentRepository paymentRepository,
                          ReservationRepository reservationRepository,
                          NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Payment> search(String reservationText, Float amount, PaymentMethod method, LocalDate paymentDate) {
        return paymentRepository.search(blankToNull(reservationText), amount, method, paymentDate);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public Optional<Payment> findById(Integer id) {
        return paymentRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Reservation> findReservations() {
        return reservationRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void save(Payment payment, Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        payment.setReservation(reservation);
        Payment savedPayment = paymentRepository.save(payment);
        notificationService.notifyPaymentProcessed(savedPayment);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void delete(Integer id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Pago no encontrado");
        }
        paymentRepository.deleteById(id);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
