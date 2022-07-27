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
import com.example.comp9323_saasproj.bean.Post;

import android.view.View;

import com.example.comp9323_saasproj.adapter.AllPostAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private ListView lvAllPost;
    private FirebaseFirestore firebaseFirestore;
    private AllPostAdapter adapter;
    private List<Post> allPosts = new ArrayList<>();
    private @NonNull ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        if(!preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            preferenceManager.clear();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(binding.getRoot());
        setListeners();
        lvAllPost = findViewById(R.id.lv_all_post);
        adapter = new AllPostAdapter(getApplicationContext());
        lvAllPost.setAdapter(adapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        ImageButton tvRefresh = findViewById(R.id.tv_refresh);
        Button searchButton = findViewById(R.id.search_button);
        EditText search_bar = findViewById(R.id.search_bar);
        ImageButton IbAddPost = findViewById(R.id.ib_add_post);
        ImageButton ibPrevention = findViewById(R.id.ib_prevention);
        ImageButton ibCure = findViewById(R.id.ib_cure);
        ImageButton ibAnnouncements = findViewById(R.id.ib_announcements);

        allPosts.clear();
        CollectionReference posts = firebaseFirestore.collection("posts");
        Query query = posts.orderBy("Time", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = new Post();
                                for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
                                    if (mapElement.getKey().equals("Category")){
                                        post.setCategory(mapElement.getValue().toString());
                                    }
                                    if (mapElement.getKey().equals("Description")){
                                        post.setDescription(mapElement.getValue().toString());
                                    }
                                    if (mapElement.getKey().equals("E-mail")){
                                        post.setEmail(mapElement.getValue().toString());
                                    }
                                    if (mapElement.getKey().equals("Title")){
                                        post.setTitle(mapElement.getValue().toString());
                                    }
                                }
                                post.setId(document.getId());
                                allPosts.add(post);
                            }
                            adapter.setData(allPosts);
                            lvAllPost.setAdapter(adapter);
                        } else {
                            Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        IbAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser cur_user = firebaseAuth.getCurrentUser();
                String email = cur_user.getEmail();
                firebaseFirestore.collection("users")
                        .whereEqualTo("E-mail", email)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
                                            if (mapElement.getKey().equals("Type")){
                                                if(mapElement.getValue().toString().equals("Expert")){
                                                    Intent intent = new Intent(MainActivity.this, AddPostExpertActivity.class);
                                                    startActivity(intent);
                                                }else{
                                                    Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Retrieving type failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allPosts.clear();
                CollectionReference posts = firebaseFirestore.collection("posts");
                Query query = posts.orderBy("Time", Query.Direction.DESCENDING);
                query.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Post post = new Post();
                                        Toast.makeText(MainActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();
                                        for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
                                            if (mapElement.getKey().equals("Category")){
                                                post.setCategory(mapElement.getValue().toString());
                                            }
                                            if (mapElement.getKey().equals("Description")){
                                                post.setDescription(mapElement.getValue().toString());
                                            }
                                            if (mapElement.getKey().equals("E-mail")){
                                                post.setEmail(mapElement.getValue().toString());
                                            }
                                            if (mapElement.getKey().equals("Title")){
                                                post.setTitle(mapElement.getValue().toString());
                                            }
                                        }
                                        post.setId(document.getId());
                                        allPosts.add(post);
                                    }
                                    AllPostAdapter adapter = new AllPostAdapter(getApplicationContext());
                                    adapter.setData(allPosts);
                                    lvAllPost.setAdapter(adapter);
                                } else {
                                    Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        lvAllPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = (Post) lvAllPost.getAdapter().getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("id", post.getId());
                bundle.putString("title",post.getTitle());
                bundle.putString("description",post.getDescription());
                bundle.putString("email",post.getEmail());
                bundle.putString("category", post.getCategory());

                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser cur_user = firebaseAuth.getCurrentUser();
                String email = cur_user.getEmail();
                firebaseFirestore.collection("users")
                        .whereEqualTo("E-mail", email)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
                                            if (mapElement.getKey().equals("Type")){
                                                if(mapElement.getValue().toString().equals("Expert")){
                                                    Intent intent = new Intent(MainActivity.this, ReviewPostActivity.class);
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                }else{
                                                    Intent intent = new Intent(MainActivity.this, ReviewPostCommonActivity.class);
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Retrieving type failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
                        allPosts.clear();
                        SearchResult res = index.search(query);
                        List hits = res.getHits();
                        for (int i = 0; i < hits.size(); i++){
                            Post post = new Post();
                            Map<String, Object> info =  (HashMap)hits.get(i);
                            for (Map.Entry<String, Object> mapElement : info.entrySet()){
                                if (mapElement.getKey().equals("Category")){
                                    post.setCategory(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("Description")){
                                    post.setDescription(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("E-mail")){
                                    post.setEmail(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("Title")){
                                    post.setTitle(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("objectID")){
                                    post.setId(mapElement.getValue().toString());
                                }
                            }
                            allPosts.add(post);
                        }
                    }
                });
                request.start();
                while(request.isAlive()){}
                AllPostAdapter adapter = new AllPostAdapter(getApplicationContext());
                adapter.setData(allPosts);
                lvAllPost.setAdapter(adapter);
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
                        allPosts.clear();
                        SearchResult res = index.search(query);
                        List hits = res.getHits();
                        for (int i = 0; i < hits.size(); i++){
                            Post post = new Post();
                            Map<String, Object> info =  (HashMap)hits.get(i);
                            for (Map.Entry<String, Object> mapElement : info.entrySet()){
                                if (mapElement.getKey().equals("Category")){
                                    post.setCategory(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("Description")){
                                    post.setDescription(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("E-mail")){
                                    post.setEmail(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("Title")){
                                    post.setTitle(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("objectID")){
                                    post.setId(mapElement.getValue().toString());
                                }
                            }
                            allPosts.add(post);
                        }
                    }
                });
                request.start();
                while(request.isAlive()){}
                AllPostAdapter adapter = new AllPostAdapter(getApplicationContext());
                adapter.setData(allPosts);
                lvAllPost.setAdapter(adapter);
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

                        allPosts.clear();
                        SearchResult res = index.search(query);
                        List hits = res.getHits();
                        for (int i = 0; i < hits.size(); i++){
                            Post post = new Post();
                            Map<String, Object> info =  (HashMap)hits.get(i);
                            for (Map.Entry<String, Object> mapElement : info.entrySet()){
                                if (mapElement.getKey().equals("Category")){
                                    post.setCategory(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("Description")){
                                    post.setDescription(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("E-mail")){
                                    post.setEmail(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("Title")){
                                    post.setTitle(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("objectID")){
                                    post.setId(mapElement.getValue().toString());
                                }
                            }
                            allPosts.add(post);
                        }
                    }
                });
                request.start();
                while(request.isAlive()){}
                AllPostAdapter adapter = new AllPostAdapter(getApplicationContext());
                adapter.setData(allPosts);
                lvAllPost.setAdapter(adapter);
            }
        });

        ibAnnouncements.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread request = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        SearchClient client =
                                DefaultSearchClient.create("RPPCQB86AX", "c9a86b621611879d90642d4af7863937");
                        SearchIndex index = client.initIndex("posts");
                        com.algolia.search.models.indexing.Query query = new com.algolia.search.models.indexing.Query("Announcements")
                                .setAttributesToRetrieve(Arrays.asList("objectID", "Title", "Description", "E-mail", "Category"))
                                .setRestrictSearchableAttributes(Arrays.asList(
                                        "Category"
                                ));
                        allPosts.clear();
                        SearchResult res = index.search(query);
                        List hits = res.getHits();
                        for (int i = 0; i < hits.size(); i++){
                            Post post = new Post();
                            Map<String, Object> info =  (HashMap)hits.get(i);
                            for (Map.Entry<String, Object> mapElement : info.entrySet()){
                                if (mapElement.getKey().equals("Category")){
                                    post.setCategory(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("Description")){
                                    post.setDescription(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("E-mail")){
                                    post.setEmail(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("Title")){
                                    post.setTitle(mapElement.getValue().toString());
                                }
                                if (mapElement.getKey().equals("objectID")){
                                    post.setId(mapElement.getValue().toString());
                                }
                            }
                            allPosts.add(post);
                        }
                    }
                });
                request.start();
                while(request.isAlive()){}
                AllPostAdapter adapter = new AllPostAdapter(getApplicationContext());
                adapter.setData(allPosts);
                lvAllPost.setAdapter(adapter);
            }
        });

        ImageButton Account = findViewById(R.id.ib_account);
        Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setListeners(){
        binding.ibLiveChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
    }
}


