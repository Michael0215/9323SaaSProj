package com.example.comp9323_saasproj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.comp9323_saasproj.bean.Review;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.leaf.collegeidleapp.util.MyCollectionDbHelper;
//import com.leaf.collegeidleapp.util.ReviewDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 商品信息评论/留言类
 * @author autumn_leaf
 */
public class ReviewCommodityActivity extends AppCompatActivity {

    TextView id, title, category, phone, description;
//    ImageView ivCommodity;
    ListView lvReview;
    LinkedList<Review> reviews = new LinkedList<>();
    EditText etComment;
//    int position;
//    byte[] picture;
    FirebaseFirestore firestoreDatabase;
    //创建！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_commodity);
//        ivCommodity = findViewById(R.id.iv_commodity);
        title = findViewById(R.id.et_title);
        category = findViewById(R.id.spn_type);
        phone = findViewById(R.id.et_email);
        description = findViewById(R.id.et_description);
        firestoreDatabase = FirebaseFirestore.getInstance();

        Bundle b = getIntent().getExtras();
        if( b != null) {
//            picture = b.getByteArray("picture");
//            Bitmap img = BitmapFactory.decodeByteArray(picture, 0, picture.length);
//            ivCommodity.setImageBitmap(img);
            title.setText(b.getString("title"));
            description.setText(b.getString("description"));
            category.setText(b.getString("category"));
            System.out.println(category);
//            price.setText(String.valueOf(b.getFloat("price"))+"元");
            phone.setText(b.getString("phone"));
//            position = b.getInt("position");
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
//        etComment = findViewById(R.id.et_comment);
//        lvReview = findViewById(R.id.list_comment);
//        //提交评论点击事件
//        Button btnReview = findViewById(R.id.btn_submit);
//        btnReview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //先检查是否为空
//                if(CheckInput()) {
////                    ReviewDbHelper dbHelper = new ReviewDbHelper(getApplicationContext(),ReviewDbHelper.DB_NAME,null,1);
////                    Review review = new Review();
////                    review.setContent(etComment.getText().toString());
////                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
////                    //获取当前时间
////                    Date date = new Date(System.currentTimeMillis());
////                    review.setCurrentTime(simpleDateFormat.format(date));
////                    String stuId = getIntent().getStringExtra("stuId");
////                    review.setStuId(stuId);
////                    review.setPosition(position);
////                    dbHelper.addReview(review);
////                    //评论置为空
////                    etComment.setText("");
////                    Toast.makeText(getApplicationContext(),"评论成功!",Toast.LENGTH_SHORT).show();
//
//
//                    Map<String, Object> comment = new HashMap<>();
//                    comment.put("Title", etTitle.getText().toString());
//                    comment.put("Category", spType.getSelectedItem().toString());
//                    comment.put("E-mail", etPhone.getText().toString());
//                    comment.put("Description", etDescription.getText().toString());
//                }
//            }
//        });
//
//
//        final ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());
//        final ReviewDbHelper dbHelper = new ReviewDbHelper(getApplicationContext(),ReviewDbHelper.DB_NAME,null,1);
//        reviews = dbHelper.readReviews(position);
//        adapter.setData(reviews);
//        //设置适配器
//        lvReview.setAdapter(adapter);
//        //刷新页面
//        TextView tvRefresh = findViewById(R.id.tv_refresh);
//        tvRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                reviews = dbHelper.readReviews(position);
//                adapter.setData(reviews);
//                lvReview.setAdapter(adapter);
//            }
//        });
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
