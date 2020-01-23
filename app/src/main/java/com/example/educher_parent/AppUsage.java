package com.example.educher_parent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.educher_parent.AppConfiguration.APPS_USAGE;
import static com.example.educher_parent.AppConfiguration.PARENT_KEY;

public class AppUsage extends AppCompatActivity {

    private RecyclerView mList;
    private AppsAdapter mAdapter;
    private long mTotal;
    private int i=0;
    private List<AppItem> appInfos;
    private AppInfoDatabase database;
    private DatabaseReference firebaseRef;
    private PrefManager prefManager;
    private List<GraphModel> dataEntries;
    private static final String TAG = "AppUsageActivity";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);


        if(!isNetworkAvailable(this)){
            Toast.makeText(this, "Internet Not Available", Toast.LENGTH_SHORT).show();
            finish();
        }

        //change status bar color
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color1));

        dataEntries = new ArrayList<>();

        init();

//        findViewById(R.id.back_apps).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),Dashboaard.class));
//                finish();
//            }
//        });

        Cursor cursor = database.searchInAppDataTable(PARENT_KEY);
        cursor.moveToFirst();

        Query query = FirebaseDatabase.getInstance().getReference(cursor.getString(2)).child(prefManager.getChildUniqueId()).child(APPS_USAGE).orderByChild("mUsageTime");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                i++;
                String mCanOpen = dataSnapshot.child("mCanOpen").getValue().toString();
                String mCount = dataSnapshot.child("mCount").getValue().toString();
                String mEventTime = dataSnapshot.child("mEventTime").getValue().toString();
                String mEventType = dataSnapshot.child("mEventType").getValue().toString();
                String mMobile = dataSnapshot.child("mMobile").getValue().toString();
                String mName = dataSnapshot.child("mName").getValue().toString();
                String mPackageName = dataSnapshot.child("mPackageName").getValue().toString();
                String mUsageTime = dataSnapshot.child("mUsageTime").getValue().toString();
                mTotal = mTotal + Integer.parseInt(mUsageTime);
                AppItem appInfo = new AppItem(mName,mPackageName,mEventTime,mUsageTime,mEventType,mCount,mMobile,mCanOpen);
                dataEntries.add(new GraphModel(mName, Integer.parseInt(mUsageTime)));
                if (Integer.parseInt(mCount)>0) {
                    appInfos.add(appInfo);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setReverseLayout(true);
                    linearLayoutManager.setStackFromEnd(true);
                    mList.setLayoutManager(linearLayoutManager);

                    MyAdapter2 adapter = new MyAdapter2(appInfos);
                    mList.setAdapter(adapter);
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
        mTotal = 0;
        appInfos = new ArrayList<>();
        mList = findViewById(R.id.list);
        prefManager = new PrefManager(getApplicationContext());
        database = new AppInfoDatabase(getApplicationContext());
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {

        private List<AppItem> mData;

        public MyAdapter2(List<AppItem> mData) {
            this.mData = mData;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            AppItem item = mData.get(position);
            holder.mName.setText(item.getmName());
            holder.mUsage.setText(AppUtil.formatMilliSeconds(Long.parseLong(item.getmUsageTime())));
            holder.mTime.setText(String.format(Locale.getDefault(),
                    "%s · %d %s ",
                    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(Long.parseLong(item.getmEventTime()))),
                    Integer.parseInt(item.getmCount()),
                    getResources().getString(R.string.times_only))
            );
            if (mTotal > 0) {
                holder.mProgress.setProgress((int) (Integer.parseInt(item.getmUsageTime()) * 100 / mTotal));
            } else {
                holder.mProgress.setProgress(0);
            }
            try {
                Glide.with(AppUsage.this)
                        .load(AppUtil.getPackageIcon(AppUsage.this, item.getmPackageName()))
                        .transition(new DrawableTransitionOptions().crossFade())
                        .into(holder.mIcon);
            }catch (Exception e){
                Log.d(TAG, "onBindViewHolder: error in icon");
            }

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),GraphActivity.class);
                    intent.putExtra("name",holder.getAdapterPosition());
                    intent.putExtra("data", (Serializable) dataEntries);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView mName;
            private TextView mUsage;
            private TextView mTime;
            private ImageView mIcon;
            private ProgressBar mProgress;
            private RelativeLayout relativeLayout;

            MyViewHolder(View itemView) {
                super(itemView);
                mName = itemView.findViewById(R.id.app_name);
                mUsage = itemView.findViewById(R.id.app_usage);
                mTime = itemView.findViewById(R.id.app_time);
                mIcon = itemView.findViewById(R.id.app_image);
                mProgress = itemView.findViewById(R.id.progressBar);
                relativeLayout = itemView.findViewById(R.id.rel);
            }
        }
    }
}
