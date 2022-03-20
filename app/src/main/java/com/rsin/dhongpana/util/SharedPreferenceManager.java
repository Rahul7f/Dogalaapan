package com.rsin.dhongpana.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SharedPreferenceManager {
    public static final String APPS_KEY = "saved_apps";
    Context context;
    SharedPreferences preferences;
    TinyDB tinydb;
    public SharedPreferenceManager(Context context) {
        this.context = context;
        if (context==null)
        {
            Log.d("SP", "null prif");
        }
        else {
            preferences = context.getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE);
            tinydb = new TinyDB(context);
        }

    }


    public  void saveAppList(ArrayList<String> apps)
    {
        tinydb.putListString(APPS_KEY,apps);
//        SharedPreferences.Editor editor = preferences.edit();
//        String result = TextUtils.join("|",apps);
//        editor.putString(APPS_KEY, result);
    }

    public ArrayList<String> readAppsList() {

        try {
//            String raw = preferences.getString(APPS_KEY,"");
//            return new ArrayList<>(Arrays.asList(raw.split("|")));
            return tinydb.getListString(APPS_KEY);
        }catch (Exception e)
        {
            Log.d("SP",e.getMessage());
        }
        return null;
    }
}
