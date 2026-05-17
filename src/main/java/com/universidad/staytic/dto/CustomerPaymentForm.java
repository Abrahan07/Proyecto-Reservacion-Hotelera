package com.universidad.staytic.dto;

import com.universidad.staytic.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class CustomerPaymentForm {

    @NotNull(message = "El metodo de pago es obligatorio")
    private PaymentMethod paymentMethod;

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
