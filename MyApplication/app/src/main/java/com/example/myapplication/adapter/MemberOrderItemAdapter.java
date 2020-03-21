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

import com.example.myapplication.AddItemMemberOrder;
import com.example.myapplication.MemberOrderItem;
import com.example.myapplication.R;
import com.example.myapplication.model.MenuItem;
import com.example.myapplication.model.OrderMenuItem;
import com.example.myapplication.service.MemberService;
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

public class MemberOrderItemAdapter extends ArrayAdapter<OrderMenuItem> {
    private List<OrderMenuItem> dataSet;
    Context mContext;
    private ProgressBar pb;
    View gView;

    public MemberOrderItemAdapter(ArrayList<OrderMenuItem> data, Context context) {
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
                MemberService.setSelectedItem(orderItem);
                Intent intent = new Intent(mContext, AddItemMemberOrder.class);
                mContext.startActivity(intent);
            }
        });

        return v;
    }
}
