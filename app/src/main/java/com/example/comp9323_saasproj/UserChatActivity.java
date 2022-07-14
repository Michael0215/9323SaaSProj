package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.comp9323_saasproj.databinding.ActivityUserChatBinding;

public class UserChatActivity extends AppCompatActivity {

    private ActivityUserChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
}