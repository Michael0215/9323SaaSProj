package com.example.comp9323_saasproj;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.algolia.search.models.indexing.SearchResult;
import com.example.comp9323_saasproj.databinding.ActivityMainBinding;
import com.example.comp9323_saasproj.utilities.Constants;
import com.example.comp9323_saasproj.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.comp9323_saasproj.bean.Commodity;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{


    ListView lvAllCommodity;
    FirebaseFirestore firebaseFirestore;
    AllCommodityAdapter adapter;
    List<Commodity> allCommodities = new ArrayList<>();
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if(!preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            preferenceManager.clear();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_main);
        lvAllCommodity = findViewById(R.id.lv_all_commodity);
        adapter = new AllCommodityAdapter(getApplicationContext());
        lvAllCommodity.setAdapter(adapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        ImageButton tvRefresh = findViewById(R.id.tv_refresh);
        Button searchButton = findViewById(R.id.search_button);
        EditText search_bar = findViewById(R.id.search_bar);
        ImageButton IbAddProduct = findViewById(R.id.ib_add_product);
        ImageButton ibPrevention = findViewById(R.id.ib_electric_product);
        ImageButton ibCure = findViewById(R.id.ib_daily_use);
        ImageButton ibNotices = findViewById(R.id.ib_sports_good);

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
//                                Toast.makeText(MainActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();
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
                                DefaultSearchClient.create("RPPCQB86AX", "c9a86b621611879d90642d4af7863937");
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

        ibPrevention.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread request = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        SearchClient client =
                                DefaultSearchClient.create("RPPCQB86AX", "c9a86b621611879d90642d4af7863937");
                        SearchIndex index = client.initIndex("posts");
                        com.algolia.search.models.indexing.Query query = new com.algolia.search.models.indexing.Query("Prevention")
                                .setAttributesToRetrieve(Arrays.asList("objectID", "Title", "Description", "E-mail", "Category"))
                                .setRestrictSearchableAttributes(Arrays.asList(
                                        "Category"
                                ));
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

        ibCure.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread request = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        SearchClient client =
                                DefaultSearchClient.create("RPPCQB86AX", "c9a86b621611879d90642d4af7863937");
                        SearchIndex index = client.initIndex("posts");
                        com.algolia.search.models.indexing.Query query = new com.algolia.search.models.indexing.Query("Cure")
                                .setAttributesToRetrieve(Arrays.asList("objectID", "Title", "Description", "E-mail", "Category"))
                                .setRestrictSearchableAttributes(Arrays.asList(
                                        "Category"
                                ));

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

        ibNotices.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread request = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        SearchClient client =
                                DefaultSearchClient.create("RPPCQB86AX", "c9a86b621611879d90642d4af7863937");
                        SearchIndex index = client.initIndex("posts");
                        com.algolia.search.models.indexing.Query query = new com.algolia.search.models.indexing.Query("Notices")
                                .setAttributesToRetrieve(Arrays.asList("objectID", "Title", "Description", "E-mail", "Category"))
                                .setRestrictSearchableAttributes(Arrays.asList(
                                        "Category"
                                ));
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
                finish();
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


