package com.rsin.dhongpana.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rsin.dhongpana.R;
import com.rsin.dhongpana.util.OnItemClick;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    ArrayList<String> checkedAppList;
    Context context;
    List<ApplicationInfo> applicationInfos;
    PackageManager packageManager;
    private OnItemClick mListener;
    public void setClickListener(OnItemClick mListener) {
        this.mListener = mListener;
    }

    public AppListAdapter(List<ApplicationInfo> appList, Context context, PackageManager packageManager,ArrayList<String> checkedAppList) {
        this.applicationInfos = appList;
        this.context = context;
        applicationInfos = appList;
        this.packageManager = packageManager;
        this.checkedAppList = checkedAppList;
    }

    @NonNull
    @Override
    public AppListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppListAdapter.ViewHolder holder, int position) {
        holder.appPackageName.setText(applicationInfos.get(position).packageName);
        holder.appName.setText( packageManager.getApplicationLabel(applicationInfos.get(position)));
        Drawable appIconDrawable =  applicationInfos.get(position).loadIcon(packageManager);
        holder.appIcon.setImageDrawable(appIconDrawable);
        if (!checkedAppList.isEmpty())
        {
            if (checkedAppList.contains(applicationInfos.get(position).packageName)){
                holder.checkBox.setChecked(true);
            }
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
            {
                mListener.addItem(applicationInfos.get(position).packageName);
            }
            else {
                mListener.removeItem(applicationInfos.get(position).packageName);
                // remove from list
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicationInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appPackageName,appName;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.img_icon);
            appPackageName = itemView.findViewById(R.id.tx_pkg);
            appName = itemView.findViewById(R.id.tx_app);
            checkBox = itemView.findViewById(R.id.cb_app);
            
        }
    }

    public ArrayList<String> getCheckedApps()
    {
        return null;
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}
