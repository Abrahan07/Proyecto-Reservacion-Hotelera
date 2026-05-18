package com.universidad.staytic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "mantenimientos")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maintenanceId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 60, message = "El tipo no puede superar 60 caracteres")
    @Column(nullable = false, length = 60)
    private String type;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
    @Column(nullable = false, length = 500)
    private String description;

    @NotNull(message = "La fecha programada es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime completedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MaintenanceStatus status = MaintenanceStatus.SCHEDULED;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    public Maintenance() {}

    public Maintenance(int maintenanceId, Room room, String type,
                       String description, LocalDateTime scheduledDate,
                       LocalDateTime completedDate, MaintenanceStatus status,
                       User employee) {
        this.maintenanceId = maintenanceId;
        this.room = room;
        this.type = type;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.completedDate = completedDate;
        this.status = status;
        this.employee = employee;
    }

    public int getMaintenanceId() { return maintenanceId; }
    public void setMaintenanceId(int maintenanceId) { this.maintenanceId = maintenanceId; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    public MaintenanceStatus getStatus() { return status; }
    public void setStatus(MaintenanceStatus status) { this.status = status; }

    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }

    @Override
    public String toString() {
        return "Maintenance{maintenanceId=" + maintenanceId + ", room=" + room +
                ", type='" + type + "', status='" + status + "', scheduledDate=" +
                scheduledDate + "}";
    }
}
