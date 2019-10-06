package com.kwaou.libraryadmin.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.models.Admin;
import com.kwaou.libraryadmin.sqlite.KeyValueDb;


public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout content;
    EditText phoneemail, password;
    Button buttonLogin;
    boolean isEmail = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        content = findViewById(R.id.content);
        phoneemail = findViewById(R.id.phoneemail);
        password = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.loginBtn);

        int login_state = Integer.parseInt(KeyValueDb.get(this, Config.LOGIN_STATE,"0"));
        if(login_state == 1){
            content.setVisibility(View.INVISIBLE);
            gotoMainActivity();
        }

        buttonLogin.setOnClickListener(this);


    }

    private void gotoMainActivity() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        };
        new Handler().postDelayed(runnable,2000);
    }

    @Override
    public void onClick(View view) {
         if(view == buttonLogin){
            if(valid()){
                loginAdmin();
            }
        }
    }

    private void loginAdmin() {
        progressDialog.show();
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_ADMIN);
        final String username = phoneemail.getText().toString();
        final String pass = password.getText().toString();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                Admin admin = dataSnapshot.getValue(Admin.class);
                if(admin!=null) {
                    if (admin.getUsername().equals(username) && admin.getPassword().equals(pass)) {
                        String token = KeyValueDb.get(SplashActivity.this, Config.USER_TOKEN,"");
                        userRef.child("token").setValue(token);
                        saveandMoveForward();
                        Toast.makeText(SplashActivity.this, "login success", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SplashActivity.this, "invalid details", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveandMoveForward() {


        KeyValueDb.set(this, Config.LOGIN_STATE,"1",1);

        gotoMainActivity();

    }

    private boolean valid() {
        boolean allokay = true;
        String username = phoneemail.getText().toString();
        String pass = password.getText().toString();

        if(username.isEmpty()){
            phoneemail.setError("Can't be empty");
            allokay = false;
        }
        if(pass.isEmpty()){
            password.setError("Can't be empty");
            allokay =  false;
        }
        return  allokay;
    }
}
