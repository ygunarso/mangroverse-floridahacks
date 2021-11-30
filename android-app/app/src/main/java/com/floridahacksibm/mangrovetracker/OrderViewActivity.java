package com.floridahacksibm.mangrovetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class OrderViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        ConstraintLayout orderSent = findViewById(R.id.orderSent);
        orderSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView check = findViewById(R.id.checkIcon1);
                check.setImageResource(R.drawable.ic_check_selected);
            }
        });

        ConstraintLayout orderConfirmed = findViewById(R.id.orderConfirmed);
        orderConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView check = findViewById(R.id.checkIcon2);
                check.setImageResource(R.drawable.ic_check_selected);
            }
        });

        ConstraintLayout pickupReady = findViewById(R.id.pickupReady);
        pickupReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView check = findViewById(R.id.checkIcon3);
                check.setImageResource(R.drawable.ic_check_selected);
            }
        });

        ConstraintLayout pickupConfirmed = findViewById(R.id.pickupConfirmed);
        pickupConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView check = findViewById(R.id.checkIcon4);
                check.setImageResource(R.drawable.ic_check_selected);
                ConstraintLayout bottomButton = findViewById(R.id.confirmButton);
                bottomButton.setBackgroundResource(R.drawable.donate_confirm_button);
            }
        });
    }
}