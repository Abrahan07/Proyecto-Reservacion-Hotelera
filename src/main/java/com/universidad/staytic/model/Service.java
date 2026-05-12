package com.universidad.staytic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "servicios")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serviceId;

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{3,80}$",
            message = "El nombre solo puede contener letras y espacios")
    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Size(max = 250, message = "La descripcion no puede superar 250 caracteres")
    @Column(length = 250)
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private boolean available;

    public Service() {}

    public Service(int serviceId, String name, String description,
                    float price, boolean available) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
    }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "Service{serviceId=" + serviceId + ", name='" + name +
                "', price=" + price + ", available=" + available + "}";
    }
}
