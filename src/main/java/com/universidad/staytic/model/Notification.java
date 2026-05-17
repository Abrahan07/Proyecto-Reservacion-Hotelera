package com.universidad.staytic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationId;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "El tipo es obligatorio")
    @Column(nullable = false, length = 40)
    private String type;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500, message = "El mensaje no puede superar 500 caracteres")
    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "read_status", nullable = false)
    private boolean read;

    public Notification() {}

    public Notification(int notificationId, User user, String type,
                        String message, LocalDateTime sentAt, boolean read) {
        this.notificationId = notificationId;
        this.user = user;
        this.type = type;
        this.message = message;
        this.sentAt = sentAt;
        this.read = read;
    }

    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    @Override
    public String toString() {
        return "Notification{notificationId=" + notificationId + ", user=" + user +
                ", type='" + type + "', sentAt=" + sentAt + ", read=" + read + "}";
    }
}
