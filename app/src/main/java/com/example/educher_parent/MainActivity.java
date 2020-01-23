package com.example.educher_parent;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;

import static com.example.educher_parent.AppConfiguration.CONNECT;

public class MainActivity extends AppCompatActivity {

    private EditText name,email,pass;
    private EditText phone;
    private Spinner phone_code;
    private String ph,n,e,p;
    private TextView login;
    private String[] countrtyCode= {"+91","+92","+93"};
    private AppInfoDatabase database;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        database = new AppInfoDatabase(getApplicationContext());
        Cursor cursor = database.searchInAppDataTable(CONNECT);
        if (cursor.getCount()>0){
            cursor.close();
            startActivity(new Intent(getApplicationContext(),Dashboard.class));
            finish();
        }
        setContentView(R.layout.activity_main);

        //change status bar color
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));

        init();

        setupSpinnerData();

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                n = name.getText().toString().trim();
                e = email.getText().toString().trim();
                p = pass.getText().toString().trim();
                ph = phone_code.getSelectedItem()+phone.getText().toString().trim();
                if (n.isEmpty() || e.isEmpty() || p.isEmpty() || ph.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please insert all values", Toast.LENGTH_SHORT).show();
                    return;
                }
                nextActivity();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }

    private void init(){

        name = findViewById(R.id.signup_name_wrapper2);
        email = findViewById(R.id.signup_email_wrapper2);
        pass = findViewById(R.id.signup_pass_wrapper2);
        phone = findViewById(R.id.phone_signup);
        phone_code = findViewById(R.id.signup_spinner);
        login = findViewById(R.id.login_text);

    }

    private void setupSpinnerData(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countrtyCode);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phone_code.setAdapter(dataAdapter);
    }

    private void nextActivity(){
        Intent intent = new Intent(getApplicationContext(),PhoneVerification.class);
        intent.putExtra("phone",ph);
        intent.putExtra("name",n);
        intent.putExtra("email",e);
        intent.putExtra("pass",p);
        startActivity(intent);
    }
}
