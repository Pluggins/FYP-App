package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.R;
import com.example.myapplication.model.MemberOrder;
import com.example.myapplication.model.MenuItem;
import com.example.myapplication.service.MenuItemService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MemberOrderAdapter extends ArrayAdapter<MemberOrder> {
    private List<MemberOrder> dataSet;
    Context mContext;
    private ProgressBar pb;
    View gView;

    public MemberOrderAdapter(ArrayList<MemberOrder> data, Context context) {
        super(context, R.layout.listrow_order, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final MemberOrder order = getItem(position);

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(R.layout.listrow_memberorder, parent,false);
            gView = vi.inflate(R.layout.listrow_memberorder, parent,false);

            TextView orderDate = (TextView) v.findViewById(R.id.memberOrderDate);
            TextView orderPrice = (TextView) v.findViewById(R.id.memberOrderPrice);
            orderDate.setText(order.getOrderDate());
            orderPrice.setText(order.getPriceDisplay());
        }
        v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LoadOrderItem load = new LoadOrderItem();
                load.execute(order.getOrderId());
            }
        });

        return v;
    }

    private class LoadOrderItem extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // HTTPPOST to API
            String response = null;
            URL url = null;
            try {
                url = new URL("https://fyp.amazecraft.net/Api/Order/RetrieveOrderItemsByOrderId");
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
                json.put("orderId", params[0]);

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
                    JSONArray jArray = obj.getJSONArray("menuItemList");
                    MenuItemService.clear();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject tmpObj = jArray.getJSONObject(i);
                        MenuItem newMenuItem = new MenuItem();
                        newMenuItem.setId(tmpObj.getString("id"));
                        newMenuItem.setName(tmpObj.getString("name"));
                        newMenuItem.setShortDesc(tmpObj.getString("shortDesc"));
                        newMenuItem.setPrice(BigDecimal.valueOf(Double.parseDouble(tmpObj.getString("price"))));
                        MenuItemService.addMenuItem(newMenuItem);
                    }

                    MenuItemService.setMenuName(obj.getString("menuName"));
                    MenuItemService.setMenuId(obj.getString("menuId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            pb.setVisibility(View.GONE);
            Intent intent = new Intent(mContext, com.example.myapplication.MenuItem.class);
            mContext.startActivity(intent);
        }
    }
}
