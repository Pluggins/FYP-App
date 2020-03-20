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

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.R;
import com.example.myapplication.model.Menu;
import com.example.myapplication.model.MenuItem;
import com.example.myapplication.model.OrderMenuItem;
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

public class OrderAdapter extends ArrayAdapter<OrderMenuItem> {
    private List<OrderMenuItem> dataSet;
    Context mContext;
    private ProgressBar pb;
    View gView;

    public OrderAdapter(ArrayList<OrderMenuItem> data, Context context) {
        super(context, R.layout.listrow_order, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final OrderMenuItem orderItem = getItem(position);

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(R.layout.listrow_order, parent,false);
            gView = vi.inflate(R.layout.listrow_order, parent,false);

            TextView txtId = (TextView) v.findViewById(R.id.orderItemId);
            TextView txtSelectedQuantity = (TextView) v.findViewById(R.id.orderSelectedQuantity);
            TextView txtItemQuantity = (TextView) v.findViewById(R.id.orderItemQuantity);
            TextView txtItemUnitPrice = (TextView) v.findViewById(R.id.orderItemPrice);
            TextView txtItemName = (TextView) v.findViewById(R.id.orderItemName);
            txtId.setText(orderItem.getItemId());
            txtSelectedQuantity.setText("0");
            txtItemQuantity.setText(String.valueOf(orderItem.getQuantity()));
            txtItemName.setText(orderItem.getItemName());
            txtItemUnitPrice.setText("Unit Price: RM" + String.format("%.2f", orderItem.getUnitPrice()));
            if (orderItem.getStatus() == 2) {
                v.setBackgroundColor(Color.parseColor("#34eb9e"));
            }
        }
        v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (orderItem.getStatus() == 1) {
                    final TextView orderItemQuantity = (TextView) view.findViewById(R.id.orderItemQuantity);
                /*
                ((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                pb = view.getRootView().findViewById(R.id.menu_progressbar);
                pb.setVisibility(View.VISIBLE);
                */

                    AlertDialog.Builder alert = new AlertDialog.Builder((Activity) mContext);

                    alert.setTitle("Set Quantity");
                    alert.setMessage("Please input the quantity to pay.");
                    final EditText input = new EditText((Activity)mContext);
                    alert.setView(input);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            try {
                                if (Integer.parseInt(input.getText().toString()) <= orderItem.getQuantity() && Integer.parseInt(input.getText().toString()) >= 0) {
                                    orderItemQuantity.setText(String.valueOf(orderItem.getQuantity()) + " ("+input.getText().toString()+")");
                                    orderItem.setSelectedQuantity(Integer.parseInt(input.getText().toString()));
                                } else if (Integer.parseInt(input.getText().toString()) < 0) {
                                    orderItemQuantity.setText(String.valueOf(orderItem.getQuantity()) + " (0)");
                                    orderItem.setSelectedQuantity(0);
                                } else {
                                    orderItemQuantity.setText(String.valueOf(orderItem.getQuantity()) + " ("+orderItem.getQuantity()+")");
                                    orderItem.setSelectedQuantity(orderItem.getQuantity());
                                }
                            } catch (NumberFormatException e) {
                                orderItemQuantity.setText(String.valueOf(orderItem.getQuantity()) + " (0)");
                                orderItem.setSelectedQuantity(0);
                            }

                        /*
                        items.set(pos, input.getText().toString());
                        adapterObject.notifyDataSetChanged();  // the adapter you set in the listView.setAdapter();

                         */
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });


                    alert.show();
                /*
                TextView txtId = view.findViewById(R.id.menuId);
                OrderAdapter.LoadMenuItem loadMenuItem = new OrderAdapter.LoadMenuItem();
                loadMenuItem.execute(txtId.getText().toString());

                 */

                    //MenuItemService.addMenuItem();
                }
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
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            pb.setVisibility(View.GONE);
            Intent intent = new Intent(mContext, com.example.myapplication.MenuItem.class);
            mContext.startActivity(intent);
        }
    }
}
