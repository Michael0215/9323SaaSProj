package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.comp9323_saasproj.databinding.ActivityLiveChatBinding;
import com.example.comp9323_saasproj.databinding.ActivityUserChatBinding;
import com.example.comp9323_saasproj.models.User;
import com.example.comp9323_saasproj.utilities.Constants;

public class LiveChatActivity extends AppCompatActivity {

    private ActivityLiveChatBinding binding;
    private User receiveUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiveDetails();
    }

    private void loadReceiveDetails() {
        receiveUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textEmail.setText(receiveUser.email);
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
}