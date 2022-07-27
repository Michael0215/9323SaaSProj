package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserTypeActivity extends AppCompatActivity {

    private Button btnStudentstaff;
    private Button btnExpert;
    private TextView btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        btnStudentstaff = findViewById(R.id.studentstaff);
        btnExpert = findViewById(R.id.expert);
        btnCancel = findViewById(R.id.Cancel);

        btnStudentstaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserTypeActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnExpert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserTypeActivity.this, ExpertRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
