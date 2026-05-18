package com.universidad.staytic.service;

import com.universidad.staytic.dto.ReservationForm;
import com.universidad.staytic.model.*;
import com.universidad.staytic.repository.PaymentRepository;
import com.universidad.staytic.repository.PromotionRepository;
import com.universidad.staytic.repository.ReservationRepository;
import com.universidad.staytic.repository.RoomRepository;
import com.universidad.staytic.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PromotionRepository promotionRepository;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              RoomRepository roomRepository,
                              PromotionRepository promotionRepository,
                              NotificationService notificationService,
                              PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.promotionRepository = promotionRepository;
        this.notificationService = notificationService;
        this.paymentRepository = paymentRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Reservation> search(String userText, String roomText, LocalDate checkIn,
                                    LocalDate checkOut, ReservationStatus status) {
        return reservationRepository.search(blankToNull(userText), blankToNull(roomText), checkIn, checkOut, status);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public Optional<Reservation> findById(Integer id) {
        return reservationRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Reservation> findReservableForCheckIn() {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.CONFIRMED
                        || reservation.getStatus() == ReservationStatus.PENDING
                        || reservation.getStatus() == ReservationStatus.ACTIVE)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Reservation> findReservableForCheckOut() {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getCheckInDateTime() != null)
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.CONFIRMED)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<User> findUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Room> findRooms() {
        return roomRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Reservation> searchCheckIns(String roomText, LocalDate fromDate, LocalDate toDate, String employeeText) {
        return reservationRepository.searchCheckIns(
                blankToNull(roomText),
                startOfDay(fromDate),
                endOfDay(toDate),
                blankToNull(employeeText));
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Reservation> searchCheckOuts(LocalDate fromDate, LocalDate toDate,
                                             Float additionalCharges, Float penalty, String roomText) {
        return reservationRepository.searchCheckOuts(
                startOfDay(fromDate),
                endOfDay(toDate),
                additionalCharges,
                penalty,
                blankToNull(roomText));
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void create(ReservationForm form) {
        create(form, null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void create(ReservationForm form, String employeeEmail) {
        validateDates(form.getCheckIn(), form.getCheckOut());
        Reservation reservation = new Reservation();
        reservation.setCreatedAt(LocalDateTime.now());
        applyForm(reservation, form, null);
        reservationRepository.save(reservation);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void update(Integer id, ReservationForm form) {
        update(id, form, null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void update(Integer id, ReservationForm form, String employeeEmail) {
        validateDates(form.getCheckIn(), form.getCheckOut());
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        applyForm(reservation, form, id);
        notificationService.notifyReservationUpdated(reservation);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void delete(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        paymentRepository.deleteByReservationReservationId(id);
        reservationRepository.delete(reservation);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void changeStatus(Integer id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        if (status == ReservationStatus.CONFIRMED) {
            reservation.confirm();
            notificationService.notifyReservationConfirmed(reservation);
        } else if (status == ReservationStatus.CANCELLED) {
            reservation.cancel();
            notificationService.notifyReservationCancelled(reservation);
        } else {
            reservation.setStatus(status);
            notificationService.notifyReservationUpdated(reservation);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void checkIn(Integer reservationId, String employeeEmail, LocalDateTime realCheckInDateTime) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("No se puede registrar check-in en una reservacion cancelada");
        }
        if (reservation.getStatus() == ReservationStatus.FINISHED) {
            throw new RuntimeException("No se puede registrar check-in en una reservacion finalizada");
        }
        if (reservation.getCheckInDateTime() != null) {
            throw new RuntimeException("Esta reservacion ya tiene check-in registrado");
        }
        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        reservation.checkIn(employee, realCheckInDateTime);
        notificationService.notifyCheckOutReminder(reservation);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void checkOut(Integer reservationId,
                           String employeeEmail,
                           LocalDateTime realCheckOutDateTime,
                           float additionalCharges,
                           float penalty) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        if (reservation.getCheckInDateTime() == null) {
            throw new RuntimeException("No se puede registrar check-out sin check-in previo");
        }
        if (reservation.getCheckOutDateTime() != null) {
            throw new RuntimeException("Esta reservacion ya tiene check-out registrado");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("No se puede registrar check-out en una reservacion cancelada");
        }
        if (additionalCharges < 0 || penalty < 0) {
            throw new RuntimeException("Los cargos adicionales y la penalizacion no pueden ser negativos");
        }

        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        reservation.checkOut(employee, realCheckOutDateTime, additionalCharges, penalty);
        notificationService.notifyReservation(reservation, "CHECKOUT_COMPLETED", "finalizada");
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Room> availableRooms(LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        return roomRepository.findAll().stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                .filter(room -> reservationRepository.countRoomConflicts(
                        room.getRoomId(), checkIn, checkOut, null) == 0)
                .toList();
    }

    public List<Room> dashboardAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        LocalDate effectiveCheckIn = checkIn == null && checkOut == null ? LocalDate.now() : checkIn;
        LocalDate effectiveCheckOut = checkIn == null && checkOut == null ? LocalDate.now().plusDays(1) : checkOut;
        validateDates(effectiveCheckIn, effectiveCheckOut);
        return roomRepository.findAll().stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                .filter(room -> reservationRepository.countRoomConflicts(
                        room.getRoomId(), effectiveCheckIn, effectiveCheckOut, null) == 0)
                .toList();
    }

    public ReservationForm toForm(Reservation reservation) {
        ReservationForm form = new ReservationForm();
        form.setReservationId(reservation.getReservationId());
        form.setUserId(reservation.getUser().getUserId());
        form.setStatus(reservation.getStatus());
        form.setTotalGuests(reservation.getTotalGuests());
        form.setNotes(reservation.getNotes());
        form.setPromotionCode(reservation.getPromotion() == null ? "" : reservation.getPromotion().getCode());
        form.setCheckInDateTime(reservation.getCheckInDateTime());
        form.setCheckOutDateTime(reservation.getCheckOutDateTime());
        form.setAdditionalCharges(reservation.getAdditionalCharges());
        form.setPenalty(reservation.getPenalty());
        form.setRoomIds(reservation.getDetails().stream()
                .map(detail -> detail.getRoom().getRoomId())
                .toList());
        reservation.getDetails().stream().findFirst().ifPresent(detail -> {
            form.setCheckIn(detail.getScheduledCheckIn());
            form.setCheckOut(detail.getScheduledCheckOut());
        });
        return form;
    }

    private void applyForm(Reservation reservation, ReservationForm form, Integer currentReservationId) {
        User user = userRepository.findById(form.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        reservation.setUser(user);
        reservation.setStatus(form.getStatus() == null ? ReservationStatus.PENDING : form.getStatus());
        reservation.setTotalGuests(form.getTotalGuests());
        reservation.setNotes(form.getNotes());
        reservation.setPromotion(findPromotion(form.getPromotionCode(), form.getCheckIn()));

        reservation.clearDetails();
        for (Integer roomId : form.getRoomIds()) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Habitacion no encontrada"));
            if (reservationRepository.countRoomConflicts(roomId, form.getCheckIn(), form.getCheckOut(), currentReservationId) > 0) {
                throw new RuntimeException("La habitacion " + room.getNumber() + " no esta disponible en ese rango de fechas");
            }
            ReservationDetail detail = new ReservationDetail();
            detail.setRoom(room);
            detail.setScheduledCheckIn(form.getCheckIn());
            detail.setScheduledCheckOut(form.getCheckOut());
            detail.calculateSubtotal();
            reservation.addDetail(detail);
        }
    }

    private void applyOperationalFields(Reservation reservation, ReservationForm form, String employeeEmail) {
        User employee = null;
        if (employeeEmail != null && !employeeEmail.isBlank()) {
            employee = userRepository.findByEmail(employeeEmail)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        }

        if (form.getCheckInDateTime() != null) {
            reservation.checkIn(employee, form.getCheckInDateTime());
            notificationService.notifyCheckOutReminder(reservation);
        }

        reservation.setAdditionalCharges(form.getAdditionalCharges());
        reservation.setPenalty(form.getPenalty());

        if (form.getCheckOutDateTime() != null) {
            reservation.checkOut(employee, form.getCheckOutDateTime(),
                    form.getAdditionalCharges(), form.getPenalty());
            notificationService.notifyReservation(reservation, "CHECKOUT_COMPLETED", "finalizada");
        }
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

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private LocalDateTime startOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDateTime endOfDay(LocalDate date) {
        return date == null ? null : date.atTime(23, 59, 59);
    }

    public List<Reservation> findReservationsByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email del usuario es requerido");
        }
        return reservationRepository.findByUserEmail(email.trim());
    }
}
