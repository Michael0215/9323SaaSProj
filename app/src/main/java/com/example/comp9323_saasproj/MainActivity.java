package com.example.comp9323_saasproj;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;



import android.text.TextUtils;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.comp9323_saasproj.databinding.ActivityMainBinding;
import com.example.comp9323_saasproj.adapter.AllCommodityAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    ListView lvAllCommodity;


    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
//    private ActivityMainBinding binding;
    AllCommodityAdapter adapter;

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

    }

}