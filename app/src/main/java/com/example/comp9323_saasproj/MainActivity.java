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
/* This class creates the activity for the main page of each account, users can view all the posts after click the
   refresh image button, or view the corresponding posts by the categorised image button: Prevention, Cure, and Announcements.
   Each post can be clicked in to view the content. User can input in the search bar and press go to view the searching results.
   Users can click on Chat to send massage to another user. Users can click on Q&A to send posts base on the account's type.
   User can also view the account information clicking on Account and do the diagnosed test in Self-Diagnosis */

//rewrite the interface in AppCompatActivity: onCreate()
public class MainActivity extends AppCompatActivity{
    // initialise the widgets created in activity_main.xml correspondingly
    private ListView lvAllPost;
    private FirebaseFirestore firebaseFirestore;
    private AllPostAdapter adapter;
    private List<Post> allPosts = new ArrayList<>();
    private @NonNull ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;

    //create actions for each widget
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // if the state of the account is signed-out, stay in login page
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
        adapter = new AllPostAdapter(getApplicationContext());// create new the object adapter
        lvAllPost.setAdapter(adapter);// set value for lnAllPost
        firebaseFirestore = FirebaseFirestore.getInstance();
        ImageButton tvRefresh = findViewById(R.id.tv_refresh);
        Button searchButton = findViewById(R.id.search_button);
        EditText search_bar = findViewById(R.id.search_bar);
        ImageButton IbAddPost = findViewById(R.id.ib_add_post);
        ImageButton ibPrevention = findViewById(R.id.ib_prevention);
        ImageButton ibCure = findViewById(R.id.ib_cure);
        ImageButton ibAnnouncements = findViewById(R.id.ib_announcements);

        //clear all posts in the listview
        allPosts.clear();
        //read the information of the corresponding post in the table 'posts' in the firestore
        CollectionReference posts = firebaseFirestore.collection("posts");
        //order all posts from new to old
        Query query = posts.orderBy("Time", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // scan all the posts in the table 'posts' in firestore by QueryDocumentSnapshot
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = new Post();
                                for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
                                    // read the information in mapElement and set value for the object post
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
                                // append each object 'post' to the arrayList allPost, now all the information of created posts stored in 'allPosts'
                                allPosts.add(post);
                            }
                            // pass the object in allPost to the adapter, read the data and convert to lvAllPost
                            adapter.setData(allPosts);
                            lvAllPost.setAdapter(adapter);
                        } else { // if can't get the data of the table 'posts' from the firestore
                            Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // listen for action of the imagebutton of 'Q&A'
        IbAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser cur_user = firebaseAuth.getCurrentUser();
                String email = cur_user.getEmail();
                // matches the account type stored in firestore if the user is going to post something
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
                                                // account type is expert
                                                if(mapElement.getValue().toString().equals("Expert")){
                                                    // jump from MainActivity.java to AddPostExpertActivity.java
                                                    Intent intent = new Intent(MainActivity.this, AddPostExpertActivity.class);
                                                    startActivity(intent);
                                                }else{ // account type is common
                                                    // jump from MainActivity.java to AddPostActivity.java
                                                    Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    }
                                }
                                else { // retrieve the account type failed in firestore
                                    Toast.makeText(MainActivity.this, "Retrieving type failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // listen for action of the imagebutton of 'refresh'
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear all posts in the listview
                allPosts.clear();
                // retrieve all the post in the firestore's table 'posts'
                CollectionReference posts = firebaseFirestore.collection("posts");
                // order the post in creating time order
                Query query = posts.orderBy("Time", Query.Direction.DESCENDING);
                query.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // retrieve all posts in the 'posts' table
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
                                } else { // error handling
                                    Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // listen for action of the listview when clicking on the post
        lvAllPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = (Post) lvAllPost.getAdapter().getItem(position);
                // read and collect the variable stored in the bundle by another java class
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
                //check the email stored in table 'users' in the firebase
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
                                                // account type is expert
                                                if(mapElement.getValue().toString().equals("Expert")){
                                                    // jump from MainActivity.java to ReviewPostActivity.java (expert)
                                                    Intent intent = new Intent(MainActivity.this, ReviewPostActivity.class);
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                }else{ // account type is common
                                                    // jump from MainActivity.java to ReviewPostCommonActivity.java (common)
                                                    Intent intent = new Intent(MainActivity.this, ReviewPostCommonActivity.class);
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    }
                                }
                                else { // error handling
                                    Toast.makeText(MainActivity.this, "Retrieving type failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // listen for action of the button of 'go'
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread request = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        // use the Algolia's API to retrieve the copied data from the firestore
                        SearchClient client =
                                DefaultSearchClient.create("RPPCQB86AX", "c9a86b621611879d90642d4af7863937");
                        // matching the data belong to 'posts'
                        SearchIndex index = client.initIndex("posts");
                        // search the query from "objectID", "Title", "Description", "E-mail", "Category"
                        com.algolia.search.models.indexing.Query query = new com.algolia.search.models.indexing.Query(search_bar.getText().toString())
                                .setAttributesToRetrieve(Arrays.asList("objectID", "Title", "Description", "E-mail", "Category"));
                        //clear all posts in the listview
                        allPosts.clear();
                        SearchResult res = index.search(query);
                        // store all retrieved data of posts from Algolia into the list 'hits'
                        List hits = res.getHits();
                        // pass the information stored in 'hits' to the object post''
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
                            // store all retrieved posts in 'allPosts'
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

        // listen for action of the imagebutton of 'Prevention'
        ibPrevention.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread request = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        SearchClient client =
                                DefaultSearchClient.create("RPPCQB86AX", "c9a86b621611879d90642d4af7863937");
                        SearchIndex index = client.initIndex("posts");
                        // only retrieve the post which category is 'Prevention'
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

    // listen for action of the imagebutton of 'Cure'
        ibCure.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread request = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        SearchClient client =
                                DefaultSearchClient.create("RPPCQB86AX", "c9a86b621611879d90642d4af7863937");
                        SearchIndex index = client.initIndex("posts");
                        // only retrieve the post which category is 'Cure'
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

        // listen for action of the imagebutton of 'Announcements'
        ibAnnouncements.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread request = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        SearchClient client =
                                DefaultSearchClient.create("RPPCQB86AX", "c9a86b621611879d90642d4af7863937");
                        SearchIndex index = client.initIndex("posts");
                        // only retrieve the post which category is 'Announcements'
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
        // listen for action of the imagebutton of 'Account'
        Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // jump from MainActivity.java to AccountActivity.java
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setListeners(){
        // jump to the live chat page
        binding.ibLiveChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
    }
}


