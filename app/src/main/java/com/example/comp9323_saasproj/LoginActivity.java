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
/* This class creates the activity for the login page, users can input their correct email
   and password to login their accounts. If users don't have the account, they can jump
   to the registration page */

//rewrite the interface in AppCompatActivity: onCreate(), onStart()
public class LoginActivity extends AppCompatActivity {
    // initialise the widgets created in activity_login.xml correspondingly
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private PreferenceManager preferenceManager;

    //create actions for each widget
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getApplicationContext());
        /* check the state of the account is signed-in or signed-out, if the account is in signed-in
           state and the user closes the app without the signed-out step, next time when he or she opens
           the app, it's still in someone's account */
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

        // listen for action of the 'sign in' button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonLogin) {
                    userLogin();
                }

            }
        });
        // listen for action of the textview 'Don't have and  account? Sign up here'
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // jump from LoginActivity.java to UserTypeActivity.java
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
        // error handling for the email and password
        if(TextUtils.isEmpty(email)){
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
        // using the API to compare the input of email & password in login page to the registered one in the firebase
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        // the input matches the result in firebase
                        if(task.isSuccessful( )) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Login Successfully\n Now you can edit your information", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
//                            preferenceManager.putString(Constants.KEY_USER_ID, user.getUid());
                            preferenceManager.putString(Constants.KEY_EMAIL, user.getEmail());
                            // jump from LoginActivity.java to MainActivity.java
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        // the input email and password are wrong
                        else{
                            Toast.makeText(LoginActivity.this,"Login Failed\nPassword or Account Name is incrrect",Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }
    private void reload() { }
}

