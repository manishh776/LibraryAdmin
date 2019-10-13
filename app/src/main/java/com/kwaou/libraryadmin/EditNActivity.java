package com.kwaou.libraryadmin;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.models.Admin;

public class EditNActivity extends AppCompatActivity {

    EditText valueOfn;
    Button buttonUpdate;
    ImageView back;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_n);

        progressDialog = new ProgressDialog(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        valueOfn = findViewById(R.id.valueOfn);
        buttonUpdate = findViewById(R.id.update);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateN();
            }
        });
        fetchN();
    }

    private void fetchN() {
        progressDialog.show();
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_ADMIN);
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                Admin admin = dataSnapshot.getValue(Admin.class);
                valueOfn.setText(admin.getN() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateN() {
        if(TextUtils.isEmpty(valueOfn.getText()) || !TextUtils.isDigitsOnly(valueOfn.getText())){
            valueOfn.setError("Invalid value");
            return;
        }
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_ADMIN);
        adminRef.child("N").setValue(Integer.parseInt(valueOfn.getText().toString()));
        Toast.makeText(this, "Value updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
