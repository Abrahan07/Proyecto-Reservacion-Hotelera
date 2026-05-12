package com.universidad.staytic.dto;

import com.universidad.staytic.model.ReservationStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationForm {

    private Integer reservationId;

    @NotNull(message = "El usuario es obligatorio")
    private Integer userId;

    @NotEmpty(message = "Debe seleccionar al menos una habitacion")
    private List<Integer> roomIds = new ArrayList<>();

    @NotNull(message = "La fecha de entrada es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;

    @NotNull(message = "La fecha de salida es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;

    @Min(value = 1, message = "Debe haber al menos un huesped")
    private int totalGuests = 1;

    private ReservationStatus status = ReservationStatus.PENDING;

    @Size(max = 30, message = "El codigo no puede superar 30 caracteres")
    private String promotionCode;

    @Size(max = 500, message = "Las notas no pueden superar 500 caracteres")
    private String notes;

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Integer> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(List<Integer> roomIds) {
        this.roomIds = roomIds;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getTotalGuests() {
        return totalGuests;
    }

    public void setTotalGuests(int totalGuests) {
        this.totalGuests = totalGuests;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
