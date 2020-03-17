package com.example.myapplication.service;


import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SessionService {
    private static String sessionId;
    private static String sessionKey;
    private static String qrUrl;
    private static Drawable QRLoginDrawable;
    private static String captureId;
    private static int type = 1;
    private static boolean isMember = false;
    private static boolean newUser = true;

    public static void setTemporarySession(String newSessionId, String newSessionKey) {
        sessionId = newSessionId;
        sessionKey = newSessionKey;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static String getSessionKey() {
        return sessionKey;
    }

    public static String getQrUrl() {
        return qrUrl;
    }

    public static void setQrUrl(String qrUrl) {
        SessionService.qrUrl = qrUrl;
    }

    public static Drawable getQRLoginDrawable() {
        return QRLoginDrawable;
    }

    public static void setQRLoginDrawable(Drawable QRLoginDrawable) {
        SessionService.QRLoginDrawable = QRLoginDrawable;
    }

    public static String getCaptureId() {
        return captureId;
    }

    public static void setCaptureId(String captureId) {
        SessionService.captureId = captureId;
    }

    public static int getType() {
        return type;
    }

    public static void setType(int type) {
        SessionService.type = type;
    }

    public static boolean isMember() {
        return isMember;
    }

    public static void setIsMember(boolean isMember) {
        SessionService.isMember = isMember;
    }

    public static boolean isNewUser() {
        return newUser;
    }

    public static void setNewUser(boolean newUser) {
        SessionService.newUser = newUser;
    }
}
