package com.example.comp9323_saasproj.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    // This is a class that critical to live chat features.
    /*
    SharedPreferences is a lightweight storage class on the Android platform.
    It is used to save common application configurations, such as Activity status.
    When an Activity is paused, the state of the Activity is saved to SharedPereferences.
    When the Activity reloads and the system callback method onSaveInstanceState is called,
    the value is then removed from SharedPreferences.
     */

    private  final SharedPreferences sharedPreferences;

    // Constructor
    public PreferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences("name", Context.MODE_PRIVATE);
    }

    // Implement functions of setting and getting values.
    public void putBoolean(String key, boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key){
        return sharedPreferences.getString(key, null);
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}


