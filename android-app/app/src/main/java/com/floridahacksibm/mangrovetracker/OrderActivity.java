package com.floridahacksibm.mangrovetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {

    private String orderID;

    private TextView processingView;
    private TextView preparingView;
    private TextView readyView;
    private TextView pickedUpView;

    Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        TextView orderIDView = findViewById(R.id.orderID);
        processingView = findViewById(R.id.processingTitle);
        preparingView = findViewById(R.id.preparingTitle);
        readyView = findViewById(R.id.readyTitle);
        pickedUpView = findViewById(R.id.pickedUpTitle);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            orderID = extras.getString("orderID");
            orderIDView.setText(orderID);
        }

        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                updateOrder();
            }
        }, 0, 2000);
    }

    private void updateOrder() {
//        String url = "https://mangrove-backend-api.us-south.cf.appdomain.cloud/api/orders/create";
        String url = "http://192.168.1.186:8000/api/orders/"+orderID;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OrderActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String s = response.body().string();
                            JSONObject obj = new JSONObject(s);
                            String status = obj.getString("status");
                            int black = ContextCompat.getColor(getApplicationContext(), R.color.black);
                            switch(status) {
                                case "processing":
                                    processingView.setTextColor(black);
                                    break;
                                case "preparing":
                                    processingView.setTextColor(black);
                                    preparingView.setTextColor(black);
                                    break;
                                case "ready":
                                    processingView.setTextColor(black);
                                    preparingView.setTextColor(black);
                                    readyView.setTextColor(black);
                                    break;
                                case "pickedup":
                                    processingView.setTextColor(black);
                                    preparingView.setTextColor(black);
                                    readyView.setTextColor(black);
                                    pickedUpView.setTextColor(black);
                                    break;
                                default:
                                    break;
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}