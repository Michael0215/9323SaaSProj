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

//    ImageButton ivPhoto;
    EditText etTitle,etPhone,etDescription;
    Spinner spType;
    Button btnPublish;

    FirebaseFirestore firestoreDatabase;
//    FirebaseDatabase firebaseDatabase;
//    DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commodity);


        //取出学号
//        tvStuId = findViewById(R.id.tv_student_id);
//        tvStuId.setText(this.getIntent().getStringExtra("user_id"));
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


//        databaseReference = firebaseDatabase.getReference("UserInfo");

        //发布按钮点击事件
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先检查合法性
                if(CheckInput()) {
//                    CommodityDbHelper dbHelper = new CommodityDbHelper(getApplicationContext(), CommodityDbHelper.DB_NAME, null, 1);


                    Commodity commodity = new Commodity();


                    commodity.setTitle(etTitle.getText().toString());
                    commodity.setCategory(spType.getSelectedItem().toString());
                    commodity.setPhone(etPhone.getText().toString());
                    commodity.setDescription(etDescription.getText().toString());

                    // Create a new user with a first and last name
                    Map<String, Object> user = new HashMap<>();
                    user.put("Title", commodity.getTitle().toString());
//                    user.put("Category", spType.getSelectedItem().toString());

                    user.put("Category", commodity.getCategory().toString());
                    user.put("Contact Number", commodity.getPhone().toString());
                    user.put("Description", commodity.getDescription().toString());

// Add a new document with a generated ID
                    firestoreDatabase.collection("Commodity test")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddCommodityActivity.this, "Post Success!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddCommodityActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddCommodityActivity.this, "Post failed try again!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    Toast.makeText(AddCommodityActivity.this, "Please add some data.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            //从相册返回的数据
//            if (data != null) {
//                //得到图片的全路径
//                Uri uri = data.getData();
//                ivPhoto.setImageURI(uri);
//            }
//        }
//    }

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