package com.universidad.staytic.model;

import java.util.Date;
public class Payment {

        private int paymentId;
        private Reservation reservation;
        private float amount;
        private PaymentMethod paymentMethod;
        private Date paymentDate;
        private String status;
        private String reference;

        public Payment() {}

        public Payment(int paymentId, Reservation reservation, float amount,
                    PaymentMethod paymentMethod, Date paymentDate,
                    String status, String reference) {
            this.paymentId = paymentId;
            this.reservation = reservation;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
            this.paymentDate = paymentDate;
            this.status = status;
            this.reference = reference;
        }

        // paymentId: solo get, el id no cambia
        public int getPaymentId() { return paymentId; }

        public Reservation getReservation() { return reservation; }
        public void setReservation(Reservation reservation) { this.reservation = reservation; }

        public float getAmount() { return amount; }
        public void setAmount(float amount) { this.amount = amount; }

        public PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

        // paymentDate: solo get, se asigna automáticamente al procesar
        public Date getPaymentDate() { return paymentDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        // reference: solo get, viene del procesador de pago externo
        public String getReference() { return reference; }

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

