package com.example.tufengyi.manlife.db;

import android.content.Context;
import android.content.SharedPreferences;

public class SPManager {

    public static void setting_add(String key, String value, Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences("Settings",0);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(key,value);
        mEditor.apply();
    }

    public static String setting_get(String key,Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences("Settings",0);
        String value = mSharedPreferences.getString(key,null);
        return value;

    }
}
