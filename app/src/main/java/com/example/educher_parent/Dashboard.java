package com.example.educher_parent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.educher_parent.AppConfiguration.APPS;
import static com.example.educher_parent.AppConfiguration.PARENT;
import static com.example.educher_parent.AppConfiguration.PARENT_KEY;

public class Dashboard extends AppCompatActivity {


    private ConstraintLayout apps,usage,schedule,setting,sug;
    private PrefManager prefManager;
    private TextView key,child_phone,lock_phone;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private AppInfoDatabase dataBaseHelper;
    private ArrayAdapter<String> arrayAdapter;
    private List<ChildInfo> deviceInfos;
    private AlertDialog.Builder builderSingle;
    private List<ChildInfo> data;
    private static final String TAG = "Dashboaard";
    String packageName;
    String parent_key;
    String SystemUi;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //change status bar color
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));

        init();

        apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String child = child_phone.getText().toString().trim();
//                apps.setBackground(getResources().getDrawable(R.drawable.border));
//                sug.setBackground(getResources().getDrawable(R.drawable.borderless));
//                setting.setBackground(getResources().getDrawable(R.drawable.borderless));
//                schedule.setBackground(getResources().getDrawable(R.drawable.borderless));
//                usage.setBackground(getResources().getDrawable(R.drawable.borderless));
                if (child.isEmpty()){
                    Toast.makeText(Dashboard.this, "Please select child first", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(getApplicationContext(),AllApps.class));
                }
            }
        });
        usage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                usage.setBackground(getResources().getDrawable(R.drawable.border));
//                sug.setBackground(getResources().getDrawable(R.drawable.borderless));
//                setting.setBackground(getResources().getDrawable(R.drawable.borderless));
//                schedule.setBackground(getResources().getDrawable(R.drawable.borderless));
//                apps.setBackground(getResources().getDrawable(R.drawable.borderless));
                String child = child_phone.getText().toString().trim();
                if (child.isEmpty()){
                    Toast.makeText(Dashboard.this, "Please select child first", Toast.LENGTH_SHORT).show();
                }
                else{
                    startActivity(new Intent(getApplicationContext(),AppUsage.class));
                }

            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                schedule.setBackground(getResources().getDrawable(R.drawable.border));
//                sug.setBackground(getResources().getDrawable(R.drawable.borderless));
//                setting.setBackground(getResources().getDrawable(R.drawable.borderless));
//                usage.setBackground(getResources().getDrawable(R.drawable.borderless));
//                apps.setBackground(getResources().getDrawable(R.drawable.borderless));
                String child = child_phone.getText().toString().trim();
                if (child.isEmpty()){
                    Toast.makeText(Dashboard.this, "Please select child first", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(getApplicationContext(),SelectSchedule.class));

                }

            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setting.setBackground(getResources().getDrawable(R.drawable.border));
//                schedule.setBackground(getResources().getDrawable(R.drawable.borderless));
//                sug.setBackground(getResources().getDrawable(R.drawable.borderless));
//                usage.setBackground(getResources().getDrawable(R.drawable.borderless));
//                apps.setBackground(getResources().getDrawable(R.drawable.borderless));
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));

            }
        });

        sug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sug.setBackground(getResources().getDrawable(R.drawable.border));
//                setting.setBackground(getResources().getDrawable(R.drawable.borderless));
//                schedule.setBackground(getResources().getDrawable(R.drawable.borderless));
//                usage.setBackground(getResources().getDrawable(R.drawable.borderless));
//                apps.setBackground(getResources().getDrawable(R.drawable.borderless));
                String child = child_phone.getText().toString().trim();
                if (child.isEmpty()){
                    Toast.makeText(Dashboard.this, "Please select child first", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(getApplicationContext(),Suggestion.class));

                }
            }
        });

        if (prefManager.getWhichPerson() != null)
            child_phone.setText(prefManager.getWhichPerson());
        child_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sug.setBackground(getResources().getDrawable(R.drawable.borderless));
