package com.universidad.staytic.model;



public class Room {

    private int roomId;
    private String number;
    private int floor;
    private RoomType type;
    private float pricePerNight;
    private String status;
    private String description;

    public Room() {}

    public Room(int roomId, String number, int floor, RoomType type,
                      float pricePerNight, String status, String description) {
        this.roomId = roomId;
        this.number = number;
        this.floor = floor;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.status = status;
        this.description = description;
    }

    // roomId: solo get, el id no cambia
    public int getRoomId() { return roomId; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public float getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(float pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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