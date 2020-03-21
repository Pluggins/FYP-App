package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.adapter.MemberOrderItemAdapter;
import com.example.myapplication.adapter.OrderAdapter;
import com.example.myapplication.model.Menu;
import com.example.myapplication.model.OrderMenuItem;
import com.example.myapplication.service.CartService;
import com.example.myapplication.service.MemberService;
import com.example.myapplication.service.OrderService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MemberOrderItem extends AppCompatActivity {
    private ProgressBar bar;
    private DialogInterface.OnClickListener dialogClickListener = null;
    private MemberOrderItemAdapter aAdapter;
    private ListView listView;
    private ArrayList<OrderMenuItem> orderItems = (ArrayList<OrderMenuItem>) MemberService.getOrderItems();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        bar.setVisibility(View.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        List<OrderMenuItem> orderItems = MemberService.getOrderItems();
                        for (OrderMenuItem item : orderItems) {
                            com.example.myapplication.model.MenuItem menuItem = new com.example.myapplication.model.MenuItem();
                            menuItem.setId(item.getMenuItemId());
                            menuItem.setName(item.getItemName());
                            menuItem.setDisplayPrice("Unit Price: RM" + String.format("%.2f", item.getUnitPrice()));
                            menuItem.setPrice(BigDecimal.valueOf(item.getUnitPrice()));
                            CartService.addItem(menuItem, item.getQuantity());
                        }
                        onBackPressed();
                        Toast toast = Toast.makeText(getApplicationContext(), "Item has been added to cart.", Toast.LENGTH_SHORT);
                        toast.show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        bar = (ProgressBar) findViewById(R.id.order_progressbar);
        aAdapter = new MemberOrderItemAdapter(orderItems, this);
        listView = (ListView) findViewById(R.id.orderItemListView);
        listView.setAdapter(aAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.top_memberorderitem_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.payOrderBtn:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Would you like to add all the items to cart now?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
