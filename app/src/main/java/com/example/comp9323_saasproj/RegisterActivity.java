package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText tvStuNumber,tvStuPwd,tvStuConfirmPwd;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        tvStuNumber = findViewById(R.id.et_username);
        tvStuPwd = findViewById(R.id.et_password);
        tvStuConfirmPwd = findViewById(R.id.et_confirm_password);

        Button register = findViewById(R.id.buttonRegister);
        Button cancel = findViewById(R.id.buttonCancel);


        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        FirebaseUser user = firebaseAuth.getCurrentUser();


        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CheckInput()) {
//                    User user = new User();
//                    user.setUsername(tvStuNumber.getText().toString());
//                    user.setPassword(tvStuPwd.getText().toString());
//                    UserDbHelper dbHelper = new UserDbHelper(getApplicationContext(),UserDbHelper.DB_NAME,null,1);
//                    dbHelper.addUser(user);
                    registerUser();
//                    Toast.makeText(RegisterActivity.this,"Congratulations! Registration succeeded!",Toast.LENGTH_SHORT).show();
//                    finish();
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
//                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private void registerUser(){
        String email = tvStuNumber.getText().toString().trim();
        String password= tvStuPwd.getText().toString().trim();
//        System.out.println("here1");
//        System.out.println(email);
//        System.out.println(password);
//        if(TextUtils.isEmpty(email)){
//            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
//            return;
//            //email is empty, stopping the function execution
//        }
//        if(TextUtils.isEmpty(password)){
//            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
//            return;
//            //email is empty, stopping the function execution
//        }
        progressDialog.setMessage("Registering User...");
        progressDialog.show();
//        System.out.println("here2");
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        System.out.println("here3");
                        if(task.isSuccessful()) {
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                            updateUI(user);
                            //user is successfully reg and logged in
                            // Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            //progressDialog.cancel();
                            //execute profile
                            //startActivity(new Intent(getApplicationContext(), Login.class));
                            createUserStructure();
                            Toast.makeText(RegisterActivity.this, "Registered Successfully\nNow you can login", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this, "Registered failed, try again", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }

    private void createUserStructure(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String name = "Your Name:";
        String faculty ="Your Faculty";
        String id = user.getUid();
        String mailAddress = user.getEmail();
        String course = "Your Course";
        UserInformation userInformation = new UserInformation(id,name,faculty,mailAddress,course);
        databaseReference.child(id).setValue(userInformation);
    }


    public boolean CheckInput() {
        String username = tvStuNumber.getText().toString();
        String password = tvStuPwd.getText().toString();
        String confirm_password = tvStuConfirmPwd.getText().toString();
        if(username.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"User name cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"Password cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirm_password.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"Confirm password cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.trim().equals(confirm_password.trim())) {
            Toast.makeText(RegisterActivity.this,"Password input is inconsistent!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length()<=6){
            Toast.makeText(RegisterActivity.this,"Please create a longer password more than 6 characters!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

//    private void updateUI(FirebaseUser user) {
//
//    }
}