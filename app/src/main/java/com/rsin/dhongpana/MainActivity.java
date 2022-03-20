package com.rsin.dhongpana;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rsin.dhongpana.adapter.AppListAdapter;
import com.rsin.dhongpana.util.OnItemClick;
import com.rsin.dhongpana.util.SharedPreferenceManager;
import com.rsin.dhongpana.util.TinyDB;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton actionButton;
    AppListAdapter adapter;
    SharedPreferenceManager preferenceManager;
    ArrayList<String> checkedAppList;
    String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_apps);
        actionButton = findViewById(R.id.fab_save);

        // geting all apps
        List<ApplicationInfo> applicationInfos = new ArrayList<>();
              applicationInfos  = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        List<ApplicationInfo> applicationInfos2 = new ArrayList<>();
        for (ApplicationInfo info: applicationInfos)
        {
            if (getPackageManager().getLaunchIntentForPackage(info.packageName)!=null)
            {
               applicationInfos2.add(info);

            }
        }

        preferenceManager  = new SharedPreferenceManager(getApplicationContext());
        checkedAppList  = new ArrayList<>();
        checkedAppList = preferenceManager.readAppsList();

        adapter = new AppListAdapter(applicationInfos2,getApplicationContext(),getPackageManager(),checkedAppList);
        adapter.setClickListener(onItemClick());
        recyclerView.setAdapter(adapter);
        actionButton.setOnClickListener(view -> {
            if (!checkedAppList.isEmpty())
            {
                preferenceManager.saveAppList(checkedAppList);
                Log.d(TAG, checkedAppList.toString());
            }
            else {
                Toast.makeText(getApplicationContext(), "select items", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isServiceEnabled()) {
            Toast.makeText(getApplicationContext(), "service is not enable", Toast.LENGTH_SHORT).show();
            promptServiceOff();
        }
        else if (isRunningMiui()) {
            promptExtraMiuiPermission();
        }
    }

    private void promptExtraMiuiPermission() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.alert_miui_title)
                .setMessage(R.string.alert_enable)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_miui_alert), Toast.LENGTH_SHORT).show();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS,Uri.fromParts("package",getPackageName(),null));
                                startActivity(intent);
                            }
                        },500L);
                    }
                })
                .show();

    }

    private boolean isRunningMiui() {
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod(c.getName());
            String miui = get.invoke(c,"ro.miui.ui.version.name").toString();
            return (miui != null && !miui.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void promptServiceOff() {
        Context context = new ContextThemeWrapper(MainActivity.this, R.style.AppTheme2);
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.alert_title)
                .setMessage(R.string.alert_message)
                .setPositiveButton(R.string.alert_enable,(dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setCancelable(true)
                .show();
    }

    private boolean isServiceEnabled() {
        AccessibilityManager am = (AccessibilityManager) getApplicationContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(getPackageName()) && enabledServiceInfo.name.equals("accessibility"))
                return true;
        }

        return false;

    }
    OnItemClick onItemClick()
    {
        return new OnItemClick() {

            @Override
            public void addItem(String packageName) {
                checkedAppList.add(packageName);
                Log.d(TAG, checkedAppList.toString());

            }

            @Override
            public void removeItem(String packageName) {
                checkedAppList.remove(packageName);
                Log.d(TAG, checkedAppList.toString());
            }
        };
    }



}