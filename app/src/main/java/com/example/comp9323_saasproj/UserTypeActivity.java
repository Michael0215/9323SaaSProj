package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/* This class creates the activity for the UserType page, users can register an account based
   on their user type: STUDENT/STAFF or EXPERT, if users already have an account, he or she can also
   jump back to the login page */

//rewrite the interface in AppCompatActivity: onCreate()
public class UserTypeActivity extends AppCompatActivity {
    // initialise the widgets created in activity_user_type.xml correspondingly
    private Button btnStudentstaff;
    private Button btnExpert;
    private TextView btnCancel;

    //create actions for each widget
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        btnStudentstaff = findViewById(R.id.studentstaff);
        btnExpert = findViewById(R.id.expert);
        btnCancel = findViewById(R.id.Cancel);

        // listen for action of the button STUDENT/STAFF
        btnStudentstaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // jump from UserTypeActivity.java to RegisterActivity.java
                Intent intent = new Intent(UserTypeActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        // listen for action of the textview 'Already have a account? Sign in here'
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            } //jump back to the previous page 'login'
        });

        // listen for action of the button EXPERT
        btnExpert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // jump from UserTypeActivity.java to ExpertRegisterActivity.java
                Intent intent = new Intent(UserTypeActivity.this, ExpertRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
