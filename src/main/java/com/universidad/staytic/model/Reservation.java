package com.universidad.staytic.model;

import java.util.Date;

public class Reservation {

    private int reservationId;
    private User user;
    private ReservationStatus status;
    private Date createdAt;
    private int totalGuests;
    private String notes;
    private Promotion promotion;

    // Absorbidos de CheckIn
    private Date checkInDateTime;
    private User employeeCheckIn;

    // Absorbidos de CheckOut
    private Date checkOutDateTime;
    private User employeeCheckOut;
    private float additionalCharges;
    private float penalty;
    private Invoice invoice;

    public Reservation() {
    }

    public Reservation(Invoice invoice, float penalty, float additionalCharges, User employeeCheckOut, Date checkOutDateTime, User employeeCheckIn, Date checkInDateTime, Promotion promotion, String notes, int totalGuests, Date createdAt, ReservationStatus status, User user, int reservationId) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
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

    public Date getCheckInDateTime() {
        return checkInDateTime;
    }

    public void setCheckInDateTime(Date checkInDateTime) {
        this.checkInDateTime = checkInDateTime;
    }

    public User getEmployeeCheckIn() {
        return employeeCheckIn;
    }

    public void setEmployeeCheckIn(User employeeCheckIn) {
        this.employeeCheckIn = employeeCheckIn;
    }

    public Date getCheckOutDateTime() {
        return checkOutDateTime;
    }

    public void setCheckOutDateTime(Date checkOutDateTime) {
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
