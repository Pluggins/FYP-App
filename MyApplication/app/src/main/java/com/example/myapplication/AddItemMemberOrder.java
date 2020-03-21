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

import com.example.myapplication.model.MenuItem;
import com.example.myapplication.service.CartService;
import com.example.myapplication.service.MemberService;
import com.example.myapplication.service.MenuItemService;
import com.example.myapplication.service.SessionService;

import java.math.BigDecimal;

public class AddItemMemberOrder extends AppCompatActivity {
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
        title.setText(MemberService.getSelectedItem().getItemName());
        itemQuantity.setText(String.valueOf(MemberService.getSelectedItem().getQuantity()));
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
                MenuItem menuItem = new MenuItem();
                menuItem.setName(MemberService.getSelectedItem().getItemName());
                menuItem.setPrice(BigDecimal.valueOf(MemberService.getSelectedItem().getUnitPrice()));
                menuItem.setDisplayPrice("Unit Price: RM" + String.format("%.2f", MemberService.getSelectedItem().getUnitPrice()));
                menuItem.setId(MemberService.getSelectedItem().getMenuItemId());
                CartService.addItem(menuItem, Integer.parseInt(itemQuantity.getText().toString()));
                Intent intent = null;
                if (SessionService.getType() == 1) {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                } else if (SessionService.getType() == 2) {
                    intent = new Intent(getApplicationContext(), MainActivityMember.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                Toast toast = Toast.makeText(getApplicationContext(), "Item has been added to cart.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
