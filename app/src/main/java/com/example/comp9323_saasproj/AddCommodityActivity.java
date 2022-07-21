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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.example.comp9323_saasproj.bean.Commodity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

public class AddCommodityActivity extends AppCompatActivity {

    ListView lvAllCommodity;
    EditText etTitle,etDescription;
    TextView etPhone;
    Spinner spType;
    Button btnPublish;
    FirebaseFirestore firestoreDatabase;
    private FirebaseAuth firebaseAuth;
    int flag = 0;

    public void readFirebaseType(FirebaseCallback callback) {
        firestoreDatabase = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser cur_user = firebaseAuth.getCurrentUser();
        String email = cur_user.getEmail();
        firestoreDatabase.collection("users")
                .whereEqualTo("E-mail", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
                                    if (mapElement.getKey().equals("Type")){
                                        if(mapElement.getValue().toString().equals("expert")){
                                            flag = 1;
                                        }
                                        callback.onResponse(flag);
                                    }
                                }
                            }
                        }
                        else {
                            Toast.makeText(AddCommodityActivity.this, "Retrieving type failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public interface FirebaseCallback {
        void onResponse(int flag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commodity);
        AppCompatImageView btnBack = findViewById(R.id.btn_back);
        etPhone = findViewById(R.id.tv_email);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser cur_user = firebaseAuth.getCurrentUser();
        etPhone.setText(cur_user.getEmail());
        readFirebaseType(new FirebaseCallback() {
            @Override
            public void onResponse(int flag) {
            }
        });

        btnBack.setOnClickListener(v -> onBackPressed());
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        spType = findViewById(R.id.spn_type);
        btnPublish = findViewById(R.id.btn_publish);

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1){
                    Toast.makeText(AddCommodityActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser cur_user = firebaseAuth.getCurrentUser();
                SearchClient client =
                        DefaultSearchClient.create("RPPCQB86AX", "9a7a77519d4ecd18b81452abdc74bc8e");
                if(CheckInput()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    Map<String, Object> post = new HashMap<>();
                    post.put("Title", etTitle.getText().toString());
                    post.put("Category", spType.getSelectedItem().toString());
                    post.put("E-mail", cur_user.getEmail());
                    post.put("Description", etDescription.getText().toString());
                    post.put("Time", simpleDateFormat.format(date));
                    firestoreDatabase.collection("posts")
                            .add(post)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddCommodityActivity.this, "Post success!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddCommodityActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddCommodityActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    public boolean CheckInput() {
        String title = etTitle.getText().toString();
        String type = spType.getSelectedItem().toString();
        String phone = etPhone.getText().toString();
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