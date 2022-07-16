package com.example.comp9323_saasproj;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.example.comp9323_saasproj.bean.Commodity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.comp9323_saasproj.adapter.AllCommodityAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    ListView lvAllCommodity;
    FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    AllCommodityAdapter adapter;
    List<Commodity> allCommodities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvAllCommodity = findViewById(R.id.lv_all_commodity);
        adapter = new AllCommodityAdapter(getApplicationContext());
        lvAllCommodity.setAdapter(adapter);
        final Bundle bundle = this.getIntent().getExtras();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ImageButton tvRefresh = findViewById(R.id.tv_refresh);

        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCommodities.clear();
                CollectionReference posts = firebaseFirestore.collection("posts");
                Query query = posts.orderBy("Time", Query.Direction.DESCENDING);
                query.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Commodity commodity = new Commodity();
                                        Toast.makeText(MainActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();
                                        for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
                                            if (mapElement.getKey().equals("Category")){
                                                commodity.setCategory(mapElement.getValue().toString());
                                            }
                                            if (mapElement.getKey().equals("Description")){
                                                commodity.setDescription(mapElement.getValue().toString());
                                            }
                                            if (mapElement.getKey().equals("E-mail")){
                                                commodity.setPhone(mapElement.getValue().toString());
                                            }
                                            if (mapElement.getKey().equals("Title")){
                                                commodity.setTitle(mapElement.getValue().toString());
                                            }
                                        }
                                        commodity.setId(document.getId());
                                        allCommodities.add(commodity);
                                    }
                                    adapter.setData(allCommodities);
                                    lvAllCommodity.setAdapter(adapter);
                                } else {
                                    Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        lvAllCommodity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commodity commodity = (Commodity) lvAllCommodity.getAdapter().getItem(position);
                Bundle bundle1 = new Bundle();
                bundle1.putString("id", commodity.getId());
                bundle1.putString("title",commodity.getTitle());
                bundle1.putString("description",commodity.getDescription());
                bundle1.putString("phone",commodity.getPhone());
                bundle1.putString("category", commodity.getCategory());
                Intent intent = new Intent(MainActivity.this, ReviewCommodityActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

        ImageButton PersonalCenter = findViewById(R.id.ib_personal_center);
        PersonalCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PersonalCenterActivity.class);
                startActivity(intent);
            }
        });

        ImageButton LiveChat = findViewById(R.id.ib_home_page);
        LiveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserChatActivity.class);
                startActivity(intent);
            }
        });
    }
}


