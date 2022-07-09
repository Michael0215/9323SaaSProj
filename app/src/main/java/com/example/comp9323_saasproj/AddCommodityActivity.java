package com.example.comp9323_saasproj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.util.*;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.comp9323_saasproj.bean.Commodity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;





import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;







import java.io.ByteArrayOutputStream;

public class AddCommodityActivity extends AppCompatActivity {

    ListView lvAllCommodity;
    EditText etTitle,etPhone,etDescription;
    Spinner spType;
    Button btnPublish;
    FirebaseFirestore firestoreDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commodity);
        Button btnBack = findViewById(R.id.btn_back);
        //返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etTitle = findViewById(R.id.et_title);
        etPhone = findViewById(R.id.et_email);
        etDescription = findViewById(R.id.et_description);
        spType = findViewById(R.id.spn_type);
        btnPublish = findViewById(R.id.btn_publish);
        firestoreDatabase = FirebaseFirestore.getInstance();
        //发布按钮点击事件
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先检查合法性
                if(CheckInput()) {
                    // Create a new user with a first and last name
                    Map<String, Object> user = new HashMap<>();
                    user.put("Title", etTitle.getText().toString());
                    user.put("Category", spType.getSelectedItem().toString());
                    user.put("E-mail", etPhone.getText().toString());
                    user.put("Description", etDescription.getText().toString());

                    // Add a new document with a generated ID
                    firestoreDatabase.collection("UNSWusers")
                            .add(user)
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
                else{
                    Toast.makeText(AddCommodityActivity.this, "Please add some data.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this,"商品标题不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (type.trim().equals("请选择类别")) {
            Toast.makeText(this,"商品类别未选择!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.trim().equals("")) {
            Toast.makeText(this,"手机号码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.trim().equals("")) {
            Toast.makeText(this,"商品描述不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}