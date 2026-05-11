package com.universidad.staytic.model;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationDetail {

    private int detailId;
    private Room room;
    private Date scheduledCheckIn;
    private Date scheduledCheckOut;
    private int nightCount;
    private float subtotal;
    private List<User> guests;

    public ReservationDetail() {
        this.guests = new ArrayList<>();
    }

    public ReservationDetail(int detailId, Room room, Date scheduledCheckIn,
                          Date scheduledCheckOut, int nightCount, float subtotal) {
        this.detailId = detailId;
        this.room = room;
        this.scheduledCheckIn = scheduledCheckIn;
        this.scheduledCheckOut = scheduledCheckOut;
        this.nightCount = nightCount;
        this.subtotal = subtotal;
        this.guests = new ArrayList<>();
    }

    // detailId: solo get
    public int getDetailId() { return detailId; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public Date getScheduledCheckIn() { return scheduledCheckIn; }
    public void setScheduledCheckIn(Date scheduledCheckIn) { this.scheduledCheckIn = scheduledCheckIn; }

    public Date getScheduledCheckOut() { return scheduledCheckOut; }
    public void setScheduledCheckOut(Date scheduledCheckOut) { this.scheduledCheckOut = scheduledCheckOut; }

    // nightCount: solo get, se calcula a partir de las fechas
    public int getNightCount() { return nightCount; }

    // subtotal: solo get, se calcula con calculateSubtotal()
    public float getSubtotal() { return subtotal; }

    public List<User> getGuests() { return guests; }
    public void setGuests(List<User> guests) { this.guests = guests; }

    public void addGuest(User guest) {
        if (!this.guests.contains(guest)) {
            this.guests.add(guest);
        }
    }

    public void removeGuest(User guest) {
        this.guests.remove(guest);
    }

    @Override
    public String toString() {
        return "DetalleReserva{detailId=" + detailId + ", room=" + room +
                ", scheduledCheckIn=" + scheduledCheckIn +
                ", scheduledCheckOut=" + scheduledCheckOut +
                ", nightCount=" + nightCount + ", subtotal=" + subtotal +
                ", guests=" + guests.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationDetail)) return false;
        ReservationDetail d = (ReservationDetail) o;
        return detailId == d.detailId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(detailId);
    }
}
