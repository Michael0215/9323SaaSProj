package com.example.comp9323_saasproj.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp9323_saasproj.databinding.ItemContainerUserBinding;
import com.example.comp9323_saasproj.listeners.UserListener;
import com.example.comp9323_saasproj.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    // Adapter for the displaying E-mail and type of a user in 'Select user' page.
    private final List<User> users;
    private final UserListener userListener;

    // Constructor
    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    // Create view for this page.
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    // Set data to fill containers with real values.
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Extend basic ViewHolder, overwrite setData function to set what we want.
    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user){
            binding.textEmail.setText(user.email);
            binding.textType.setText(user.type);
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }
}
