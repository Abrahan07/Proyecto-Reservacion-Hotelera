package com.universidad.staytic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "habitaciones")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomId;

    @NotBlank(message = "El numero es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9-]{1,10}$",
            message = "El numero solo puede contener letras, numeros y guiones")
    @Column(nullable = false, unique = true, length = 10)
    private String number;

    @Min(value = 1, message = "El piso debe ser mayor o igual a 1")
    @Max(value = 99, message = "El piso no puede superar 99")
    @Column(nullable = false)
    private int floor;

    @NotNull(message = "El tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomType type;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    @Column(nullable = false)
    private float pricePerNight;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Size(max = 300, message = "La descripcion no puede superar 300 caracteres")
    @Column(length = 300)
    private String description;

    public Room() {}

    public Room(int roomId, String number, int floor, RoomType type,
                      float pricePerNight, RoomStatus status, String description) {
        this.roomId = roomId;
        this.number = number;
        this.floor = floor;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.status = status;
        this.description = description;
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public float getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(float pricePerNight) { this.pricePerNight = pricePerNight; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Habitacion{roomId=" + roomId + ", number='" + number + "', floor=" + floor +
                ", type=" + type + ", status='" + status + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room h = (Room) o;
        return roomId == h.roomId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(roomId);
    }
}
