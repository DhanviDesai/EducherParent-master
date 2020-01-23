package com.example.educher_parent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.educher_parent.AppConfiguration.PARENT_KEY;
import static com.example.educher_parent.AppConfiguration.SCHEDULE;

public class ScheduleApps extends AppCompatActivity {

    private Spinner day;
    private EditText from,to;
    private String f,t,d,parent_key;
    private DatabaseReference reference;
    private AppInfoDatabase database;
    private PrefManager prefManager;
    private Button btn;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    private RecyclerView recyclerView;
    private Switch nightmood;
    private List<ScheduleModel> models;
    private static final String TAG = "ScheduleActivity";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_apps);

        //change status bar color
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color1));

        nightmood = findViewById(R.id.switch1);
        prefManager = new PrefManager(getApplicationContext());

        if (prefManager.getNightMood()){
            nightmood.setChecked(true);
        }else{
            nightmood.setChecked(false);
        }
        database = new AppInfoDatabase(getApplicationContext());
        Cursor cursor = database.searchInAppDataTable(PARENT_KEY);
        cursor.moveToFirst();
        parent_key = cursor.getString(2);
        reference = FirebaseDatabase.getInstance().getReference(parent_key).child(prefManager.getChildUniqueId()).child(SCHEDULE);
        cursor.close();

        models = new ArrayList<>();
        recyclerView = findViewById(R.id.schedule_recycler);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleAlertDialoge();
            }
        });

        nightmood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    prefManager.setNightMode(true);
                    setNightMood();
                }else{
                    prefManager.setNightMode(false);
                    deleteNightMode();

                }
            }
        });
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String ffrom = dataSnapshot.child("from").getValue().toString();
                String fto = dataSnapshot.child("to").getValue().toString();
                String fday = dataSnapshot.child("day").getValue().toString();
                String fkey = dataSnapshot.getKey();
                Log.d(TAG, "onChildAdded: "+fkey);
                ScheduleModel model = new ScheduleModel(ffrom,fto,fday,fkey,parent_key,prefManager.getChildUniqueId());
                models.add(model);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                ScheduleAdapter adapter = new ScheduleAdapter(models,getApplicationContext());
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setupSpinnerData(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, days);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day.setAdapter(dataAdapter);
    }

    private void scheduleAlertDialoge(){
        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.schedule_alert_dialog, null);

        from =  dialogView.findViewById(R.id.from);
        to   =  dialogView.findViewById(R.id.to);
        day =  dialogView.findViewById(R.id.day_spinner2);
        btn = dialogView.findViewById(R.id.button2);
        setupSpinnerData();
        dialogBuilder.setTitle("Add Schedule");
        dialogBuilder.setView(dialogView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f = from.getText().toString().trim();
                t = to.getText().toString().trim();
                d = day.getSelectedItem().toString();
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("from",f);
                hashMap.put("to",t);
                hashMap.put("day",d);
                reference.push().setValue(hashMap);
                Toast.makeText(ScheduleApps.this, "Schedule added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void setNightMood(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("from","22:00");
        hashMap.put("to","8:00");
        hashMap.put("day","everyday");
        reference.child("every").setValue(hashMap);
    }

    private void deleteNightMode(){
        reference.child("every").removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d(TAG, "onComplete: deleted");
            }
        });
    }
}
