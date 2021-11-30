package com.floridahacksibm.mangrovetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DonateConfirmActivity extends AppCompatActivity {

    int special;
    int normal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_confirm);
        ConstraintLayout okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            normal = extras.getInt("normalSeeds");
            special = extras.getInt("specialSeeds");
        }

        if (normal > 0) {
            ConstraintLayout normalLayout = findViewById(R.id.normalSeeds);
            normalLayout.setVisibility(View.VISIBLE);
            TextView normalCount = findViewById(R.id.normalSeedCount);
            String normalText = "x"+normal;
            normalCount.setText(normalText);
        }

        if (special > 0) {
            ConstraintLayout specialLayout = findViewById(R.id.specialSeeds);
            specialLayout.setVisibility(View.VISIBLE);
            TextView specialCount = findViewById(R.id.specialSeedCount);
            String specialText = "x"+special;
            specialCount.setText(specialText);
        }


    }
}