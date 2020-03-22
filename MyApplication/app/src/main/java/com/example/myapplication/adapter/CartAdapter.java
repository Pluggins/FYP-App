package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myapplication.AddItemCart;
import com.example.myapplication.R;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Menu;
import com.example.myapplication.model.MenuItem;
import com.example.myapplication.service.CartService;
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

public class CartAdapter extends ArrayAdapter<CartItem> {
    private List<CartItem> dataSet;
    Context mContext;

    public CartAdapter(ArrayList<CartItem> data, Context context) {
        super(context, R.layout.listrow_menu, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final CartItem cartItem = getItem(position);

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(R.layout.listrow_cartitem, parent,false);

            TextView txtId = (TextView) v.findViewById(R.id.cartItemId);
            TextView txtName = (TextView) v.findViewById(R.id.cartItemName);
            TextView txtPrice = (TextView) v.findViewById(R.id.cartItemPrice);
            TextView txtTotal = (TextView) v.findViewById(R.id.cartItemTotal);
            TextView txtQuantity = (TextView) v.findViewById(R.id.cartItemQuantity);
            txtId.setText(cartItem.getMenuItem().getId());
            txtName.setText(cartItem.getMenuItem().getName());
            txtPrice.setText(cartItem.getMenuItem().getPrice().toString());
            txtTotal.setText("RM"+String.format("%.2f",cartItem.getMenuItem().getPrice().doubleValue() * cartItem.getQuantity()));
            txtQuantity.setText(String.valueOf(cartItem.getQuantity()));
        }

        v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CartService.setSelectedItem(cartItem);
                Intent intent = new Intent(mContext, AddItemCart.class);
                mContext.startActivity(intent);
            }
        });
        return v;
    }

    private class LoadMenuItem extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String id = MenuItemService.getMenuId();
            if (!id.equals(params[0])) {
                // HTTPPOST to API
                String response = null;
                URL url = null;
                try {
                    url = new URL("https://fyp.amazecraft.net/Api/MenuItem/RetrieveListByMenuId");
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
                    json.put("MenuId", params[0]);

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

            Intent intent = new Intent(mContext, com.example.myapplication.MenuItem.class);
            mContext.startActivity(intent);
        }
    }
}
