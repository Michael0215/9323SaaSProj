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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ExpertRegisterActivity extends AppCompatActivity {

    ImageButton ivPhoto;
    EditText tvStuNumber,tvStuPwd,tvStuConfirmPwd;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_register);

        tvStuNumber = findViewById(R.id.et_username);
        tvStuPwd = findViewById(R.id.et_password);
        tvStuConfirmPwd = findViewById(R.id.et_confirm_password);

        ivPhoto = findViewById(R.id.iv_photo);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,1);
            }
        });


        Button btnCancel = findViewById(R.id.buttonCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Button register = findViewById(R.id.buttonRegister);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CheckInput()) {
                    registerUser();
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


//                            createUserStructure();


                            Toast.makeText(ExpertRegisterActivity.this, "Registered Successfully\nNow you can login", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                            // 加一个flag
                            Intent intent = new Intent(ExpertRegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(ExpertRegisterActivity.this, "Registered failed, try email format again", Toast.LENGTH_SHORT).show();
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