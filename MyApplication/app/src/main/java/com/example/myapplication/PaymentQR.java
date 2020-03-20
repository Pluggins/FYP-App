package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.service.PaymentService;
import com.example.myapplication.service.SessionService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class PaymentQR extends AppCompatActivity {
    private ImageView qrImg;
    private Button cancelBtn;
    private TextView txtAmount;
    private Timer timer;
    private TextView paymentInst;
    private TextView paymentInst2;
    private Button captureDetailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_q_r);
        qrImg = (ImageView) findViewById(R.id.paymentQRImg);
        qrImg.setImageDrawable(PaymentService.getPaymentQRDrawable());
        cancelBtn = (Button) findViewById(R.id.qrPaymentCancelBtn);
        txtAmount = (TextView) findViewById(R.id.qrPayAmount);
        txtAmount.setText(String.valueOf("RM"+String.format("%.2f", PaymentService.getAmount())));
        paymentInst = (TextView) findViewById(R.id.paymentInst);
        paymentInst2 = (TextView) findViewById(R.id.paymentInst2);
        captureDetailBtn = (Button) findViewById(R.id.membershipBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentService.setPaymentDone(true);
                timer.cancel();
                if (PaymentService.isFullyPaid()) {
                    Intent intent = new Intent(PaymentQR.this, MainInit.class);
                    startActivity(intent);
                    Runtime.getRuntime().exit(0);
                } else {
                    onBackPressed();
                }
            }
        });
        captureDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentService.setPaymentDone(true);
                timer.cancel();
                GenerateMemberQR generateQR = new GenerateMemberQR();
                generateQR.execute();
            }
        });
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                PaymentQR.CheckCaptureStatus captureStatus = new PaymentQR.CheckCaptureStatus();
                captureStatus.execute(SessionService.getCaptureId());
            }
        };
        PaymentService.setPaymentDone(false);
        timer.scheduleAtFixedRate(task, 3000,500);
    }

    private class CheckCaptureStatus extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            if (!PaymentService.isPaymentDone()) {
                // HTTPPOST to API
                Log.i("Test", "testing");

                String response = null;
                URL url = null;
                try {
                    url = new URL("https://fyp.amazecraft.net/Api/Payment/CheckPaypalOrder");
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
                    json.put("paymentId", PaymentService.getPaymentId());

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
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String message) {
            if (message != null) {
                try {
                    JSONObject obj = new JSONObject(message);
                    String status = obj.getString("status");
                    if (status.equals("APPROVED")) {
                        Boolean isAll = obj.getBoolean("paidAll");
                        if (isAll) {
                            PaymentService.setFullyPaid(true);
                            paymentInst.setText("Payment Received!");
                            paymentInst2.setText("Your order is now closed.");
                            qrImg.setVisibility(View.INVISIBLE);
                            if (!SessionService.isMember()) {
                                txtAmount.setText("Would you like to save your order details to your phone for future reference?");
                                captureDetailBtn.setVisibility(View.VISIBLE);
                            } else {
                                txtAmount.setVisibility(View.INVISIBLE);
                            }
                            cancelBtn.setText("Done");
                        } else {
                            paymentInst.setText("Payment Received!");
                            paymentInst2.setVisibility(View.INVISIBLE);
                            qrImg.setVisibility(View.INVISIBLE);
                            cancelBtn.setText("Done");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GenerateMemberQR extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // HTTPPOST to API
            String response = null;
            URL url = null;
            try {
                url = new URL("https://fyp.amazecraft.net/Api/Capture/Generate");
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
                json.put("type", "2");
                json.put("sessionId", SessionService.getSessionId());
                json.put("sessionKey", SessionService.getSessionKey());

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
                    String qrUrl = obj.getString("captureQR");
                    InputStream is = null;
                    is = (InputStream) new URL(qrUrl).getContent();
                    PaymentService.setPaymentQRDrawable(Drawable.createFromStream(is, "src name"));
                    qrImg.setImageDrawable(PaymentService.getPaymentQRDrawable());
                    paymentInst.setText("Capture QR");
                    paymentInst2.setText("To save your order detail.");
                    txtAmount.setVisibility(View.INVISIBLE);
                    qrImg.setVisibility(View.VISIBLE);
                    captureDetailBtn.setVisibility(View.GONE);
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
