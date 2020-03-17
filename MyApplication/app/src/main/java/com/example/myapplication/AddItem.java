package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.service.CartService;
import com.example.myapplication.service.MenuItemService;
import com.example.myapplication.service.SessionService;
import com.google.android.material.snackbar.Snackbar;

public class AddItem extends AppCompatActivity {
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
        title.setText(MenuItemService.getSelectedMenuItemName());

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
                com.example.myapplication.model.MenuItem selectedItem = MenuItemService.retrieveItemById(MenuItemService.getSelectedMenuItemId());
                CartService.addItem(selectedItem, Integer.parseInt(itemQuantity.getText().toString()));
                Intent intent = null;
                if (SessionService.getType() == 1) {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                } else if (SessionService.getType() == 2) {
                    intent = new Intent(getApplicationContext(), MainActivityMember.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast toast = Toast.makeText(getApplicationContext(), "Item has been added to cart.", Toast.LENGTH_SHORT);
                toast.show();

            }
        });
    }
}
