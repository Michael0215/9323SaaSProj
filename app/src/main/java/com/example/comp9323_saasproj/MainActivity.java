package com.example.comp9323_saasproj;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.algolia.search.exceptions.AlgoliaRuntimeException;
import com.algolia.search.models.indexing.SearchResult;
import com.example.comp9323_saasproj.adapter.ReviewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.example.comp9323_saasproj.bean.Commodity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.example.comp9323_saasproj.adapter.AllCommodityAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Handler handler = null;

//    private Handler handler =  new Handler()
//    {
//        @Override
//        public  void handleMessage(Message msg)
//        {
//            if (msg.what == 0)
//            {
//                searchButton.setText( "completed");
//                adapter.setData(allCommodities);
//                lvAllCommodity.setAdapter(adapter);
//            }
//        }
//    };


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
        Button searchButton = findViewById(R.id.search_button);
        EditText search_bar = findViewById(R.id.search_bar);
        ImageButton IbAddProduct = findViewById(R.id.ib_add_product);
        handler=new Handler();

        IbAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCommodityActivity.class);
                startActivity(intent);
            }
        });

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
                Bundle bundle = new Bundle();
                bundle.putString("id", commodity.getId());
                bundle.putString("title",commodity.getTitle());
                bundle.putString("description",commodity.getDescription());
                bundle.putString("phone",commodity.getPhone());
                bundle.putString("category", commodity.getCategory());
                Intent intent = new Intent(MainActivity.this, ReviewCommodityActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread request = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        SearchClient client =
                                DefaultSearchClient.create("RPPCQB86AX", "9a7a77519d4ecd18b81452abdc74bc8e");
                        SearchIndex index = client.initIndex("posts");
                        com.algolia.search.models.indexing.Query query = new com.algolia.search.models.indexing.Query(search_bar.getText().toString())
                                .setAttributesToRetrieve(Arrays.asList("objectID", "Title", "Description", "E-mail", "Category"));
                        allCommodities.clear();
                        SearchResult res = index.search(query);
                        List hits = res.getHits();
                        for (int i = 0; i < hits.size(); i++){
                            Commodity commodity = new Commodity();
                            Map<String, Object> info =  (HashMap)hits.get(i);
                            for (Map.Entry<String, Object> mapElement : info.entrySet()){
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
                                if (mapElement.getKey().equals("objectID")){
                                    commodity.setId(mapElement.getValue().toString());
                                }
                            }
                            allCommodities.add(commodity);
                        }
                    }
                });
                request.start();
                while(request.isAlive()){}
                AllCommodityAdapter adapter = new AllCommodityAdapter(getApplicationContext());
                adapter.setData(allCommodities);
                lvAllCommodity.setAdapter(adapter);
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


