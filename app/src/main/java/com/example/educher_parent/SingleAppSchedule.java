package com.example.educher_parent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.educher_parent.AppConfiguration.APPS;
import static com.example.educher_parent.AppConfiguration.PARENT_KEY;

public class SingleAppSchedule extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<AppInfo> appInfos;
    private AppInfoDatabase database;
    private DatabaseReference firebaseRef;
    private PrefManager prefManager;
    private int i;
    private static final String TAG = "SingleSchduleApp";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_app_schedule);

        if(!isNetworkAvailable(this)){
            Toast.makeText(this, "Internet Not Available", Toast.LENGTH_SHORT).show();
            finish();
        }

        //change status bar color
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color1));

        init();


        Cursor cursor = database.searchInAppDataTable(PARENT_KEY);
        cursor.moveToFirst();

        firebaseRef = FirebaseDatabase.getInstance().getReference(cursor.getString(2)).child(prefManager.getChildUniqueId()).child(APPS);

        firebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                i++;
                String id = dataSnapshot.child("id").getValue().toString();
                String locked = dataSnapshot.child("locked").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String packageName = dataSnapshot.child("packageName").getValue().toString();
                AppInfo appInfo = new AppInfo(Integer.parseInt(id),name,packageName,Boolean.parseBoolean(locked));
                appInfos.add(appInfo);
                Log.d(TAG, "onChildAdded: "+dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount()==i){
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    SingleScheduleAdapter adapter = new SingleScheduleAdapter(SingleAppSchedule.this,appInfos);
                    recyclerView.setAdapter(adapter);
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

    private void init(){
        appInfos = new ArrayList<>();
        recyclerView = findViewById(R.id.singleschedule);
        prefManager = new PrefManager(getApplicationContext());
        database = new AppInfoDatabase(getApplicationContext());
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
