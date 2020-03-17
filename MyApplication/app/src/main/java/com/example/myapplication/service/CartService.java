package com.example.myapplication.service;

import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Menu;
import com.example.myapplication.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    private static List<CartItem> itemList = new ArrayList<CartItem>();

    public static void addItem(MenuItem menuItem, int quantity) {
        boolean changed = false;
        for (CartItem item : itemList) {
            if (!changed) {
                if (item.getMenuItem().getId().equals(menuItem.getId())) {
                    item.setQuantity(item.getQuantity() + quantity);
                    changed = true;
                }
            }
        }

        if (!changed) {
            CartItem newItem = new CartItem();
            newItem.setMenuItem(menuItem);
            newItem.setQuantity(quantity);

            itemList.add(newItem);
        }
    }

    public static void clear() {
        itemList = new ArrayList<CartItem>();
    }

    public static List<CartItem> retrieveCartItems() {
        return itemList;
    }

    public static int getItemCount() {
        return itemList.size();
    }
}
