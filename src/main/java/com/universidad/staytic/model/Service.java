package com.universidad.staytic.model;

public class Service {

    private int serviceId;
    private String name;
    private String description;
    private float price;
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

    // serviceId: solo get, el id no cambia
    public int getServiceId() { return serviceId; }

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
