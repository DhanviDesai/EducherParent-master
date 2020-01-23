package com.example.educher_parent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.educher_parent.AppConfiguration.CHILD_REGISTRATION;
import static com.example.educher_parent.AppConfiguration.CONNECT;
import static com.example.educher_parent.AppConfiguration.PARENT_KEY;

public class Login extends AppCompatActivity {

    private EditText passwordwrapper;
    private EditText phone;
    private Spinner spinner;
    private String[] countrtyCode= {"+91","+92","+93"};
    private DatabaseReference reference;
    private String ph,pass,ch;
    private AppInfoDatabase database;
    private AlertDialog.Builder builder;
    private EditText pho;
    private static final String TAG = "Login";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new AppInfoDatabase(getApplicationContext());
        Cursor cursor = database.searchInAppDataTable(CONNECT);
        if (cursor.getCount()>0){
            cursor.close();
            startActivity(new Intent(getApplicationContext(),Dashboard.class));
            finish();
        }
        setContentView(R.layout.activity_login);

        //change status bar color
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));

        init();

        setupSpinnerData();


        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = passwordwrapper.getText().toString().trim();
                ph = spinner.getSelectedItem()+phone.getText().toString().trim();
                if (pass.isEmpty() || ph.isEmpty()){
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                verify();
            }
        });

        findViewById(R.id.textView9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        findViewById(R.id.forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotAlert();
            }
        });

    }

    private void init(){

        passwordwrapper = findViewById(R.id.login_pass);
        phone = findViewById(R.id.login_phone);
        spinner = findViewById(R.id.login_spinner);
    }

    private void setupSpinnerData(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countrtyCode);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void verify(){
        ch = ph.replace("+","");
        reference = FirebaseDatabase.getInstance().getReference().child(CHILD_REGISTRATION).child(ch);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount()!=0) {

                    String p = dataSnapshot.child("phone").getValue().toString();
                    String pa = dataSnapshot.child("password").getValue().toString();
                    Log.d(TAG, "onDataChange:phone "+p);
                    Log.d(TAG, "onDataChange:pass "+pa);
                    if (p.equals(ch) && pa.equals(pass)){
                        database.removeAppFromDatabase(PARENT_KEY);
                        database.insertDataInAppDataTable(PARENT_KEY,p);
                        database.insertDataInAppDataTable(CONNECT,"true");
                        startActivity(new Intent(getApplicationContext(),Dashboard.class));
                        finish();
                    }
                }else{
                    Toast.makeText(Login.this, "invalid phone number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void forgotAlert(){
        builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.forgotlayout, null);

        pho =  dialogView.findViewById(R.id.forgot_email);
        builder.setTitle("Forgot Password");
        builder.setView(dialogView);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String forgot_phone = pho.getText().toString().trim();
                Intent intent = new Intent(getApplicationContext(),PhoneVerification.class);
                intent.putExtra("phone",forgot_phone);
                intent.putExtra("forgot","forgot");
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
