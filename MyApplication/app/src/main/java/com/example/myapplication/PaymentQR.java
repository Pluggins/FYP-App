package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_q_r);
        qrImg = (ImageView) findViewById(R.id.paymentQRImg);
        qrImg.setImageDrawable(PaymentService.getPaymentQRDrawable());
        cancelBtn = (Button) findViewById(R.id.qrPaymentCancelBtn);
        txtAmount = (TextView) findViewById(R.id.qrPayAmount);
        txtAmount.setText(String.valueOf("RM"+PaymentService.getAmount()));
        paymentInst = (TextView) findViewById(R.id.paymentInst);
        paymentInst2 = (TextView) findViewById(R.id.paymentInst2);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                PaymentQR.CheckCaptureStatus captureStatus = new PaymentQR.CheckCaptureStatus();
                captureStatus.execute(SessionService.getCaptureId());
            }
        };
        timer.scheduleAtFixedRate(task, 2000,500);
    }

    private class CheckCaptureStatus extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
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
}
