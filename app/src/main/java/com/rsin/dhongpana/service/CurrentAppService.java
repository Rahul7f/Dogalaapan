package com.rsin.dhongpana.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.rsin.dhongpana.MemeActivity;
import com.rsin.dhongpana.util.SharedPreferenceManager;

public class CurrentAppService extends AccessibilityService {
    String TAG = "CurrentAppService";
    private String currentFocusedPackage = "";
    SharedPreferenceManager preferenceManager;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Service Connected!");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferenceManager = new SharedPreferenceManager(CurrentAppService.this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG,"AccessibilityEvent");
        if (event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        {
            if (event.getPackageName() !=null)
            {
                Log.d(TAG,event.getPackageName().toString());
                if (isPackageSaved(event.getPackageName().toString()))
                {
                    // bhai ko call kro be
                    Log.d(TAG, "Saved Package in context: "+currentFocusedPackage);
                    Intent intent  = new Intent(getApplicationContext(), MemeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Log.d(TAG,"list contain this app");

                }
                else {
                    Log.d(TAG,"list not contain this app");
                }

            }
        }

    }

    private boolean isPackageSaved(String pack) {
            Log.e(TAG, preferenceManager.readAppsList().toString());
        return preferenceManager.readAppsList().contains(pack);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service Interrupted!");

    }

    ActivityInfo getActivityName(ComponentName componentName)
    {
        try {
           return getPackageManager().getActivityInfo(componentName,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
