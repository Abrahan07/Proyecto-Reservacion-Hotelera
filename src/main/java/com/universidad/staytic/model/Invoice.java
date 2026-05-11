package com.universidad.staytic.model;

import java.util.Date;

public class Invoice {
    private int invoiceId;
    private Reservation reservation;
    private Payment payment;
    private float subtotal;
    private float taxes;
    private float total;
    private Date issuedAt;

    public Invoice() {}

    public Invoice(int invoiceId, Reservation reservation, Payment payment,
                   float subtotal, float taxes, float total, Date issuedAt) {
        this.invoiceId = invoiceId;
        this.reservation = reservation;
        this.payment = payment;
        this.subtotal = subtotal;
        this.taxes = taxes;
        this.total = total;
        this.issuedAt = issuedAt;
    }

    // invoiceId: solo get
    public int getInvoiceId() { return invoiceId; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    // subtotal, taxes, total: solo get, calculados con calculateTotal()
    public float getSubtotal() { return subtotal; }
    public float getTaxes() { return taxes; }
    public float getTotal() { return total; }

    // issuedAt: solo get, se asigna al emitir
    public Date getIssuedAt() { return issuedAt; }

    @Override
    public String toString() {
        return "Factura{invoiceId=" + invoiceId + ", reservation=" + reservation +
                ", subtotal=" + subtotal + ", taxes=" + taxes +
                ", total=" + total + ", issuedAt=" + issuedAt + "}";
    }
}