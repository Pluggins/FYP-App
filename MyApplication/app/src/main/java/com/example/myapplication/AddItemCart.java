package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.MenuItem;
import com.example.myapplication.service.CartService;
import com.example.myapplication.service.MemberService;
import com.example.myapplication.service.SessionService;
import com.example.myapplication.ui.cart.CartFragment;

import java.math.BigDecimal;

public class AddItemCart extends AppCompatActivity {
    EditText itemQuantity;
    ImageButton addBtn;
    ImageButton minusBtn;
    TextView title;
    Button backBtn;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        title = (TextView) findViewById(R.id.itemTitle);
        itemQuantity = (EditText) findViewById(R.id.itemQuantity);
        addBtn = (ImageButton) findViewById(R.id.itemAddBtn);
        minusBtn = (ImageButton) findViewById(R.id.itemMinusBtn);
        backBtn = (Button) findViewById(R.id.addBackBtn);
        submitBtn = (Button) findViewById(R.id.addItemBtn);
        title.setText(CartService.getSelectedItem().getMenuItem().getName());
        itemQuantity.setText(String.valueOf(CartService.getSelectedItem().getQuantity()));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(itemQuantity.getText().toString());
                itemQuantity.setText(String.valueOf(++amount));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(itemQuantity.getText().toString());
                itemQuantity.setText(String.valueOf(--amount));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CartItem item : CartService.retrieveCartItems()) {
                    if (item.getMenuItem().getId().equals(CartService.getSelectedItem().getMenuItem().getId())) {
                        int sum;
                        try {
                            sum = Integer.parseInt(itemQuantity.getText().toString());
                        } catch (NumberFormatException e) {
                            sum = 0;
                        }
                        if (sum < 0) {
                            item.setQuantity(0);
                        } else {
                            item.setQuantity(sum);
                        }
                    }
                }
                CartFragment.resetAdapter();
                onBackPressed();
                finish();
                Toast toast = Toast.makeText(getApplicationContext(), "Quantity has been adjusted.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
