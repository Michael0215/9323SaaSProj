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

public class ReviewPostActivity extends AppCompatActivity {

    // This activity determines the way you review a post (and comments).

    // Define widgets, linked list, hash map and database objects.
    private TextView title, category, email, description, blank;
    private ListView lvReview;
    private LinkedList<Review> allReviews = new LinkedList<>();
    private Map<String, Object> comment = new HashMap<>();
    private EditText etComment;
    private FirebaseFirestore firestoreDatabase;
    private FirebaseAuth firebaseAuth;
    // Use this flag to refresh comments automatically after entering a post.
    int createRefresh = 1;

    // Firestore is an asynchronous database.
    /*
    If you try to give a value of a variable inside the API function to a global variable, and then use the global one
    outside this function, you will probably have a null pointer. Because you haven't actually get this value from the
    database yet even though the order of codes seems correct.
     */
    /*
    To solve this problem, one way is to define a callback interface. And use it onResponse function inside the API function.
    As you see, I packaged codes related to requesting database in a function with a parameter of callback.
    And add callback.onResponse(1); at the end of this function to ensure the request has already been done.
     */

    public void refreshFirebaseComments(FirebaseCallback callback, Bundle b) {
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
                                    Toast.makeText(ReviewPostActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ReviewPostActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                            callback.onResponse(1);
                        }
                    }
                });
    }

    public void submitFirebaseComments(FirebaseCallback callback, Map<String, Object> comment) {
        firestoreDatabase.collection("comments")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ReviewPostActivity.this, "Comment success!", Toast.LENGTH_SHORT).show();
                        callback.onResponse(1);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReviewPostActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();
                        callback.onResponse(1);
                    }
                });
    }

    // Define the interface of callback.
    public interface FirebaseCallback {
        void onResponse(int flag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_post);

        // Get widget in .xml file.
        title = findViewById(R.id.et_title);
        category = findViewById(R.id.spn_type);
        email = findViewById(R.id.et_email);
        description = findViewById(R.id.et_description);
        description.setMovementMethod(ScrollingMovementMethod.getInstance());
        blank = findViewById(R.id.tv_no_comments_now);
        firestoreDatabase = FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        Bundle b = getIntent().getExtras();

        // Receive info transferred from previous page.
        if(b != null) {
            title.setText(b.getString("title"));
            description.setText(b.getString("description"));
            category.setText(b.getString("category"));
            email.setText(b.getString("email"));
        }
        AppCompatImageView tvBack  = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(view -> onBackPressed());

        // Clear this list before refreshing and get ready to store a new list of comments.
        allReviews.clear();
        refreshFirebaseComments(new FirebaseCallback(){
            @Override
            public void onResponse(int flag) {
                ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());
                // If list is not empty, then set 'NO COMMENTS' text to be invisible.
                if(allReviews != null && !allReviews.isEmpty()){
                    blank.setVisibility(View.INVISIBLE);
                } else {
                    blank.setVisibility(View.VISIBLE);
                }
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
                if(CheckInput()) {
                    // Store values of attributes of a comments in a map.
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    comment.clear();
                    comment.put("postID", b.getString("id"));
                    Date date = new Date(System.currentTimeMillis());
                    comment.put("Time", simpleDateFormat.format(date));
                    comment.put("Content", etComment.getText().toString());
                    comment.put("E-mail", firebaseAuth.getCurrentUser().getEmail());
                    // Submit by using the packaged function with a callback.
                    submitFirebaseComments(new FirebaseCallback(){
                        @Override
                        public void onResponse(int flag) {
                        }
                    }, comment);
                    // Automatic refresh after submitting.
                    allReviews.clear();
                    refreshFirebaseComments(new FirebaseCallback(){
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
            }
        });
        // Manual refresh.
        ImageButton tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allReviews.clear();
                refreshFirebaseComments(new FirebaseCallback(){
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

    public boolean CheckInput() {
        String comment = etComment.getText().toString();
        if (comment.trim().equals("")) {
            Toast.makeText(this,"your comment can't be blanked!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
