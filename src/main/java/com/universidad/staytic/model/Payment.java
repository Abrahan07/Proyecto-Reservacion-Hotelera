package com.universidad.staytic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "pagos")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    @NotNull(message = "La reservacion es obligatoria")
    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor que 0")
    @Column(nullable = false)
    private float amount;

    @NotNull(message = "El metodo de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @NotNull(message = "La fecha de pago es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate paymentDate;

    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{3,30}$",
            message = "El estado solo puede contener letras y espacios")
    @Column(nullable = false, length = 30)
    private String status = "REGISTRADO";

    @Size(max = 80, message = "La referencia no puede superar 80 caracteres")
    @Column(length = 80)
    private String reference;

    public Payment() {}

    public Payment(int paymentId, Reservation reservation, float amount,
                   PaymentMethod paymentMethod, LocalDate paymentDate,
                   String status, String reference) {
        this.paymentId = paymentId;
        this.reservation = reservation;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.status = status;
        this.reference = reference;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    @Override
    public String toString() {
        return "Payment{paymentId=" + paymentId + ", amount=" + amount +
                ", paymentMethod=" + paymentMethod + ", paymentDate=" + paymentDate +
                ", status='" + status + "', reference='" + reference + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment p = (Payment) o;
        return paymentId == p.paymentId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(paymentId);
    }
}
