package com.example.comp9323_saasproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPostExpertActivity extends AppCompatActivity {

    private EditText etTitle,etDescription;
    private TextView etEmail, etType;
    private Button btnPublish;
    private FirebaseFirestore firestoreDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_expert);
        AppCompatImageView btnBack = findViewById(R.id.btn_back);
        etEmail = findViewById(R.id.tv_email);
        etType = findViewById(R.id.spn_type);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser cur_user = firebaseAuth.getCurrentUser();
        etEmail.setText(cur_user.getEmail());
        btnBack.setOnClickListener(v -> onBackPressed());
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        btnPublish = findViewById(R.id.btn_publish);

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestoreDatabase = FirebaseFirestore.getInstance();
                FirebaseUser cur_user = firebaseAuth.getCurrentUser();
                if(CheckInput()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    Map<String, Object> post = new HashMap<>();
                    post.put("Title", etTitle.getText().toString());
                    post.put("Category", etType.getText().toString());
                    post.put("E-mail", cur_user.getEmail());
                    post.put("Description", etDescription.getText().toString());
                    post.put("Time", simpleDateFormat.format(date));
                    firestoreDatabase.collection("posts")
                            .add(post)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddPostExpertActivity.this, "Post success!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddPostExpertActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddPostExpertActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    public boolean CheckInput() {
        String title = etTitle.getText().toString();
        String type = etType.getText().toString();
        String description = etDescription.getText().toString();
        if (title.trim().equals("")) {
            Toast.makeText(this,"Title can not be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (type.trim().equals("")) {
            Toast.makeText(this,"Please select a category!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.trim().equals("")) {
            Toast.makeText(this,"Description can not be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}