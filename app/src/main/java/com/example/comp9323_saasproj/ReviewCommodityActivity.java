package com.example.comp9323_saasproj;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp9323_saasproj.adapter.ReviewAdapter;
import com.example.comp9323_saasproj.bean.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ReviewCommodityActivity extends AppCompatActivity {

    TextView id, title, category, phone, description;
    ListView lvReview;
    LinkedList<Review> allReviews = new LinkedList<>();
    Map<String, Object> comment = new HashMap<>();
    EditText etComment;
    FirebaseFirestore firestoreDatabase;
    private FirebaseAuth firebaseAuth;
    int flag = 0;
    int createRefresh = 1;

    public void readFirebaseType(AddCommodityActivity.FirebaseCallback callback) {
        firestoreDatabase = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser cur_user = firebaseAuth.getCurrentUser();
        String email = cur_user.getEmail();
        firestoreDatabase.collection("users")
                .whereEqualTo("E-mail", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
                                    if (mapElement.getKey().equals("Type")){
                                        if(mapElement.getValue().toString().equals("expert")){
                                            flag = 1;
                                        }
                                        callback.onResponse(flag);
                                    }
                                }
                            }
                        }
                        else {
                            Toast.makeText(ReviewCommodityActivity.this, "Retrieving type failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void refreshFirebaseComments(AddCommodityActivity.FirebaseCallback callback, Bundle b) {
        allReviews.clear();
        CollectionReference comments = firestoreDatabase.collection("comments");
        Query query = comments.whereEqualTo("postID", b.getString("id")).orderBy("Time", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Review review = new Review();
                                if (createRefresh == 0){
                                    Toast.makeText(ReviewCommodityActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();
                                }
                                if (createRefresh == 1){
                                    createRefresh = 0;
                                }
                                for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
                                    if (mapElement.getKey().equals("Content")){
                                        review.setContent(mapElement.getValue().toString());
                                    }
                                    if (mapElement.getKey().equals("Time")){
                                        review.setCurrentTime(mapElement.getValue().toString());
                                    }
                                    if (mapElement.getKey().equals("E-mail")){
                                        review.setPhone(mapElement.getValue().toString());
                                    }
                                    if (mapElement.getKey().equals("postID")){
                                        review.setPostID(mapElement.getValue().toString());
                                    }
                                }
                                allReviews.add(review);
                            }
                            callback.onResponse(1);
                        } else {
                            Toast.makeText(ReviewCommodityActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                            callback.onResponse(1);
                        }
                    }
                });
    }

    public void submitFirebaseComments(AddCommodityActivity.FirebaseCallback callback,Map<String, Object> comment) {
        firestoreDatabase.collection("comments")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ReviewCommodityActivity.this, "Comment success!", Toast.LENGTH_SHORT).show();
                        callback.onResponse(1);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReviewCommodityActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();
                        callback.onResponse(1);
                    }
                });
    }

    public interface FirebaseCallback {
        void onResponse(int flag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_commodity);
        title = findViewById(R.id.et_title);
        category = findViewById(R.id.spn_type);
        phone = findViewById(R.id.et_email);
        description = findViewById(R.id.et_description);
        description.setMovementMethod(ScrollingMovementMethod.getInstance());
        firestoreDatabase = FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        Bundle b = getIntent().getExtras();
        readFirebaseType(new AddCommodityActivity.FirebaseCallback() {
            @Override
            public void onResponse(int flag) {
            }
        });
        if( b != null) {
            title.setText(b.getString("title"));
            description.setText(b.getString("description"));
            category.setText(b.getString("category"));
            phone.setText(b.getString("phone"));
        }
        AppCompatImageView tvBack  = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(view -> onBackPressed());

        allReviews.clear();
        refreshFirebaseComments(new AddCommodityActivity.FirebaseCallback(){
            @Override
            public void onResponse(int flag) {
                ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());
                adapter.setData(allReviews);
                lvReview.setAdapter(adapter);
            }
        }, b);

        //Submit
        etComment = findViewById(R.id.et_comment);
        lvReview = findViewById(R.id.list_comment);
        firestoreDatabase = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        lvReview = findViewById(R.id.list_comment);
        Button btnReview = findViewById(R.id.btn_submit);
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0){
                    Toast.makeText(ReviewCommodityActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(CheckInput()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    comment.clear();
                    comment.put("postID", b.getString("id"));
                    Date date = new Date(System.currentTimeMillis());
                    comment.put("Time", simpleDateFormat.format(date));
                    comment.put("Content", etComment.getText().toString());
                    comment.put("E-mail", firebaseAuth.getCurrentUser().getEmail());
                    submitFirebaseComments(new AddCommodityActivity.FirebaseCallback(){
                        @Override
                        public void onResponse(int flag) {
                        }
                    }, comment);
                }
            }
        });
        // Refresh
        ImageButton tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allReviews.clear();
                refreshFirebaseComments(new AddCommodityActivity.FirebaseCallback(){
                    @Override
                    public void onResponse(int flag) {
                        ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());
                        adapter.setData(allReviews);
                        lvReview.setAdapter(adapter);
                    }
                }, b);
            }
        });
    }

    public boolean CheckInput() {
        String comment = etComment.getText().toString();
        if (comment.trim().equals("")) {
            Toast.makeText(this,"your comment can't be blanked!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
