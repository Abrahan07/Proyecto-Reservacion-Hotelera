package com.universidad.staytic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservaciones")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservationId;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Min(value = 1, message = "Debe haber al menos un huesped")
    @Column(nullable = false)
    private int totalGuests;

    @Size(max = 500, message = "Las notas no pueden superar 500 caracteres")
    @Column(length = 500)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime checkInDateTime;

    @ManyToOne
    @JoinColumn(name = "employee_check_in_id")
    private User employeeCheckIn;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime checkOutDateTime;

    @ManyToOne
    @JoinColumn(name = "employee_check_out_id")
    private User employeeCheckOut;

    private float additionalCharges;
    private float penalty;

    @Transient
    private Invoice invoice;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationDetail> details = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "reservacion_servicios",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services = new ArrayList<>();

    public Reservation() {
    }

    public Reservation(Invoice invoice, float penalty, float additionalCharges, User employeeCheckOut, LocalDateTime checkOutDateTime, User employeeCheckIn, LocalDateTime checkInDateTime, Promotion promotion, String notes, int totalGuests, LocalDateTime createdAt, ReservationStatus status, User user, int reservationId) {
        this.invoice = invoice;
        this.penalty = penalty;
        this.additionalCharges = additionalCharges;
        this.employeeCheckOut = employeeCheckOut;
        this.checkOutDateTime = checkOutDateTime;
        this.employeeCheckIn = employeeCheckIn;
        this.checkInDateTime = checkInDateTime;
        this.promotion = promotion;
        this.notes = notes;
        this.totalGuests = totalGuests;
        this.createdAt = createdAt;
        this.status = status;
        this.user = user;
        this.reservationId = reservationId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalGuests() {
        return totalGuests;
    }

    public void setTotalGuests(int totalGuests) {
        this.totalGuests = totalGuests;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public LocalDateTime getCheckInDateTime() {
        return checkInDateTime;
    }

    public void setCheckInDateTime(LocalDateTime checkInDateTime) {
        this.checkInDateTime = checkInDateTime;
    }

    public User getEmployeeCheckIn() {
        return employeeCheckIn;
    }

    public void setEmployeeCheckIn(User employeeCheckIn) {
        this.employeeCheckIn = employeeCheckIn;
    }

    public LocalDateTime getCheckOutDateTime() {
        return checkOutDateTime;
    }

    public void setCheckOutDateTime(LocalDateTime checkOutDateTime) {
        this.checkOutDateTime = checkOutDateTime;
    }

    public User getEmployeeCheckOut() {
        return employeeCheckOut;
    }

    public void setEmployeeCheckOut(User employeeCheckOut) {
        this.employeeCheckOut = employeeCheckOut;
    }

    public float getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(float additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public float getPenalty() {
        return penalty;
    }

    public void setPenalty(float penalty) {
        this.penalty = penalty;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public List<ReservationDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ReservationDetail> details) {
        this.details = details;
    }

    public void addDetail(ReservationDetail detail) {
        detail.setReservation(this);
        this.details.add(detail);
    }

    public void clearDetails() {
        this.details.clear();
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void addService(Service service) {
        if (service != null && !this.services.contains(service)) {
            this.services.add(service);
        }
    }

    public void clearServices() {
        this.services.clear();
    }

    public boolean confirm() {
        if (this.status == ReservationStatus.CANCELLED || this.status == ReservationStatus.FINISHED) {
            return false;
        }
        this.status = ReservationStatus.CONFIRMED;
        return true;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        for (ReservationDetail detail : this.details) {
            if (detail != null && detail.getRoom() != null) {
                detail.getRoom().setStatus(RoomStatus.AVAILABLE);
            }
        }
    }

    public float calculateTotal() {
        float subtotal = 0;
        for (ReservationDetail detail : this.details) {
            subtotal += detail.calculateSubtotal();
        }
        return subtotal - applyPromotion() + calculateCharges();
    }

    public float applyPromotion() {
        if (promotion == null) {
            return 0;
        }
        float subtotal = 0;
        for (ReservationDetail detail : this.details) {
            subtotal += detail.getSubtotal();
        }
        return subtotal * (promotion.getDiscount() / 100);
    }

    protected void generateNotification() {
        // Las notificaciones persistentes se generan desde NotificationService.
    }

    public void checkIn(User employee, LocalDateTime realCheckInDateTime) {
        if (employee == null) {
            throw new RuntimeException("Empleado de check-in es obligatorio");
        }
        if (realCheckInDateTime == null) {
            throw new RuntimeException("Fecha/hora real de check-in es obligatoria");
        }

        this.checkInDateTime = realCheckInDateTime;
        this.employeeCheckIn = employee;
        this.status = ReservationStatus.ACTIVE;

        for (ReservationDetail detail : this.details) {
            if (detail != null && detail.getRoom() != null) {
                detail.getRoom().setStatus(RoomStatus.OCCUPIED);
            }
        }
    }

    public void checkIn() {
        this.checkInDateTime = LocalDateTime.now();
        this.status = ReservationStatus.ACTIVE;
        for (ReservationDetail detail : this.details) {
            if (detail != null && detail.getRoom() != null) {
                detail.getRoom().setStatus(RoomStatus.OCCUPIED);
            }
        }
    }

    public void checkOut(User employee, LocalDateTime realCheckOutDateTime, float additionalCharges, float penalty) {
        if (employee == null) {
            throw new RuntimeException("Empleado de check-out es obligatorio");
        }
        if (realCheckOutDateTime == null) {
            throw new RuntimeException("Fecha/hora real de check-out es obligatoria");
        }
        if (this.checkInDateTime == null) {
            throw new RuntimeException("No se puede registrar check-out sin check-in previo");
        }

        this.checkOutDateTime = realCheckOutDateTime;
        this.additionalCharges = additionalCharges;
        this.penalty = penalty;
        this.employeeCheckOut = employee;
        this.status = ReservationStatus.FINISHED;

        for (ReservationDetail detail : this.details) {
            if (detail != null && detail.getRoom() != null) {
                detail.getRoom().setStatus(RoomStatus.AVAILABLE);
            }
        }
    }

    public void checkOut() {
        if (this.checkInDateTime == null) {
            throw new RuntimeException("No se puede registrar check-out sin check-in previo");
        }
        this.checkOutDateTime = LocalDateTime.now();
        this.status = ReservationStatus.FINISHED;
        for (ReservationDetail detail : this.details) {
            if (detail != null && detail.getRoom() != null) {
                detail.getRoom().setStatus(RoomStatus.AVAILABLE);
            }
        }
    }

    public float calculateCharges() {
        float serviceCharges = 0;
        for (Service service : services) {
            serviceCharges += service.getPrice();
        }
        return additionalCharges + penalty + serviceCharges;
    }

    public Invoice generateInvoice() {
        float subtotal = 0;
        for (ReservationDetail detail : this.details) {
            subtotal += detail.calculateSubtotal();
        }
        float total = calculateTotal();
        float taxes = total * 0.19f;
        return new Invoice(0, this, null, subtotal, taxes, total + taxes, LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", user=" + user +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", totalGuests=" + totalGuests +
                ", notes='" + notes + '\'' +
                ", promotion=" + promotion +
                ", checkInDateTime=" + checkInDateTime +
                ", employeeCheckIn=" + employeeCheckIn +
                ", checkOutDateTime=" + checkOutDateTime +
                ", employeeCheckOut=" + employeeCheckOut +
                ", additionalCharges=" + additionalCharges +
                ", penalty=" + penalty +
                ", invoice=" + invoice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation r = (Reservation) o;
        return reservationId == r.reservationId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(reservationId);
    }
}
