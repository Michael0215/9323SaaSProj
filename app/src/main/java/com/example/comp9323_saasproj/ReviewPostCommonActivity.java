package com.example.comp9323_saasproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
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

public class ReviewPostCommonActivity extends AppCompatActivity {

    // This activity is almost the same with ReviewPostActivity.
    /*
    Because students and staff are not able to comment, the only difference is that I deleted the comment button.
    Thus, there is no need to add comments record to database.
     */
    TextView title, category, email, description, blank;
    ListView lvReview;
    LinkedList<Review> allReviews = new LinkedList<>();
    Map<String, Object> comment = new HashMap<>();
    FirebaseFirestore firestoreDatabase;
    private FirebaseAuth firebaseAuth;
    int flag = 0;
    int createRefresh = 1;

    public void readFirebaseType(ReviewPostActivity.FirebaseCallback callback) {
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
                            Toast.makeText(ReviewPostCommonActivity.this, "Retrieving type failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void refreshFirebaseComments(ReviewPostActivity.FirebaseCallback callback, Bundle b) {
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
                                    Toast.makeText(ReviewPostCommonActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();

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
                                        review.setEmail(mapElement.getValue().toString());
                                    }
                                    if (mapElement.getKey().equals("postID")){
                                        review.setPostID(mapElement.getValue().toString());
                                    }
                                }
                                allReviews.add(review);
                            }
                            callback.onResponse(1);
                        } else {
                            Toast.makeText(ReviewPostCommonActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                            callback.onResponse(1);
                        }
                    }
                });
    }

    public interface FirebaseCallback {
        void onResponse(int flag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_post_common);
        lvReview = findViewById(R.id.list_comment);
        title = findViewById(R.id.et_title);
        category = findViewById(R.id.spn_type);
        email = findViewById(R.id.et_email);
        description = findViewById(R.id.et_description);
        description.setMovementMethod(ScrollingMovementMethod.getInstance());
        blank = findViewById(R.id.tv_no_comments_now);
        firestoreDatabase = FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        Bundle b = getIntent().getExtras();
        readFirebaseType(new ReviewPostActivity.FirebaseCallback() {
            @Override
            public void onResponse(int flag) {
            }
        });
        if(b != null) {
            title.setText(b.getString("title"));
            description.setText(b.getString("description"));
            category.setText(b.getString("category"));
            email.setText(b.getString("email"));
        }
        AppCompatImageView tvBack  = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(view -> onBackPressed());

        allReviews.clear();
        refreshFirebaseComments(new ReviewPostActivity.FirebaseCallback(){
            @Override
            public void onResponse(int flag) {
                ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());
                if(allReviews != null && !allReviews.isEmpty()){
                    blank.setVisibility(View.INVISIBLE);
                } else {
                    blank.setVisibility(View.VISIBLE);
                }
                adapter.setData(allReviews);
                lvReview.setAdapter(adapter);
            }
        }, b);

        // Refresh
        ImageButton tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allReviews.clear();
                refreshFirebaseComments(new ReviewPostActivity.FirebaseCallback(){
                    @Override
                    public void onResponse(int flag) {
                        ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());
                        if(allReviews != null && !allReviews.isEmpty()){
                            blank.setVisibility(View.INVISIBLE);
                        } else {
                            blank.setVisibility(View.VISIBLE);
                        }
                        adapter.setData(allReviews);
                        lvReview.setAdapter(adapter);
                    }
                }, b);
            }
        });
    }
}