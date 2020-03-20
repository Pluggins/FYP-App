package com.example.myapplication.service;

import com.example.myapplication.model.MemberOrder;

import java.util.List;

public class MemberService {
    private static String email;
    private static String dateJoined;
    private static String name;
    private static List<MemberOrder> orders;

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
}
