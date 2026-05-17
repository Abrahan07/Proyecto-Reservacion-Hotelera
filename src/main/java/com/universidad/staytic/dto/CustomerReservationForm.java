package com.universidad.staytic.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerReservationForm {

    @NotNull(message = "La habitacion es obligatoria")
    private Integer roomId;

    @NotNull(message = "La fecha de entrada es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;

    @NotNull(message = "La fecha de salida es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;

    @Min(value = 1, message = "Debe haber al menos un huesped")
    private int totalGuests = 1;

    private List<Integer> serviceIds = new ArrayList<>();

    @Size(max = 30, message = "El codigo no puede superar 30 caracteres")
    private String promotionCode;

    @Size(max = 500, message = "Las notas no pueden superar 500 caracteres")
    private String notes;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
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

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
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
