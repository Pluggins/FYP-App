package com.example.myapplication.service;

import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Menu;
import com.example.myapplication.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    private static List<CartItem> itemList = new ArrayList<CartItem>();
    private static CartItem selectedItem;

    public static void addItem(MenuItem menuItem, int quantity) {
        boolean changed = false;
        for (CartItem item : itemList) {
            if (!changed) {
                if (item.getMenuItem().getId().equals(menuItem.getId())) {
                    int endQuantity = item.getQuantity() + quantity;
                    if (endQuantity < 0) {
                        item.setQuantity(0);
                    } else {
                        item.setQuantity(item.getQuantity() + quantity);
                    }

                    changed = true;
                }
            }
        }

        if (!changed) {
            CartItem newItem = new CartItem();
            newItem.setMenuItem(menuItem);
            if (quantity < 0) {
                newItem.setQuantity(0);
            } else {
                newItem.setQuantity(quantity);
            }

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

    public static CartItem getSelectedItem() {
        return selectedItem;
    }

    public static void setSelectedItem(CartItem selectedItem) {
        CartService.selectedItem = selectedItem;
    }
}
