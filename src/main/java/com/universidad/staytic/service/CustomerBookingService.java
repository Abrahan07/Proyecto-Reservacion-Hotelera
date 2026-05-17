package com.universidad.staytic.service;

import com.universidad.staytic.dto.CustomerReservationForm;
import com.universidad.staytic.model.*;
import com.universidad.staytic.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerBookingService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final PromotionRepository promotionRepository;
    private final PaymentRepository paymentRepository;

    public CustomerBookingService(RoomRepository roomRepository,
                                  ReservationRepository reservationRepository,
                                  UserRepository userRepository,
                                  ServiceRepository serviceRepository,
                                  PromotionRepository promotionRepository,
                                  PaymentRepository paymentRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.promotionRepository = promotionRepository;
        this.paymentRepository = paymentRepository;
    }

    @PreAuthorize("hasRole('GUEST')")
    public List<Room> availableRooms(LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        return roomRepository.findAll().stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                .filter(room -> reservationRepository.countRoomConflicts(room.getRoomId(), checkIn, checkOut, null) == 0)
                .toList();
    }

    @PreAuthorize("hasRole('GUEST')")
    public Optional<Room> findRoom(Integer roomId) {
        return roomRepository.findById(roomId);
    }

    @PreAuthorize("hasRole('GUEST')")
    public List<com.universidad.staytic.model.Service> availableServices() {
        return serviceRepository.findByAvailableTrueOrderByNameAsc();
    }

    @PreAuthorize("hasRole('GUEST')")
    public Reservation getReservationForUser(Integer reservationId, String email) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        if (!reservation.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("No puedes acceder a esta reservacion");
        }
        return reservation;
    }

    @PreAuthorize("hasRole('GUEST')")
    @Transactional
    public Reservation createReservation(CustomerReservationForm form, String email) {
        validateDates(form.getCheckIn(), form.getCheckOut());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Room room = roomRepository.findById(form.getRoomId())
                .orElseThrow(() -> new RuntimeException("Habitacion no encontrada"));
        if (room.getStatus() != RoomStatus.AVAILABLE
                || reservationRepository.countRoomConflicts(room.getRoomId(), form.getCheckIn(), form.getCheckOut(), null) > 0) {
            throw new RuntimeException("La habitacion ya no esta disponible para esas fechas");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setTotalGuests(form.getTotalGuests());
        reservation.setNotes(form.getNotes());
        reservation.setPromotion(findPromotion(form.getPromotionCode(), form.getCheckIn()));

        ReservationDetail detail = new ReservationDetail();
        detail.setRoom(room);
        detail.setScheduledCheckIn(form.getCheckIn());
        detail.setScheduledCheckOut(form.getCheckOut());
        detail.addGuest(user);
        detail.calculateSubtotal();
        reservation.addDetail(detail);

        List<com.universidad.staytic.model.Service> services = findSelectedServices(form.getServiceIds());
        float serviceTotal = 0;
        for (com.universidad.staytic.model.Service service : services) {
            reservation.addService(service);
            serviceTotal += service.getPrice();
        }
        reservation.setAdditionalCharges(serviceTotal);

        return reservationRepository.save(reservation);
    }

    @PreAuthorize("hasRole('GUEST')")
    @Transactional
    public Payment payReservation(Integer reservationId, String email, PaymentMethod paymentMethod) {
        Reservation reservation = getReservationForUser(reservationId, email);
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(reservation.calculateTotal());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus("PAGADO");
        payment.setReference("CLI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.confirm();
        return paymentRepository.save(payment);
    }

    private List<com.universidad.staytic.model.Service> findSelectedServices(List<Integer> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return new ArrayList<>();
        }
        return serviceRepository.findAllById(serviceIds).stream()
                .filter(com.universidad.staytic.model.Service::isAvailable)
                .toList();
    }

    private Promotion findPromotion(String code, LocalDate date) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return promotionRepository
                .findByCodeIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(code.trim(), date, date)
                .orElseThrow(() -> new RuntimeException("El codigo de promocion no existe o no esta vigente"));
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new RuntimeException("Las fechas son obligatorias");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new RuntimeException("La fecha de salida debe ser posterior a la fecha de entrada");
        }
    }
}
