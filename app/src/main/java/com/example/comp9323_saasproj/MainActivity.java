package com.example.comp9323_saasproj;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.example.comp9323_saasproj.bean.Commodity;
//import com.example.comp9323_saasproj.util.CommodityDbHelper;

import android.text.TextUtils;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.comp9323_saasproj.databinding.ActivityMainBinding;
import com.example.comp9323_saasproj.adapter.AllCommodityAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    ListView lvAllCommodity;

    FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
//    private ActivityMainBinding binding;
//    CommodityDbHelper dbHelper;
    AllCommodityAdapter adapter;
    List<Commodity> allCommodities = new ArrayList<>();
//    Commodity commodity = new


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

                firebaseFirestore.collection("UNSWusers")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Toast.makeText(MainActivity.this, document.getId() + " => " + document.getData(), Toast.LENGTH_SHORT).show();
//                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }

                                } else {
                                    Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
//                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });



//                allCommodities = dbHelper.readAllCommodities();
//                adapter.setData(allCommodities);
//                lvAllCommodity.setAdapter(adapter);
            }
        });

    }

}