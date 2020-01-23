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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import static com.example.educher_parent.AppConfiguration.SUGGESTION;

public class Suggestion extends AppCompatActivity {
    private EditText appname,applink;
    private String name,link,parent_key,token;
    private DatabaseReference reference;
    private AppInfoDatabase database;
    private PrefManager prefManager;
    private Button btn;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RecyclerView recyclerView;
    private List<SuggestionModel> models;
    private static final String TAG = "SuggestionActivity";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        //change status bar color
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color1));


        prefManager = new PrefManager(getApplicationContext());
        database = new AppInfoDatabase(getApplicationContext());
        Cursor cursor = database.searchInAppDataTable(PARENT_KEY);
        cursor.moveToFirst();
        parent_key = cursor.getString(2);
        reference = FirebaseDatabase.getInstance().getReference(parent_key).child(prefManager.getChildUniqueId()).child(SUGGESTION);
        cursor.close();

        models = new ArrayList<>();
        recyclerView = findViewById(R.id.suggestion_recycler_single);

        findViewById(R.id.add_suggestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleAlertDialoge();
            }
        });
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String ffrom = dataSnapshot.child("name").getValue().toString();
                String fto = dataSnapshot.child("link").getValue().toString();
                SuggestionModel model = new SuggestionModel(ffrom,fto);
                models.add(model);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                SuggestionAdapter adapter = new SuggestionAdapter(models,getApplicationContext());
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

    private void scheduleAlertDialoge(){
        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.suggestion_dialog, null);

        appname =  dialogView.findViewById(R.id.editText2);
        applink   =  dialogView.findViewById(R.id.editText3);

        btn = dialogView.findViewById(R.id.button4);
        dialogBuilder.setTitle("Add Suggestion App");
        dialogBuilder.setView(dialogView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = appname.getText().toString().trim();
                link = applink.getText().toString().trim();
                token = prefManager.getChildUniqueId();

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("name",name);
                hashMap.put("link",link);

                reference.push().setValue(hashMap);
                Toast.makeText(Suggestion.this, "Suggestion sent", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog = dialogBuilder.create();
        dialog.show();
    }
}
