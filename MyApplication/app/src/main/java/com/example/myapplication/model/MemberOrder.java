package com.example.myapplication.model;

public class MemberOrder {
    private String orderId;
    private String orderDate;
    private String price;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPriceDisplay() {
        return price;
    }

    public void setPriceDisplay(String priceDisplay) {
        this.price = priceDisplay;
    }
}
