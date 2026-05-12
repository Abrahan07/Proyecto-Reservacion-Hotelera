package com.universidad.staytic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "promociones")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int promotionId;

    @NotBlank(message = "El codigo es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9-]{3,30}$", message = "El codigo solo puede contener letras, numeros y guiones")
    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Size(max = 200, message = "La descripcion no puede superar 200 caracteres")
    @Column(length = 200)
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "El descuento debe ser mayor que 0")
    @DecimalMax(value = "100.0", message = "El descuento no puede superar 100")
    @Column(nullable = false)
    private float discount;

    @NotNull(message = "La fecha inicial es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "La fecha final es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate endDate;

    public Promotion() {}

    public Promotion(int promotionId, String code, String description,
                     float discount, LocalDate startDate, LocalDate endDate) {
        this.promotionId = promotionId;
        this.code = code;
        this.description = description;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getPromotionId() { return promotionId; }
    public void setPromotionId(int promotionId) { this.promotionId = promotionId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getDiscount() { return discount; }
    public void setDiscount(float discount) { this.discount = discount; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    @Override
    public String toString() {
        return "Promotion{promotionId=" + promotionId + ", code='" + code +
                "', discount=" + discount + ", startDate=" + startDate +
                ", endDate=" + endDate + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Promotion)) return false;
        Promotion p = (Promotion) o;
        return promotionId == p.promotionId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(promotionId);
    }
}
