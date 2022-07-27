package com.example.comp9323_saasproj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.comp9323_saasproj.R;
import com.example.comp9323_saasproj.bean.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllPostAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Post> posts = new ArrayList<>();
    HashMap<Integer,View> location = new HashMap<>();

    public AllPostAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(location.get(position) == null){
            convertView = layoutInflater.inflate(R.layout.item_container_post,null);
            Post post = (Post) getItem(position);
            holder = new ViewHolder(convertView,post);
            location.put(position,convertView);
            convertView.setTag(holder);
        }else{
            convertView = location.get(position);
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle,tvType,tvDescription,tvEmail;

        public ViewHolder(View itemView, Post post) {
            tvTitle = itemView.findViewById(R.id.tv_name);
            tvType = itemView.findViewById(R.id.tv_type);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvTitle.setText(post.getTitle());
            tvEmail.setText(post.getEmail());
            tvType.setText(post.getCategory());
        }
    }
}
