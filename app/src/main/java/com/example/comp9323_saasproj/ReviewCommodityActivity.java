package com.example.comp9323_saasproj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp9323_saasproj.adapter.ReviewAdapter;
//import com.leaf.collegeidleapp.bean.Collection;
import com.example.comp9323_saasproj.bean.Commodity;
import com.example.comp9323_saasproj.bean.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.leaf.collegeidleapp.util.MyCollectionDbHelper;
//import com.leaf.collegeidleapp.util.ReviewDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 商品信息评论/留言类
 * @author autumn_leaf
 */
public class ReviewCommodityActivity extends AppCompatActivity {

    TextView id, title, category, phone, description;
    ListView lvReview;
    LinkedList<Review> allReviews = new LinkedList<>();
    EditText etComment;
    FirebaseFirestore firestoreDatabase;
    private FirebaseAuth firebaseAuth;

    //创建！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
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
        if( b != null) {
            title.setText(b.getString("title"));
            description.setText(b.getString("description"));
            category.setText(b.getString("category"));
            System.out.println(category);
            phone.setText(b.getString("phone"));
        }
        //返回
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        //提交！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
        etComment = findViewById(R.id.et_comment);
        lvReview = findViewById(R.id.list_comment);
        firestoreDatabase = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        lvReview = findViewById(R.id.list_comment);
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //提交评论点击事件
        Button btnReview = findViewById(R.id.btn_submit);
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先检查是否为空
                if(CheckInput()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Map<String, Object> comment = new HashMap<>();
                    comment.put("postID", b.getString("id"));
                    Date date = new Date(System.currentTimeMillis());
//                    comment.put("E-mail",)
                    comment.put("Time", simpleDateFormat.format(date));
                    comment.put("Content", etComment.getText().toString());
                    comment.put("E-mail", firebaseAuth.getCurrentUser().getEmail());

                    firestoreDatabase.collection("comments")
                            .add(comment)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(ReviewCommodityActivity.this, "Comment success!", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(ReviewCommodityActivity.this, MainActivity.class);
//                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ReviewCommodityActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

        //刷新！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
        final ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());
        TextView tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allReviews.clear();
                firestoreDatabase.collection("comments")
                        .whereEqualTo("postID", b.getString("id"))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
//                                    ArrayList<String> value = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Review review = new Review();
                                        Toast.makeText(ReviewCommodityActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();
                                        //set db id as commodity id
//                                        value.add((String)document.getId());
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
//                                            value.add((String)mapElement.getValue().toString());
                                        }
//                                        review.setPhone(value.get(1));
//                                        review.setContent(value.get(2));
//                                        review.setCurrentTime(value.get(3));
//                                        review.setPostID(value.get(4));
//                                        System.out.println(value.get(1));
//                                        System.out.println(value.get(2));
//                                        System.out.println(value.get(3));
//                                        System.out.println(value.get(4));
                                        allReviews.add(review);
//                                        value.clear();
                                    }
//                                    System.out.println("sssssssssssssssss"+allReviews);
                                    adapter.setData(allReviews);
                                    lvReview.setAdapter(adapter);
                                } else {
                                    Toast.makeText(ReviewCommodityActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    /**
     * 检查输入评论是否为空
     * @return true
     */
    public boolean CheckInput() {
        String comment = etComment.getText().toString();
        if (comment.trim().equals("")) {
            Toast.makeText(this,"评论内容不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
