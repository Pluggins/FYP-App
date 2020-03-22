package com.example.myapplication.ui.cart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.OrderActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.adapter.MenuAdapter;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Menu;
import com.example.myapplication.model.MenuItem;
import com.example.myapplication.model.OrderMenuItem;
import com.example.myapplication.service.CartService;
import com.example.myapplication.service.MenuItemService;
import com.example.myapplication.service.MenuService;
import com.example.myapplication.service.OrderService;
import com.example.myapplication.service.SessionService;
import com.example.myapplication.service.VendorService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CartFragment extends Fragment {

    private CartViewModel cartViewModel;
    private static ListView listView;
    private static ArrayAdapter aAdapter;
    private ArrayList<CartItem> cartItems = (ArrayList<CartItem>) CartService.retrieveCartItems();
    private FloatingActionButton fab;
    private DialogInterface.OnClickListener dialogClickListener = null;
    private static View root;
    private ProgressBar bar;
    private static FragmentActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        root = inflater.inflate(R.layout.fragment_cart, container, false);
        cartViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        setHasOptionsMenu(true);
        activity = getActivity();
        bar = (ProgressBar) root.findViewById(R.id.cart_progressbar);
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        bar.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        SubmitOrderList submitOrder = new SubmitOrderList();
                        submitOrder.execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        fab = root.findViewById(R.id.submitOrderfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CartService.getItemCount() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Submit order for processing now?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    Snackbar.make(root, "Your cart is empty.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        aAdapter = new CartAdapter(cartItems, getActivity());
        listView = (ListView) root.findViewById(R.id.cartListView);
        listView.setAdapter(aAdapter);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_cart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myOrderBtn:
                bar.setVisibility(View.VISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                LoadOrderList loadOrderList = new LoadOrderList();
                loadOrderList.execute();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SubmitOrderList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            if (!OrderService.isInitiatedOrder()) {
                String response = null;
                URL url = null;
                try {
                    url = new URL("https://fyp.amazecraft.net/Api/Order/Create");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoInput(true);

                    JSONObject json = new JSONObject();
                    json.put("type",2);
                    json.put("sessionId", SessionService.getSessionId());
                    json.put("sessionKey", SessionService.getSessionKey());
                    json.put("vendorId", VendorService.getVendorId());

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(json.toString());
                    wr.flush();

                    try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        StringBuilder sb = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            sb.append(responseLine.trim());
                        }
                        response = sb.toString();

                        JSONObject obj = new JSONObject(response);
                        OrderService.setOrderId(obj.getString("orderId"));
                        OrderService.setInitiatedOrder(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NetworkOnMainThreadException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String message) {
            SubmitOrderListPost post = new SubmitOrderListPost();
            post.execute(OrderService.getOrderId());
        }
    }

    private class SubmitOrderListPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            List<CartItem> cartItems = CartService.retrieveCartItems();
            Set<JSONObject> orderMenuItems = new HashSet<>();
            for (CartItem item : cartItems) {
                JSONObject newObj = new JSONObject();
                try {
                    if (item.getQuantity() != 0) {
                        newObj.put("itemId", item.getMenuItem().getId());
                        newObj.put("quantity", item.getQuantity());
                        orderMenuItems.add(newObj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String response = null;
            URL url = null;
            try {
                url = new URL("https://fyp.amazecraft.net/Api/Order/AddItemByOrderIdSeparate");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoInput(true);

                JSONObject json = new JSONObject();
                json.put("orderId", OrderService.getOrderId());
                json.put("items", orderMenuItems);

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                String newJson = json.toString().replace("\"[", "[").replace("]\"", "]").replace("\\\"", "\"");
                wr.write(newJson);
                wr.flush();

                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        sb.append(responseLine.trim());
                    }
                    response = sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NetworkOnMainThreadException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String message) {
            try {
                JSONObject obj = new JSONObject(message);
                String status = obj.getString("status");
                if (status.equals("OK")) {
                    CartService.clear();
                    aAdapter.clear();
                    aAdapter.notifyDataSetChanged();
                    Snackbar.make(root, "Order has been submitted successfully.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(root, "Error encountered, please try again.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            bar.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private class LoadOrderList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String response = null;
            URL url = null;
            try {
                url = new URL("https://fyp.amazecraft.net/Api/Order/GetActiveOrderById");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoInput(true);

                JSONObject json = new JSONObject();
                json.put("orderId", OrderService.getOrderId());

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(json.toString());
                wr.flush();

                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        sb.append(responseLine.trim());
                    }
                    response = sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NetworkOnMainThreadException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String message) {
            try {
                OrderService.clearOrder();
                JSONObject obj = new JSONObject(message);
                if (obj.getString("result").equals("ORDER_NOT_EXIST")) {
                    Snackbar.make(root, "Please submit an order first.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    bar.setVisibility(View.GONE);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    JSONArray jArray = obj.getJSONArray("items");
                    for (int i = 0; i < jArray.length() ; i++) {
                        JSONObject tmpObj = jArray.getJSONObject(i);
                        OrderMenuItem menuItem = new OrderMenuItem(tmpObj.getString("orderItemId"), tmpObj.getString("name"), tmpObj.getDouble("orderItemUnitPrice"), tmpObj.getInt("quantity"), tmpObj.getInt("status"));
                        OrderService.addItem(menuItem);
                    }
                    bar.setVisibility(View.GONE);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Intent intent = new Intent(getActivity(), OrderActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static FragmentActivity getThisActivity() {
        return activity;
    }

    public static void resetAdapter() {
        aAdapter = new CartAdapter((ArrayList<CartItem>)CartService.retrieveCartItems(), CartFragment.getThisActivity());
        listView = (ListView) root.findViewById(R.id.cartListView);
        listView.setAdapter(aAdapter);
    }
}