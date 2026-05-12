package com.universidad.staytic.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "detalle_reservaciones")
public class ReservationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int detailId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @NotNull(message = "La habitacion es obligatoria")
    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    @NotNull(message = "La fecha de entrada es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate scheduledCheckIn;

    @NotNull(message = "La fecha de salida es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate scheduledCheckOut;

    @Column(nullable = false)
    private int nightCount;

    @Column(nullable = false)
    private float subtotal;

    public ReservationDetail() {
    }

    public ReservationDetail(int detailId, Room room, LocalDate scheduledCheckIn,
                          LocalDate scheduledCheckOut, int nightCount, float subtotal) {
        this.detailId = detailId;
        this.room = room;
        this.scheduledCheckIn = scheduledCheckIn;
        this.scheduledCheckOut = scheduledCheckOut;
        this.nightCount = nightCount;
        this.subtotal = subtotal;
    }

    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public LocalDate getScheduledCheckIn() { return scheduledCheckIn; }
    public void setScheduledCheckIn(LocalDate scheduledCheckIn) { this.scheduledCheckIn = scheduledCheckIn; }

    public LocalDate getScheduledCheckOut() { return scheduledCheckOut; }
    public void setScheduledCheckOut(LocalDate scheduledCheckOut) { this.scheduledCheckOut = scheduledCheckOut; }

    public int getNightCount() { return nightCount; }
    public void setNightCount(int nightCount) { this.nightCount = nightCount; }

    public float getSubtotal() { return subtotal; }
    public void setSubtotal(float subtotal) { this.subtotal = subtotal; }

    public void calculateSubtotal() {
        if (scheduledCheckIn == null || scheduledCheckOut == null || room == null) {
            this.nightCount = 0;
            this.subtotal = 0;
            return;
        }
        this.nightCount = (int) ChronoUnit.DAYS.between(scheduledCheckIn, scheduledCheckOut);
        this.subtotal = this.nightCount * room.getPricePerNight();
    }

    @Override
    public String toString() {
        return "DetalleReserva{detailId=" + detailId + ", room=" + room +
                ", scheduledCheckIn=" + scheduledCheckIn +
                ", scheduledCheckOut=" + scheduledCheckOut +
                ", nightCount=" + nightCount + ", subtotal=" + subtotal + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationDetail)) return false;
        ReservationDetail d = (ReservationDetail) o;
        return detailId == d.detailId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(detailId);
    }
}
