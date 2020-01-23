package com.example.educher_parent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.example.educher_parent.AppConfiguration.CHILD_REGISTRATION;
import static com.example.educher_parent.AppConfiguration.PARENT_KEY;

public class EditProfile extends AppCompatActivity {

    private EditText name,email,pass;
    private DatabaseReference reference;
    private AppInfoDatabase database;
    private String n,e,p,key;
    private static final String TAG = "EditProfile";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //change status bar color
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color1));



        database = new AppInfoDatabase(getApplicationContext());
        Cursor cursor = database.searchInAppDataTable(PARENT_KEY);
        cursor.moveToFirst();
        key = cursor.getString(2);
        reference = FirebaseDatabase.getInstance().getReference().child(CHILD_REGISTRATION).child(key);

        name = findViewById(R.id.edit_name);
        email = findViewById(R.id.edit_email);
        pass = findViewById(R.id.edit_pass);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nam = dataSnapshot.child("name").getValue().toString();
                String em = dataSnapshot.child("email").getValue().toString();
                String pas = dataSnapshot.child("password").getValue().toString();
                name.setText(nam);
                email.setText(em);
                pass.setText(pas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }

    private void update(){

        n = name.getText().toString().trim();
        e = email.getText().toString().trim();
        p = pass.getText().toString().trim();

        if (n.isEmpty() || e.isEmpty() || p.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("name",n);
        hashMap.put("email",e);
        hashMap.put("password",p);
        hashMap.put("phone",key);

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(EditProfile.this, "Updated", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditProfile.this, "Something went wrong.try again", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
