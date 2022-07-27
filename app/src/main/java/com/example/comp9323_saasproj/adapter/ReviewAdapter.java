package com.example.comp9323_saasproj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.comp9323_saasproj.R;
import com.example.comp9323_saasproj.bean.Review;

import java.util.HashMap;
import java.util.LinkedList;

public class ReviewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private LinkedList<Review> reviews = new LinkedList<>();
    HashMap<Integer,View> location = new HashMap<>();

    public ReviewAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(LinkedList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(location.get(position) == null) {
            convertView = layoutInflater.inflate(R.layout.item_container_comment,null);
            Review review = (Review) getItem(position);
            holder = new ViewHolder(convertView,review);
            location.put(position,convertView);
            convertView.setTag(holder);
        }else {
            convertView = location.get(position);
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tvEmail,tvTime,tvContent;

        public ViewHolder(View itemView, Review review) {
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_comment);
            tvEmail.setText(review.getEmail());
            tvTime.setText(review.getCurrentTime());
            tvContent.setText(review.getContent());
        }
    }
}

