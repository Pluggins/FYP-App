package com.example.myapplication.service;

import com.example.myapplication.model.MenuItem;
import com.example.myapplication.model.OrderMenuItem;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private static String orderId = null;
    private static boolean initiatedOrder = false;
    private static List<OrderMenuItem> items = new ArrayList<OrderMenuItem>();

    public static boolean isInitiatedOrder() {
        return initiatedOrder;
    }

    public static void setInitiatedOrder(boolean initiatedOrder) {
        OrderService.initiatedOrder = initiatedOrder;
    }

    public static String getOrderId() {
        return orderId;
    }

    public static void setOrderId(String orderId) {
        OrderService.orderId = orderId;
    }

    public static List<OrderMenuItem> getItems() {
        return items;
    }

    public static void setItems(List<OrderMenuItem> items) {
        OrderService.items = items;
    }

    public static void addItem(OrderMenuItem item) {
        items.add(item);
    }

    public static void clearOrder() {
        items = new ArrayList<OrderMenuItem>();
    }
}
