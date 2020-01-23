package com.example.educher_parent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.example.educher_parent.AppConfiguration.CHILD_REGISTRATION;
import static com.example.educher_parent.AppConfiguration.CONNECT;
import static com.example.educher_parent.AppConfiguration.PARENT_KEY;
import static com.example.educher_parent.AppConfiguration.TOKEN;

public class PhoneVerification extends AppCompatActivity {
    private String mobile,name,email,password,forgot;
    private String mVerificationId;
    private TextView msg;
    private EditText code;
    private FirebaseAuth mAuth;
    private DatabaseReference reference,ref;
    private String token;
    private AppInfoDatabase database;
    private static final String TAG = "phoneverification";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: ");
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d(TAG, "onVerificationFailed: "+e.getMessage());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        FirebaseApp.initializeApp(this);

        //change status bar color
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));

        mAuth = FirebaseAuth.getInstance();
        code = findViewById(R.id.code);
        msg = findViewById(R.id.login_text);
        database = new AppInfoDatabase(getApplicationContext());

        forgot = "";

        reference = FirebaseDatabase.getInstance().getReference().child(CHILD_REGISTRATION);


        Bundle bundle = getIntent().getExtras();
        if (bundle !=null) {
            Intent intent = getIntent();
            if (intent.hasExtra("forgot")){
                mobile = bundle.getString("phone");
                forgot = bundle.getString("forgot");
            }else{
                mobile = bundle.getString("phone");
                name = bundle.getString("name");
                email = bundle.getString("email");
                password = bundle.getString("pass");
            }
        }
        ref = FirebaseDatabase.getInstance().getReference();
        saveToken();

        msg.setText("Please Type the verification code sent \n to "+mobile);

        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(code.getText().toString().trim().length() ==6){
                    String c = code.getText().toString().trim();
                    Log.d(TAG, "onTextChanged: "+code);
                    verifyVerificationCode(c);
                }
            }
        });
    }

    private void sendVerificationCode(String mobile) {

        Log.d(TAG, "sendVerificationCode: "+mobile);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(PhoneVerification.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: success");
                    //verification successful we will start the profile activity
                    if (forgot.equals("forgot")){

                        startActivity(new Intent(getApplicationContext(),ForgotPassword.class));

                    }else{

                        final String phchange= mobile.replace("+","");
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("name",name);
                        hashMap.put("email",email);
                        hashMap.put("password",password);
                        hashMap.put("phone",phchange);
                        HashMap<String,String> tokenmap = new HashMap<>();
                        tokenmap.put("token",token);
                        ref.child(phchange).child(TOKEN).setValue(tokenmap);
                        reference.child(phchange).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    database.removeAppFromDatabase(PARENT_KEY);
                                    database.insertDataInAppDataTable(PARENT_KEY,phchange);
                                    database.insertDataInAppDataTable(CONNECT,"true");
                                    nextActivity();
                                }else {
                                    Toast.makeText(PhoneVerification.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                } else {

                    String message = "Somthing is wrong, we will fix it soon...";

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered...";
                    }
                    Toast.makeText(PhoneVerification.this, ""+message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void nextActivity(){
        startActivity(new Intent(getApplicationContext(),Dashboard.class));
        finish();
    }

    private void saveToken(){
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( PhoneVerification.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                sendVerificationCode(mobile);
            }
        });
    }
}
