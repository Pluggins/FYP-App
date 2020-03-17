package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.adapter.MenuItemAdapter;
import com.example.myapplication.service.MenuItemService;

import java.util.ArrayList;

public class MenuItem extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter aAdapter;
    private ArrayList<com.example.myapplication.model.MenuItem> menuItems = (ArrayList<com.example.myapplication.model.MenuItem>) MenuItemService.retrireveList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);
        setTitle(MenuItemService.getMenuName());
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        aAdapter = new MenuItemAdapter(menuItems, this);
        listView = (ListView) findViewById(R.id.menuItemListView);
        listView.setAdapter(aAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
