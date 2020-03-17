package com.example.myapplication.service;

public class VendorService {
    private static String vendorId = "e916642b-d464-476f-920d-43462d0110b3";

    public static String getVendorId() {
        return vendorId;
    }

    public static void setVendorId(String vendorId) {
        VendorService.vendorId = vendorId;
    }
}
