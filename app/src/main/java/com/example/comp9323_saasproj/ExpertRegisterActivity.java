package com.example.comp9323_saasproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
/* This class creates the activity of the registration page for EXPERT, expert can input their email address,
   satisfactory password ,confirmed password and the certificate in image format, after click on sign up, their
   accounts' information will be stored in firebase and jump back to the login page */

//rewrite the interface in AppCompatActivity: onCreate()
public class ExpertRegisterActivity extends AppCompatActivity {
    // initialise the widgets created in activity_expert_register.xml correspondingly
    private ImageButton ivPhoto;
    private EditText tvUsername,tvPassword,tvConfirmPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    //create actions for each widget
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_register);

        tvUsername = findViewById(R.id.et_username);
        tvPassword = findViewById(R.id.et_password);
        tvConfirmPassword = findViewById(R.id.et_confirm_password);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        ivPhoto = findViewById(R.id.iv_photo);
        // listen for action of the imagebutton of the certificate
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,null);
                //convert the image type to the type can be stored
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,1);
            }
        });

        ImageView btnCancel = findViewById(R.id.buttonCancel);
        // listen for action of the imageview 'return/cancel'
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // return to the previous page to choose the user type
                finish();
            }
        });

        TextView BackToLogin = findViewById(R.id.BackToLogin);
        // listen for action of the textview 'Already have a account? Sign in here'
        BackToLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.BackToLogin) {
                    // jump from ExpertRegisterActivity.java to LoginActivity.java
                    Intent intent = new Intent(ExpertRegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button register = findViewById(R.id.buttonRegister);
        // listen for action of the 'SIGN UP' button
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //if the input format of the created email address, password, and confirmed password are satisfactory
                if(CheckInput()) {
                    // register the account information to the firebase
                    registerUser();
                }

            }
        });
    }

    private void registerUser(){
        FirebaseFirestore firestoreDatabase;
        firestoreDatabase = FirebaseFirestore.getInstance();
        String email = tvUsername.getText().toString().trim();
        String password= tvPassword.getText().toString().trim();
        // store the account information into the USer hashMap
        Map<String, Object> user = new HashMap<>();
        user.put("E-mail", email);
        user.put("Password", password);
        user.put("Type", "Expert");

        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        //using the API to store the input information into firebase
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // table 'users' in firebase to store the information in the hashMap 'user'
                            firestoreDatabase.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(ExpertRegisterActivity.this, "Registered Successfully!\nNow you can login.", Toast.LENGTH_SHORT).show();
                                            progressDialog.cancel();
                                            // jump from ExpertRegisterActivity.java to LoginActivity.java
                                            Intent intent = new Intent(ExpertRegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    }) // error handling if the registration is failed
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ExpertRegisterActivity.this, "Registered failed, try email format again", Toast.LENGTH_SHORT).show();
                                            progressDialog.cancel();
                                        }
                                    });
                        }else{ // error handling if the registration is failed
                            Toast.makeText(ExpertRegisterActivity.this, "Registered failed, try email format again", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }

    // set rules for created email address, password and confirmed password
    public boolean CheckInput() {
        String username = tvUsername.getText().toString();
        String password = tvPassword.getText().toString();
        String confirm_password = tvConfirmPassword.getText().toString();
        if(username.trim().equals("")) {
            Toast.makeText(ExpertRegisterActivity.this,"User name cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.trim().equals("")) {
            Toast.makeText(ExpertRegisterActivity.this,"Password cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirm_password.trim().equals("")) {
            Toast.makeText(ExpertRegisterActivity.this,"Confirmed password cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.trim().equals(confirm_password.trim())) {
            Toast.makeText(ExpertRegisterActivity.this,"Password input is inconsistent!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length()<6){
            Toast.makeText(ExpertRegisterActivity.this,"Please create a stronger password, your password should be at least 6 characters!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}