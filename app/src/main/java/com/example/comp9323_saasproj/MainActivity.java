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
                                    ArrayList<String> value = new ArrayList<>();

                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Commodity commodity = new Commodity();

//                                        System.out.println("id type:"+document.getId().getClass().getSimpleName());
                                        Toast.makeText(MainActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();
                                        //set db id as commodity id
                                        value.add((String)document.getId());
                                        for (Map.Entry mapElement : document.getData().entrySet()){
                                            value.add((String)mapElement.getValue().toString());
                                        }
//                                        value.remove(1);

//                                        System.out.println(value.size());
                                        commodity.setId(value.get(0));
                                        commodity.setCategory(value.get(1));
                                        commodity.setDescription(value.get(2));
                                        commodity.setPhone(value.get(3));
                                        commodity.setTitle(value.get(4));
                                        allCommodities.add(commodity);


//                                        System.out.println("item:"+value);
//                                        System.out.println(value.get(0).getClass().getSimpleName()+" "+value.get(1).getClass().getSimpleName()+" "+value.get(2).getClass().getSimpleName()+" "+value.get(3).getClass().getSimpleName()+" "+value.get(4).getClass().getSimpleName()+" "+value.get(5).getClass().getSimpleName());
                                        value.clear();

//                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                    adapter.setData(allCommodities);
                                    lvAllCommodity.setAdapter(adapter);

//                                    System.out.println("sssssssssss: "+ allCommodities);
                                } else {
                                    Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
//                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });



            }
        });

    }

}