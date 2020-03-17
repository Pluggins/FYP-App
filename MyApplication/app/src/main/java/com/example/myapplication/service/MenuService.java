package com.example.myapplication.service;

import com.example.myapplication.model.Menu;

import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private static List<Menu> menuList = new ArrayList<Menu>();
    private static boolean init = false;

    public static void clear() {
        menuList = new ArrayList<Menu>();
    }

    public static void addList(Menu menu) {
        init = true;
        menuList.add(menu);
    }

    public static List<Menu> retrieveList() {
        return menuList;
    }

    public static boolean isInit() {
        return init;
    }
}
