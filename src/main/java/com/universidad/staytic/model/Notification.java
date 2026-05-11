package com.universidad.staytic.model;

import java.util.Date;

public class Notification {

    private int notificationId;
    private User user;
    private String type;
    private String message;
    private Date sentAt;
    private boolean read;

    public Notification() {}

    public Notification(int notificationId, User user, String type,
                        String message, Date sentAt, boolean read) {
        this.notificationId = notificationId;
        this.user = user;
        this.type = type;
        this.message = message;
        this.sentAt = sentAt;
        this.read = read;
    }

    // notificationId: solo get, el id no cambia
    public int getNotificationId() { return notificationId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // sentAt: solo get, se asigna al momento de enviar
    public Date getSentAt() { return sentAt; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    @Override
    public String toString() {
        return "Notification{notificationId=" + notificationId + ", user=" + user +
                ", type='" + type + "', sentAt=" + sentAt + ", read=" + read + "}";
    }
}
