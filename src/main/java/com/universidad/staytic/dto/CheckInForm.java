package com.universidad.staytic.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class CheckInForm {

    @NotNull(message = "La reservacion es obligatoria")
    private Integer reservationId;

    @NotNull(message = "La fecha y hora real de entrada es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime checkInDateTime;

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDateTime getCheckInDateTime() {
        return checkInDateTime;
    }

    public void setCheckInDateTime(LocalDateTime checkInDateTime) {
        this.checkInDateTime = checkInDateTime;
    }
}
