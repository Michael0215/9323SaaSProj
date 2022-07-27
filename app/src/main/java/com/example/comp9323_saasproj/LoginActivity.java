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

import com.example.comp9323_saasproj.utilities.Constants;
import com.example.comp9323_saasproj.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        preferenceManager = new PreferenceManager(getApplicationContext());

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonLogin) {
                    userLogin();
                }

            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, UserTypeActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if(TextUtils.isEmpty( email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;
            //email is empty, stopping the function execution
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            return;
            //email is empty, stopping the function execution
        }
        //if the email and password are entered
        //showing the dialog
        progressDialog.setMessage("Login...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful( )) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Login Successfully\n Now you can edit your information", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
//                            preferenceManager.putString(Constants.KEY_USER_ID, user.getUid());
                            preferenceManager.putString(Constants.KEY_EMAIL, user.getEmail());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Login Failed\nPassword or Account Name is incrrect",Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }
    private void reload() { }
}

