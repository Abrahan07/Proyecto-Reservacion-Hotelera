package com.universidad.staytic.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class CheckOutForm {

    @NotNull(message = "La reservacion es obligatoria")
    private Integer reservationId;

    @NotNull(message = "La fecha y hora real de salida es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime checkOutDateTime;

    @DecimalMin(value = "0.0", message = "Los consumos adicionales no pueden ser negativos")
    private float additionalCharges;

    @DecimalMin(value = "0.0", message = "La penalizacion no puede ser negativa")
    private float penalty;

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDateTime getCheckOutDateTime() {
        return checkOutDateTime;
    }

    public void setCheckOutDateTime(LocalDateTime checkOutDateTime) {
        this.checkOutDateTime = checkOutDateTime;
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
}
