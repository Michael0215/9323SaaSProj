package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.comp9323_saasproj.adapter.UsersAdapter;
import com.example.comp9323_saasproj.databinding.ActivityUserChatBinding;
import com.example.comp9323_saasproj.listeners.UserListener;
import com.example.comp9323_saasproj.models.User;
import com.example.comp9323_saasproj.utilities.Constants;
import com.example.comp9323_saasproj.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserChatActivity extends AppCompatActivity implements UserListener {

    private ActivityUserChatBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserEmail.equals(queryDocumentSnapshot.getString(Constants.KEY_EMAIL))){
                                continue;
                            }
                            User user = new User();
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.type = queryDocumentSnapshot.getString("Type");
                            users.add(user);
                        }
                        if(users.size() > 0){
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });

    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), LiveChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}