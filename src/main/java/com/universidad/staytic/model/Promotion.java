package com.universidad.staytic.model;

import java.util.Date;

public class Promotion {

    private int promotionId;
    private String code;
    private String description;
    private float discount;
    private Date startDate;
    private Date endDate;

    public Promotion() {}

    public Promotion(int promotionId, String code, String description,
                     float discount, Date startDate, Date endDate) {
        this.promotionId = promotionId;
        this.code = code;
        this.description = description;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // promotionId: solo get, el id no cambia
    public int getPromotionId() { return promotionId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getDiscount() { return discount; }
    public void setDiscount(float discount) { this.discount = discount; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

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
