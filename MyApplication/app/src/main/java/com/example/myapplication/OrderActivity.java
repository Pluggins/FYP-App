package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.adapter.MenuItemAdapter;
import com.example.myapplication.adapter.OrderAdapter;
import com.example.myapplication.model.MenuItem;
import com.example.myapplication.model.OrderMenuItem;
import com.example.myapplication.service.MenuItemService;
import com.example.myapplication.service.OrderService;
import com.example.myapplication.ui.cart.CartFragment;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter aAdapter;
    private DialogInterface.OnClickListener dialogClickListener = null;
    private ArrayList<OrderMenuItem> orderItems = (ArrayList<OrderMenuItem>) OrderService.getItems();
    private boolean isAllItem = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        aAdapter = new OrderAdapter(orderItems, this);
        listView = (ListView) findViewById(R.id.orderItemListView);
        listView.setAdapter(aAdapter);
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
                    builder.setMessage("Would you want to pay for all the items now?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Would you want to pay for the selected items now?").setPositiveButton("Yes", dialogClickListener)
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
}
