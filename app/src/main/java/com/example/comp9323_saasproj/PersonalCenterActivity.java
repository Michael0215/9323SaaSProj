package com.example.comp9323_saasproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.comp9323_saasproj.databinding.ActivityPersonalCenterBinding;
import com.example.comp9323_saasproj.utilities.Constants;
import com.example.comp9323_saasproj.utilities.PreferenceManager;

public class PersonalCenterActivity extends AppCompatActivity {
    private @NonNull ActivityPersonalCenterBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setCancelListeners();
        setLogOutListeners();
    }

    private void setLogOutListeners(){
        binding.btnLogout.setOnClickListener(v -> signOut());
    }

    private void setCancelListeners(){
        binding.buttonCancel.setOnClickListener(v -> backToMain());
    }

    private void signOut(){
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
    private void backToMain(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}