
package com.floridahacksibm.mangrovetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentActivity extends AppCompatActivity {

    String userID;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        TextView valueView = findViewById(R.id.value);
        ImageView incrementView = findViewById(R.id.increment);
        ImageView decrementView = findViewById(R.id.decrement);
        TextView itemPrice = findViewById(R.id.itemPrice);
        TextView totalPrice = findViewById(R.id.totalPrice);
        ConstraintLayout payButton = findViewById(R.id.payButton);

        incrementView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.parseInt(valueView.getText().toString());
                if (current <= 0) {
                    decrementView.setColorFilter(getResources().getColor(R.color.black));
                }
                int newValue = current+1;
                valueView.setText(String.valueOf(newValue));
                String price = "$"+newValue*10;
                itemPrice.setText(price);
                totalPrice.setText(price);
            }
        });
        decrementView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.parseInt(valueView.getText().toString());
                int newValue = current-1;
                if (newValue <= 0) {
                    decrementView.setColorFilter(getResources().getColor(R.color.gray));
                }
                if (current > 0) {
                    valueView.setText(String.valueOf(newValue));
                    String price = "$"+newValue*10;
                    itemPrice.setText(price);
                    totalPrice.setText(price);
                }
            }
        });

        backButton = findViewById(R.id.backIcon);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donateTrees(Integer.parseInt(valueView.getText().toString()));

            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            userID = extras.getString("userID");
        }
    }

    private void donateTrees(int amount) {
        RequestBody requestBody = buildRequestBody(userID, amount);
        String url = "https://mangrove-backend-api.us-south.cf.appdomain.cloud/api/users/donate";

        OkHttpClient client = new OkHttpClient();
        Request postRequest = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        client.newCall(postRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PaymentActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            System.out.println(s);
                            JSONObject obj = new JSONObject(s);
                            Intent intent = new Intent(PaymentActivity.this, DonateConfirmActivity.class);
                            intent.putExtra("normalSeeds", obj.getInt("normal"));
                            intent.putExtra("specialSeeds", obj.getInt("special"));
                            startActivity(intent);
                            finish();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    // Build the media request body for image file
    private RequestBody buildRequestBody(String userID, int amount) {
        String json = "{" +
                "\"userID\":\""+userID+"\"," +
                "\"amount\":"+amount +
                "}";
        return RequestBody.create(MediaType.parse("application/json"), json);
    }
}