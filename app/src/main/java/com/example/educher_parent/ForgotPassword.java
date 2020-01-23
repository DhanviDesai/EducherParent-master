package com.example.educher_parent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.educher_parent.AppConfiguration.CHILD_REGISTRATION;
import static com.example.educher_parent.AppConfiguration.PARENT_KEY;

public class ForgotPassword extends AppCompatActivity {

    private EditText pass,re_pass;
    private String p,re_p;
    private AppInfoDatabase database;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //change status bar color
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color1));

        pass = findViewById(R.id.for_pass);
        re_pass = findViewById(R.id.for_pass_reenter);
        database = new AppInfoDatabase(getApplicationContext());

        findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p = pass.getText().toString().trim();
                re_p = re_pass.getText().toString().trim();
                if (p.isEmpty() || re_p.isEmpty()){
                    Toast.makeText(ForgotPassword.this, "PLease fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (p.equals(re_p)){
                    Cursor cursor = database.searchInAppDataTable(PARENT_KEY);
                    cursor.moveToFirst();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(CHILD_REGISTRATION).child(cursor.getString(2));
                    reference.child("password").setValue(p);
                    startActivity(new Intent(getApplicationContext(),Dashboard.class));
                    finish();
                }else{
                    re_pass.setError("Password not same");
                    return;
                }
            }
        });
    }
}
