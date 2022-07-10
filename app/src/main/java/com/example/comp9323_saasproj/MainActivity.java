package com.example.comp9323_saasproj;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.example.comp9323_saasproj.bean.Commodity;

import android.view.View;

import com.example.comp9323_saasproj.adapter.AllCommodityAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    ListView lvAllCommodity;
    FirebaseFirestore firebaseFirestore;

    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    //    private ActivityMainBinding binding;
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
        final TextView tvStuNumber = findViewById(R.id.tv_student_number);
        String str = "";
        if (bundle != null) {
            str = "Welcome" + bundle.getString("username") + ", Hello!";
        }
        tvStuNumber.setText(str);
        final String stuNum = tvStuNumber.getText().toString();//substring(2, tvStuNumber.getText().length() - 4);
        ImageButton IbAddProduct = findViewById(R.id.ib_add_product);

        IbAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCommodityActivity.class);
                if (bundle != null) {
                    //获取学生学号
                    bundle.putString("user_id", stuNum);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        TextView tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCommodities.clear();
                firebaseFirestore.collection("UNSWusers")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
//                                    ArrayList<String> value = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Commodity commodity = new Commodity();
                                        Toast.makeText(MainActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();
                                        //set db id as commodity id
//                                        value.add((String)document.getId());
                                        for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
//                                            System.out.println("key= " + mapElement.getKey() + " and value= " + mapElement.getValue());
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
//                                            value.add((String)mapElement.getValue().toString());
                                        }

                                        commodity.setId(document.getId());
//                                        commodity.setId(value.get(0));
//                                        commodity.setCategory(value.get(1));
//                                        commodity.setDescription(value.get(2));
//                                        commodity.setPhone(value.get(3));
//                                        commodity.setTitle(value.get(4));
                                        allCommodities.add(commodity);


//                                        value.clear();
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
//                bundle1.putByteArray("picture",commodity.getPicture());
                bundle1.putString("title",commodity.getTitle());
                bundle1.putString("description",commodity.getDescription());
//                bundle1.putFloat("price",commodity.getPrice());
                bundle1.putString("phone",commodity.getPhone());
//                bundle1.putString("stuId",stuNum);
                bundle1.putString("category", commodity.getCategory());
                Intent intent = new Intent(MainActivity.this, ReviewCommodityActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
    }
}