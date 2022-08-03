package com.example.comp9323_saasproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import java.text.SimpleDateFormat;
import java.util.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AddPostActivity extends AppCompatActivity {

    // The class for inputting the content of a new post and submitting it.

    // Initialize widgets used in onCreate method.
    private EditText etTitle,etDescription;
    private TextView etEmail;
    private Spinner spType;
    private Button btnPublish;
    private FirebaseFirestore firestoreDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // Connect objects variables with widgets in the .xml file.
        AppCompatImageView btnBack = findViewById(R.id.btn_back);
        etEmail = findViewById(R.id.tv_email);
        firebaseAuth = FirebaseAuth.getInstance();
        // Get current user from FirebaseAuth database.
        FirebaseUser cur_user = firebaseAuth.getCurrentUser();
        etEmail.setText(cur_user.getEmail());
        btnBack.setOnClickListener(v -> onBackPressed());
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        spType = findViewById(R.id.spn_type);
        btnPublish = findViewById(R.id.btn_publish);

        // Set what happens when publish button is pressed.
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestoreDatabase = FirebaseFirestore.getInstance();
                FirebaseUser cur_user = firebaseAuth.getCurrentUser();
                if(CheckInput()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    // Initialize a new map to store info of this post.
                    Map<String, Object> post = new HashMap<>();
                    post.put("Title", etTitle.getText().toString());
                    post.put("Category", spType.getSelectedItem().toString());
                    post.put("E-mail", cur_user.getEmail());
                    post.put("Description", etDescription.getText().toString());
                    post.put("Time", simpleDateFormat.format(date));
                    // Push this record of post to Firestore database.
                    firestoreDatabase.collection("posts")
                            .add(post)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    // Go back to home page.
                                    Toast.makeText(AddPostActivity.this, "Post success!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Go back to home page.
                                    Toast.makeText(AddPostActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    // Check each row of input. If any one of them is empty, then you cannot publish.
    public boolean CheckInput() {
        String title = etTitle.getText().toString();
        String type = spType.getSelectedItem().toString();
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