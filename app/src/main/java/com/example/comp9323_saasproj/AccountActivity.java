package com.example.comp9323_saasproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.comp9323_saasproj.databinding.ActivityAccountBinding;
import com.example.comp9323_saasproj.utilities.Constants;
import com.example.comp9323_saasproj.utilities.PreferenceManager;

public class AccountActivity extends AppCompatActivity {
    private @NonNull ActivityAccountBinding binding;
    private PreferenceManager preferenceManager;

    // Activity for 'Account' page.
    /* Because this is not the core feature of our project,
    we did not implement functions like 'My Profile', 'Update Password', etc.
    Only 'Sign Out' and 'Back' button is available.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners(){
        binding.btnLogout.setOnClickListener(v -> signOut());
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