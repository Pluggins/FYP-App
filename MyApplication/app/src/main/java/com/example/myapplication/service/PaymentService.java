package com.example.myapplication.service;

import android.graphics.drawable.Drawable;

public class PaymentService {
    private static String paymentId;
    private static String paymentLinkQR;
    private static Drawable paymentQRDrawable;
    private static double amount;
    private static boolean fullyPaid = false;
    private static boolean paymentDone = false;

    public static String getPaymentId() {
        return paymentId;
    }

    public static void setPaymentId(String paymentId) {
        PaymentService.paymentId = paymentId;
    }

    public static String getPaymentLinkQR() {
        return paymentLinkQR;
    }

    public static void setPaymentLinkQR(String paymentLinkQR) {
        PaymentService.paymentLinkQR = paymentLinkQR;
    }

    public static double getAmount() {
        return amount;
    }

    public static void setAmount(double amount) {
        PaymentService.amount = amount;
    }

    public static Drawable getPaymentQRDrawable() {
        return paymentQRDrawable;
    }

    public static void setPaymentQRDrawable(Drawable paymentQRDrawable) {
        PaymentService.paymentQRDrawable = paymentQRDrawable;
    }

    public static boolean isFullyPaid() {
        return fullyPaid;
    }

    public static void setFullyPaid(boolean fullyPaid) {
        PaymentService.fullyPaid = fullyPaid;
    }

    public static boolean isPaymentDone() {
        return paymentDone;
    }

    public static void setPaymentDone(boolean paymentDone) {
        PaymentService.paymentDone = paymentDone;
    }
}
