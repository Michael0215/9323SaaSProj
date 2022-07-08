package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText tvStuNumber,tvStuPwd,tvStuConfirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvStuNumber = findViewById(R.id.et_username);
        tvStuPwd = findViewById(R.id.et_password);
        tvStuConfirmPwd = findViewById(R.id.et_confirm_password);

        Button register = findViewById(R.id.buttonRegister);
        Button cancel = findViewById(R.id.buttonCancel);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CheckInput()) {
//                    User user = new User();
//                    user.setUsername(tvStuNumber.getText().toString());
//                    user.setPassword(tvStuPwd.getText().toString());
//                    UserDbHelper dbHelper = new UserDbHelper(getApplicationContext(),UserDbHelper.DB_NAME,null,1);
//                    dbHelper.addUser(user);
                    Toast.makeText(RegisterActivity.this,"恭喜你注册成功!",Toast.LENGTH_SHORT).show();
                    finish();
                }
//                if (v.getId() == R.id.buttonRegister) {
//                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonCancel) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean CheckInput() {
        String username = tvStuNumber.getText().toString();
        String password = tvStuPwd.getText().toString();
        String confirm_password = tvStuConfirmPwd.getText().toString();
        if(username.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"用户名不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirm_password.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"确认密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.trim().equals(confirm_password.trim())) {
            Toast.makeText(RegisterActivity.this,"两次密码输入不一致!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}