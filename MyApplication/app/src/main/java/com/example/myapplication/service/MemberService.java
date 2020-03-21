package com.example.myapplication.service;

import com.example.myapplication.model.MemberOrder;
import com.example.myapplication.model.OrderMenuItem;

import java.util.List;

public class MemberService {
    private static String email;
    private static String dateJoined;
    private static String name;
    // A member consists of filled email, datejoined and name
    private static Boolean isMember = false;
    private static List<MemberOrder> orders;
    private static List<OrderMenuItem> orderItems;
    private static OrderMenuItem selectedItem;

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        MemberService.email = email;
    }

    public static String getDateJoined() {
        return dateJoined;
    }

    public static void setDateJoined(String dateJoined) {
        MemberService.dateJoined = dateJoined;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        MemberService.name = name;
    }

    public static List<MemberOrder> getOrders() {
        return orders;
    }

    public static void setOrders(List<MemberOrder> orders) {
        MemberService.orders = orders;
    }

    public static Boolean isMember() {
        return isMember;
    }

    public static void setIsMember(Boolean isMember) {
        MemberService.isMember = isMember;
    }

    public static List<OrderMenuItem> getOrderItems() {
        return orderItems;
    }

    public static void setOrderItems(List<OrderMenuItem> orderItems) {
        MemberService.orderItems = orderItems;
    }

    public static OrderMenuItem getSelectedItem() {
        return selectedItem;
    }

    public static void setSelectedItem(OrderMenuItem selectedItem) {
        MemberService.selectedItem = selectedItem;
    }
}