//                setting.setBackground(getResources().getDrawable(R.drawable.borderless));
//                schedule.setBackground(getResources().getDrawable(R.drawable.borderless));
//                usage.setBackground(getResources().getDrawable(R.drawable.borderless));
//                apps.setBackground(getResources().getDrawable(R.drawable.borderless));
                Cursor cursor = dataBaseHelper.fetchDeviceInfo();
                if (cursor.getCount()>0){
                    myAlertDialoge();
                }else {
                    myAlert();
                }

            }
        });






        Cursor c = dataBaseHelper.searchInAppDataTable(PARENT_KEY);
        c.moveToFirst();
        parent_key = c.getString(2);


        Log.d(TAG, "onCreate: "+c.getString(2));
        databaseReference = FirebaseDatabase.getInstance().getReference(PARENT).child(c.getString(2));

        c.close();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ChildInfo childInfos = dataSnapshot.getValue(ChildInfo.class);
                Log.d(TAG, "onChildAdded: "+childInfos.getPhone());
                deviceInfos.add(childInfos);

                for (ChildInfo info:deviceInfos){
                    if (prefManager.getChildDevice()) {
                        Cursor cursor1 = dataBaseHelper.searchChildDeviceTable(info.getChild_key());
                        if (cursor1.getCount() == 0) {
                            dataBaseHelper.insertChildDeviceInfo(info.getPhone(), info.getChild_key());
                        }
                    }else{
                        prefManager.setChildDevice(true);
                        dataBaseHelper.insertChildDeviceInfo(info.getPhone(), info.getChild_key());
                    }
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setLock_phone(){
        lock_phone = findViewById(R.id.lock_phone);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(parent_key).child(prefManager.getChildUniqueId())
                .child(APPS);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String name = dataSnapshot.child("name").getValue().toString();
                if(name.equals("Phone Screen")){
                    packageName = dataSnapshot.child("packageName").getValue().toString().replace('.','_');
                }
                if(name.equals("System UI")){
                    SystemUi = dataSnapshot.child("packageName").getValue().toString().replace('.','_');
                }
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

        if(prefManager.getWhichPerson() != null){
            lock_phone.setVisibility(View.VISIBLE);
        }

        lock_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(parent_key).child(prefManager.getChildUniqueId())
                        .child(APPS).child(packageName);
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child(parent_key).child(prefManager.getChildUniqueId())
                        .child(APPS).child(SystemUi);
                if (lock_phone.getText().equals("Lock Phone")) {
                    reference.child("locked").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                          //  Toast.makeText(Dashboard.this, "Phone Locked", Toast.LENGTH_SHORT).show();
                            lock_phone.setText("Unlock Phone");
                        }
                    });
                    reference1.child("locked").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Dashboard.this, "Phone Locked", Toast.LENGTH_SHORT).show();
                            lock_phone.setText("Unlock Phone");
                        }
                    });
                }else if(lock_phone.getText().equals("Unlock Phone")){
                    reference.child("locked").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(Dashboard.this, "Phone Unlocked", Toast.LENGTH_SHORT).show();
                            lock_phone.setText("Lock Phone");
                        }
                    });

                    reference1.child("locked").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Dashboard.this, "Phone Unlocked", Toast.LENGTH_SHORT).show();
                            lock_phone.setText("Lock Phone");
                        }
                    });
                }
            }
        });

    }

    private void init(){

        schedule = findViewById(R.id.schedule);
        usage = findViewById(R.id.app_statics);
        apps = findViewById(R.id.apps);
        setting = findViewById(R.id.setting);
        sug = findViewById(R.id.suggestion);
        prefManager = new PrefManager(getApplicationContext());
        child_phone = findViewById(R.id.child_phone);
        dataBaseHelper = new AppInfoDatabase(getApplicationContext());
        deviceInfos = new ArrayList<>();
        data = new ArrayList<>();

    }

    private void myAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
        builder.setTitle("Parent control App");
        builder.setMessage("You Don't have any child registered.Please install Educher-Child app on your child's device and enter your pin to register the child under you.");
        builder.setCancelable(false);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void myAlertDialoge(){
        arrayAdapter = new ArrayAdapter<String>(Dashboard.this, android.R.layout.select_dialog_singlechoice);
        Cursor c = dataBaseHelper.fetchDeviceInfo();
        while (c.moveToNext()){
            data.add(new ChildInfo(c.getString(1),c.getString(2)));
            arrayAdapter.add(c.getString(2));
        }
        builderSingle = new AlertDialog.Builder(Dashboard.this);
        builderSingle.setTitle("Select a child");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                prefManager.setWhichPerson(strName);
                prefManager.setChildUniqueId(data.get(which).getChild_key());
                child_phone.setText(strName);
                setLock_phone();
            }
        });
        builderSingle.show();
    }


}
