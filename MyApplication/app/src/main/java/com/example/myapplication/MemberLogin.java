package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.se.omapi.Session;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.service.MenuItemService;
import com.example.myapplication.service.SessionService;
import com.example.myapplication.service.UtilityService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MemberLogin extends AppCompatActivity {
    ImageView qrImg;
    Button cancelBtn;
    int transactionStatus = 0;
    boolean stopLoading = false;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_login);

        qrImg = (ImageView) findViewById(R.id.loginQRImg);
        cancelBtn = (Button) findViewById(R.id.qrLoginCancelBtn);
        //TextView txtView = (TextView) findViewById(R.id.qrText);
        //txtView.setText(SessionService.getQrUrl());
        qrImg.setImageDrawable(SessionService.getQRLoginDrawable());

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        timer = new Timer();
        TimerTask task = new TimerTask() {
                    public void run() {
                        CheckCaptureStatus captureStatus = new CheckCaptureStatus();
                        captureStatus.execute(SessionService.getCaptureId());
                    }
                };
        timer.scheduleAtFixedRate(task, 2000,500);
    }

    @Override
    public void onBackPressed() {
        if (!stopLoading) {
            stopLoading = true;
            timer.cancel();
            if (transactionStatus == 0) {
                Intent intent = new Intent(getApplicationContext(), MainInit.class);
                startActivity(intent);
                finish();
            } else if (transactionStatus == 1) {
                Intent intent = new Intent(getApplicationContext(), MainInit.class);
                startActivity(intent);
                finish();
                Toast toast = Toast.makeText(getApplicationContext(), "QR has expired, please try again later.", Toast.LENGTH_SHORT);
                toast.show();
            } else if (transactionStatus == 2) {
                SessionService.setType(2);
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivityMember.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast toast = Toast.makeText(getApplicationContext(), "QR has been successfully captured.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class CheckCaptureStatus extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // HTTPPOST to API
            String response = null;
            URL url = null;
            try {
                url = new URL("https://fyp.amazecraft.net/Api/Capture/GetCaptureStatus");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoInput(true);

                JSONObject json = new JSONObject();
                json.put("captureId", params[0]);

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(json.toString());
                wr.flush();

                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        sb.append(responseLine.trim());
                    }
                    response = sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NetworkOnMainThreadException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String message) {
            if (message != null) {
                try {
                    JSONObject obj = new JSONObject(message);
                    String status = obj.getString("status");
                    if (status.equals("SCANNED")) {
                        SessionService.setIsMember(true);
                        SessionService.setTemporarySession(obj.getString("sessionId"),obj.getString("sessionKey"));
                        transactionStatus = 2;
                        onBackPressed();
                    } else if (status.equals("EXPIRED")) {
                        transactionStatus = 1;
                        onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
