package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText tvStuNumber,tvStuPwd,tvStuConfirmPwd;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

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
        ImageButton cancel = findViewById(R.id.buttonCancel);

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CheckInput()) {
                    registerUser();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonCancel) {
                    finish();
                }
            }
        });
    }
    private void registerUser(){
        FirebaseFirestore firestoreDatabase;
        firestoreDatabase = FirebaseFirestore.getInstance();
        String email = tvStuNumber.getText().toString().trim();
        String password= tvStuPwd.getText().toString().trim();

        Map<String, Object> user = new HashMap<>();
        user.put("E-mail", email);
        user.put("Password", password);
        user.put("Type", "common");
        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            firestoreDatabase.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(RegisterActivity.this, "Registered Successfully!\nNow you can login.", Toast.LENGTH_SHORT).show();
                                            progressDialog.cancel();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Registered failed, try email format again", Toast.LENGTH_SHORT).show();
                                            progressDialog.cancel();
                                        }
                                    });
                        }else{
                            Toast.makeText(RegisterActivity.this, "Registered failed, try email format again", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
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
            Toast.makeText(RegisterActivity.this,"Confirmed password cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.trim().equals(confirm_password.trim())) {
            Toast.makeText(RegisterActivity.this,"Password input is inconsistent!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length()<6){
            Toast.makeText(RegisterActivity.this,"Please create a stronger password, your password should be at least 6 characters!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}