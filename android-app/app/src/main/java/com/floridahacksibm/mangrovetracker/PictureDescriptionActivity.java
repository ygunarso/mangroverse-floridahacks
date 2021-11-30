package com.floridahacksibm.mangrovetracker;

import static androidx.camera.core.CameraX.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PictureDescriptionActivity extends AppCompatActivity {

    private ImageView imageView;
    private String path;
    private Button scanButton;
    private Button submitButton;
    private EditText description;
    private EditText IDView;

    private AlertDialog.Builder builder;
    private String treeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_description);

        description = findViewById(R.id.description);
        IDView = findViewById(R.id.ID);
        builder = new AlertDialog.Builder(PictureDescriptionActivity.this);

        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PictureDescriptionActivity.this, QRCodeActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = extras.getString("path");
        }

        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (treeID.isEmpty()) {
                    Toast.makeText(PictureDescriptionActivity.this, "You must enter ID or scan QR Code", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PictureDescriptionActivity.this, "Planting...", Toast.LENGTH_SHORT).show();
                    uploadImage(path);
                }
            }
        });

        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        imageView.setRotation(90);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                treeID = data.getStringExtra("id");
                IDView.setText(treeID);
            }
        }
    }

    private void plantTree(String treeID, String userID, double height, String pictureURL, String desc) {
        RequestBody requestBody = buildRequestBody(treeID, userID, height, pictureURL, desc);
//        String url = "https://mangrove-backend-api.us-south.cf.appdomain.cloud/api/contributions/create";
        String url = "http://192.168.1.186:8000/api/contributions/create";
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
                        Toast.makeText(PictureDescriptionActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            builder.setMessage("Your tree has been successfully planted!")
                                    .setPositiveButton("Return to Main Menu", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Open next activity
                                            Intent intent = new Intent(PictureDescriptionActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.setTitle("Congratulations!");
                            alert.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void uploadImage(String path) {
        File file = new File(path);
        RequestBody requestBody = buildRequestBody(file);
//        String url = "https://mangrove-backend-api.us-south.cf.appdomain.cloud/api/trees/image/upload";
        String url = "http://192.168.1.186:8000/api/contributions/upload/"+treeID;
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
                        Toast.makeText(PictureDescriptionActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            String url = response.body().string();
                            System.out.println(url);
                            plantTree(treeID, "185e3483fbd97f5613833efcd09311ba", 5, url, "I'm the best");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    // Build the media request body for image file
    private RequestBody buildRequestBody(String treeID, String userID, double height, String pictureURL, String desc) {
        String json = "{" +
                "\"treeID\":\""+treeID+"\"," +
                "\"userID\":\""+userID+"\"," +
                "\"type\":\""+"plant"+"\"," +
                "\"height\":"+height+"," +
                "\"picture\":\""+pictureURL+"\"," +
                "\"description\":\""+desc+"\"" +
                "}";
        return RequestBody.create(MediaType.parse("application/json"), json);
    }

    // Build the media request body for image file
    private RequestBody buildRequestBody(File file) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), file))
                .build();
    }
}