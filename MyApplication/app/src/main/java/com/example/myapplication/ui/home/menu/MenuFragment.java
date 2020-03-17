package com.example.myapplication.ui.home.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MenuAdapter;
import com.example.myapplication.model.Menu;
import com.example.myapplication.service.MenuService;
import com.example.myapplication.service.SessionService;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private MenuViewModel menuViewModel;
    private ListView listView;
    private ArrayAdapter aAdapter;
    private ArrayList<Menu> menus = (ArrayList<Menu>) MenuService.retrieveList();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        menuViewModel =
                ViewModelProviders.of(this).get(MenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        //final TextView textView = root.findViewById(R.id.text_menu);
        menuViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        aAdapter = new MenuAdapter(menus, getActivity());
        listView = (ListView) root.findViewById(R.id.menuListView);
        listView.setAdapter(aAdapter);

        return root;
    }
}