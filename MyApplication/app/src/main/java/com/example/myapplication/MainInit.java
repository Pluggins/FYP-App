package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.se.omapi.Session;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.myapplication.model.Menu;
import com.example.myapplication.service.MenuService;
import com.example.myapplication.service.SessionService;
import com.example.myapplication.service.VendorService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainInit extends AppCompatActivity {
    private int choice = 0;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_init);

        setTitle("Welcome");
        final Button button = (Button) findViewById(R.id.button2);
        final Button button2 = (Button) findViewById(R.id.button3);
        bar = (ProgressBar) findViewById(R.id.progressbar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                LoadSession load = new LoadSession();
                choice = 1;
                load.execute();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                LoadSession load = new LoadSession();
                choice = 2;
                load.execute();
            }
        });
    }

    private class LoadSession extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // HTTPPOST to API
            String response = null;
            URL url = null;
            try {
                url = new URL("https://fyp.amazecraft.net/Api/User/CreateTempUserSession");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
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
            }
            return response;
        }

        @Override
        protected void onPostExecute(String message) {
            if (SessionService.isNewUser()) {
                try {
                    JSONObject obj = new JSONObject(message);
                    SessionService.setTemporarySession(obj.getString("sessionId"), obj.getString("sessionKey"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SessionService.setIsMember(false);
                SessionService.setNewUser(false);
            }

            LoadMenu loadMenu = new LoadMenu();
            loadMenu.execute();
        }
    }

    private class LoadMenu extends AsyncTask<String, Void, String>  {
        @Override
        protected String doInBackground(String[] params) {
            if (!MenuService.isInit()) {
                // HTTPPOST to API
                String response = null;
                URL url = null;
                try {
                    url = new URL("https://fyp.amazecraft.net/Api/Menu/RetrieveListByVendorId");
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
                    json.put("VendorId", VendorService.getVendorId());

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
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String message) {
            if (message != null) {
                try {
                    JSONObject obj = new JSONObject(message);
                    JSONArray jArray = obj.getJSONArray("menuList");
                    for (int i = 0; i < jArray.length() ; i++) {
                        JSONObject tmpObj = jArray.getJSONObject(i);
                        Menu newMenu = new Menu();
                        newMenu.setId(tmpObj.getString("id"));
                        newMenu.setName(tmpObj.getString("name"));
                        MenuService.addList(newMenu);
                    }
                    obj.get("menuList");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (choice == 1) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Intent intent = new Intent(MainInit.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (choice == 2) {
                LoadMemberCapture memberCapture = new LoadMemberCapture();
                memberCapture.execute();
            }
        }
    }

    private class LoadMemberCapture extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // HTTPPOST to API
            String response = null;
            URL url = null;
            try {
                url = new URL("https://fyp.amazecraft.net/Api/Capture/Generate");
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
                json.put("Type", 1);

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
                JSONObject obj = new JSONObject(message);
                SessionService.setQrUrl(obj.getString("captureQR"));
                SessionService.setCaptureId(obj.getString("captureId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            InputStream is = null;
            try {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                is = (InputStream) new URL(SessionService.getQrUrl()).getContent();
                SessionService.setQRLoginDrawable(Drawable.createFromStream(is, "src name"));
                Intent intent = new Intent(MainInit.this, MemberLogin.class);
                startActivity(intent);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
