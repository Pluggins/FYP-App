package com.example.myapplication.service;

import com.example.myapplication.model.MenuItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MenuItemService {
    private static String menuId = "";
    private static String menuName;
    private static String selectedMenuItemId = "";
    private static String selectedMenuItemName = "";
    private static List<MenuItem> menuItems = new ArrayList<MenuItem>();
    private static boolean init = false;

    public static void clear() {
        menuItems = new ArrayList<MenuItem>();
    }

    public static void addMenuItem(MenuItem item) {
        init = true;
        menuItems.add(item);
    }

    public static List<MenuItem> retrireveList() {
        return menuItems;
    }

    public static String getMenuName() {
        return menuName;
    }

    public static void setMenuName(String menuName) {
        MenuItemService.menuName = menuName;
    }

    public static String getMenuId() {
        return menuId;
    }

    public static void setMenuId(String menuId) {
        MenuItemService.menuId = menuId;
    }

    public static boolean isInit() {
        return init;
    }

    public static String getSelectedMenuItemId() {
        return selectedMenuItemId;
    }

    public static void setSelectedMenuItemId(String selectedMenuItemId) {
        MenuItemService.selectedMenuItemId = selectedMenuItemId;
    }

    public static String getSelectedMenuItemName() {
        return selectedMenuItemName;
    }

    public static void setSelectedMenuItemName(String selectedMenuItemName) {
        MenuItemService.selectedMenuItemName = selectedMenuItemName;
    }

    public static MenuItem retrieveItemById(String id) {
        MenuItem itemSelected = null;

        for (MenuItem item : menuItems) {
            if (item.getId().equals(id)) {
                itemSelected = item;
            }
        }

        return itemSelected;
    }
}
