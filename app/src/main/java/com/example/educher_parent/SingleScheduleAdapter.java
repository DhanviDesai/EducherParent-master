package com.example.educher_parent;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import static com.example.educher_parent.AppConfiguration.PARENT_KEY;
import static com.example.educher_parent.AppConfiguration.SCHEDULE_SINGLE;

public class SingleScheduleAdapter extends RecyclerView.Adapter<SingleScheduleAdapter.SingleAppViewHolder> {

    private Context context;
    private List<AppInfo> appInfos;
    private PrefManager prefManager;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private DatabaseReference reference;
    private AppInfoDatabase database;
    private String parent_key,d,allowTime;
    private EditText time;
    private Spinner day;
    private CheckBox checkBox;
    private Button btn;
    private String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

    public SingleScheduleAdapter(Context context, List<AppInfo> appInfos) {
        this.context = context;
        this.appInfos = appInfos;
        prefManager = new PrefManager(context);
        database = new AppInfoDatabase(context);
        Cursor cursor = database.searchInAppDataTable(PARENT_KEY);
        cursor.moveToFirst();
        parent_key = cursor.getString(2);
        reference = FirebaseDatabase.getInstance().getReference(parent_key).child(prefManager.getChildUniqueId()).child(SCHEDULE_SINGLE);
    }

    @NonNull
    @Override
    public SingleAppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SingleScheduleAdapter.SingleAppViewHolder(LayoutInflater.from(context).inflate(R.layout.single_schedule,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SingleAppViewHolder myViewHolder, int i) {
        final AppInfo app = appInfos.get(i);
        final String id = app.getPackageName().replace(".","_");
        myViewHolder.name.setText(app.getName());
        Cursor c = database.getusageApp(app.getPackageName());
        if(c.getCount() > 0){
            c.moveToNext();
            int value = Integer.parseInt(c.getString(3));
            myViewHolder.progressBar.setProgress(value);
            myViewHolder.view.setVisibility(View.VISIBLE);
        }
        try {
            Glide.with(context)
                    .load(AppUtil.getPackageIcon(context, app.getPackageName()))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(myViewHolder.appIcon);
        }catch (Exception e){
            Log.d("SingleAdapter", "onBindViewHolder: error in icon");
        }
        Log.i("ChildId",prefManager.getChildUniqueId());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(parent_key).child(prefManager.getChildUniqueId())
                .child(SCHEDULE_SINGLE);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.hasChild("progress")){
                    long value = (Long) dataSnapshot.child("progress").getValue();
                    Log.i("HereInside",""+value);
                    String appName = dataSnapshot.child("app").getValue().toString();
                    if(appName.equals(app.getPackageName())){
                        myViewHolder.view.setVisibility(View.VISIBLE);
                        myViewHolder.progressBar.setVisibility(View.VISIBLE);
                        myViewHolder.progressBar.setProgress(Integer.valueOf(Long.toString(value)));
                        database.update(app.getPackageName(),Long.toString(value));

                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.i("Deleted",dataSnapshot.child("app").getValue().toString());

                database.deleteScheduleremove(dataSnapshot.child("app").getValue().toString());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleAlertDialoge(app.getPackageName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }



    public class SingleAppViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView aSwitch,appIcon;
        private ProgressBar progressBar;
        private ConstraintLayout constraintLayout;
        private View view;


        public SingleAppViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.applicationName2);
            aSwitch = itemView.findViewById(R.id.schedule_single);
            appIcon = itemView.findViewById(R.id.imageView4);
            progressBar = itemView.findViewById(R.id.progressBar_app);
            view = itemView.findViewById(R.id.view7);
            constraintLayout = itemView.findViewById(R.id.constraintSingle);
        }
    }

    private void scheduleAlertDialoge(final String app){
        dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.schedule_single_dialog, null);

        time =  dialogView.findViewById(R.id.editText);
        day =  dialogView.findViewById(R.id.spinner2);
        checkBox = dialogView.findViewById(R.id.checkBox);
        btn = dialogView.findViewById(R.id.button3);
        setupSpinnerData();
        dialogBuilder.setTitle("Add Schedule");
        dialogBuilder.setView(dialogView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowTime = time.getText().toString().trim();
                if (checkBox.isChecked()){
                    d = "everyday";
                }else{
                    d = day.getSelectedItem().toString();
                }

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("time",allowTime);
                hashMap.put("app",app);
                hashMap.put("day",d);
                database.insert(app,allowTime,d);
                reference.child(app.replace(".","_")).setValue(hashMap);
                Toast.makeText(context, "Schedule added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog = dialogBuilder.create();
        dialog.show();
    }
    private void setupSpinnerData(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, days);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day.setAdapter(dataAdapter);
    }
}
