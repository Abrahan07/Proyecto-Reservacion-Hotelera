package com.universidad.staytic.model;

public class Hotel {

    private int hotelId;
    private String name;
    private String address;
    private String city;
    private String phone;
    private int stars;

    public Hotel() {}

    public Hotel(int hotelId, String name, String address,
                 String city, String phone, int stars) {
        this.hotelId = hotelId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.stars = stars;
    }

    // hotelId: solo get, el id no cambia
    public int getHotelId() { return hotelId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    @Override
    public String toString() {
        return "Hotel{hotelId=" + hotelId + ", name='" + name + "', city='" + city +
                "', stars=" + stars + "}";
    }
}