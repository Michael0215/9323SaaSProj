package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class PersonalCenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        AppCompatImageView btnCancel = findViewById(R.id.buttonCancel);
        btnCancel.setOnClickListener(view -> onBackPressed());
    }
}