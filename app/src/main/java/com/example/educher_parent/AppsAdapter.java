package com.example.educher_parent;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.example.educher_parent.AppConfiguration.APPS;
import static com.example.educher_parent.AppConfiguration.PARENT_KEY;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppsViewHolder> {
    private Context context;
    private List<AppInfo> appInfos;
    private PrefManager prefManager;
    private static final String TAG = "MyAdapter";

    public AppsAdapter(Context context, List<AppInfo> appInfos) {
        this.context = context;
        this.appInfos = appInfos;
        prefManager = new PrefManager(context);
    }

    @NonNull
    @Override
    public AppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppsViewHolder(LayoutInflater.from(context).inflate(R.layout.app_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AppsViewHolder holder, final int position) {

        final AppInfo app = appInfos.get(position);
        final String id = app.getPackageName().replace(".","_");
        holder.name.setText(app.getName());
        if (!app.getLocked()){
            holder.aSwitch.setBackground(context.getResources().getDrawable(R.drawable.ic_lock_open_black_24dp));
        }else {
            holder.aSwitch.setBackground(context.getResources().getDrawable(R.drawable.ic_lock_outline_black_24dp));
        }
        holder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.getLocked()){
                    ObjectAnimator animator = ObjectAnimator.ofFloat(holder.aSwitch,"rotation",-360f);
                    animator.setDuration(700);
                    animator.start();
                    holder.aSwitch.setBackground(context.getResources().getDrawable(R.drawable.ic_lock_outline_black_24dp));
                    app.setLocked(true);
                    Toast.makeText(context, "App locked", Toast.LENGTH_SHORT).show();
                    updateValue(id,true);
                }else {
                    ObjectAnimator animator2 = ObjectAnimator.ofFloat(holder.aSwitch,"rotation",360f);
                    animator2.setDuration(700);
                    app.setLocked(false);
                    animator2.start();
                    holder.aSwitch.setBackground(context.getResources().getDrawable(R.drawable.ic_lock_open_black_24dp));
                    Toast.makeText(context, "App unlocked", Toast.LENGTH_SHORT).show();
                    updateValue(id,false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }

    public void updateValue(String childId, Boolean isLocked){
        Log.d(TAG, "updateValue:child id "+childId+" isLock: "+isLocked);
        AppInfoDatabase database = new AppInfoDatabase(context);
        Cursor cursor=database.searchInAppDataTable(PARENT_KEY);
        cursor.moveToFirst();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(cursor.getString(2)).child(prefManager.getChildUniqueId()).child(APPS).child(childId);
        databaseReference.child("locked").setValue(isLocked);

    }

    public class AppsViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView aSwitch;
        public AppsViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.applicationName);
            aSwitch = itemView.findViewById(R.id.switchView);
        }
    }
}
