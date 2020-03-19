package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.adapter.MenuItemAdapter;
import com.example.myapplication.adapter.OrderAdapter;
import com.example.myapplication.model.MenuItem;
import com.example.myapplication.model.OrderMenuItem;
import com.example.myapplication.service.MenuItemService;
import com.example.myapplication.service.OrderService;
import com.example.myapplication.service.PaymentService;
import com.example.myapplication.service.SessionService;
import com.example.myapplication.ui.cart.CartFragment;
import com.google.android.material.snackbar.Snackbar;

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
import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter aAdapter;
    private DialogInterface.OnClickListener dialogClickListener = null;
    private ArrayList<OrderMenuItem> orderItems = (ArrayList<OrderMenuItem>) OrderService.getItems();
    private boolean isAllItem = true;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        bar = (ProgressBar) findViewById(R.id.order_progressbar);
        aAdapter = new OrderAdapter(orderItems, this);
        listView = (ListView) findViewById(R.id.orderItemListView);
        listView.setAdapter(aAdapter);
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        bar.setVisibility(View.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        SubmitRestToPayment submitPayment = new SubmitRestToPayment();
                        submitPayment.execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.top_order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.payOrderBtn:
                isAllItem = true;
                for (int i = 0; i < aAdapter.getCount(); i++) {
                    OrderMenuItem objItem = (OrderMenuItem) aAdapter.getItem(i);
                    if (objItem.getSelectedQuantity() != 0) {
                        isAllItem = false;
                    }
                }

                if (isAllItem) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Would you like to pay for all the items now?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Would you like to pay for the selected items now?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }

                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SubmitRestToPayment extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String response = null;
            URL url = null;
            try {
                url = new URL("https://fyp.amazecraft.net/Api/Payment/PayRestByOrderId");
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
                json.put("orderId",OrderService.getOrderId());

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

                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("result").equals("OK")) {
                        PaymentService.setPaymentId(obj.getString("paymentId"));
                        PaymentService.setPaymentLinkQR(obj.getString("paymentLinkQR"));
                        PaymentService.setAmount(obj.getDouble("amount"));
                        return "OK";
                    } else if (obj.getString("result").equals("PAID")) {
                        return "PAID";
                    } else if (obj.getString("result").equals("PAID_OR_EXPIRED")) {
                        return "PAID_EXPIRED";
                    } else {
                        return null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (NetworkOnMainThreadException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String message) {
            bar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (message.equals("PAID")) {
                finish();
                Toast toast = Toast.makeText(getApplicationContext(), "Order has already been paid.", Toast.LENGTH_SHORT);
                toast.show();
            } else if (message.equals("PAID_OR_EXPIRED")) {
                finish();
                Toast toast = Toast.makeText(getApplicationContext(), "Order has expired.", Toast.LENGTH_SHORT);
                toast.show();
            } else if (message.equals("OK")) {
                finish();
                try {
                    InputStream is = null;
                    is = (InputStream) new URL(PaymentService.getPaymentLinkQR()).getContent();
                    PaymentService.setPaymentQRDrawable(Drawable.createFromStream(is, "src name"));
                    Intent intent = new Intent(OrderActivity.this, PaymentQR.class);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
