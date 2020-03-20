package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Menu;
import com.example.myapplication.model.MenuItem;
import com.example.myapplication.service.MenuItemService;

import java.util.ArrayList;
import java.util.List;

public class MenuItemAdapter extends ArrayAdapter<MenuItem> {
    private List<MenuItem> dataSet;
    Context mContext;

    public MenuItemAdapter(ArrayList<MenuItem> data, Context context) {
        super(context, R.layout.listrow_menuitem, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        MenuItem menuItem = getItem(position);

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(R.layout.listrow_menuitem, parent,false);

            TextView txtId = (TextView) v.findViewById(R.id.menuItemId);
            TextView txtName = (TextView) v.findViewById(R.id.menuItemName);
            TextView txtDesc = (TextView) v.findViewById(R.id.menuItemDescription);
            TextView txtPrice = (TextView) v.findViewById(R.id.menuItemPrice);
            txtId.setText(menuItem.getId());
            txtName.setText(menuItem.getName());
            txtDesc.setText(menuItem.getShortDesc());
            txtPrice.setText(menuItem.getDisplayPrice());
        }

        v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TextView txtName = (TextView) view.findViewById(R.id.menuItemName);
                TextView txtId = (TextView) view.findViewById(R.id.menuItemId);
                MenuItemService.setSelectedMenuItemName(txtName.getText().toString());
                MenuItemService.setSelectedMenuItemId(txtId.getText().toString());

                Intent intent = new Intent(mContext, com.example.myapplication.AddItem.class);
                mContext.startActivity(intent);
            }
        });

        return v;
    }
}
