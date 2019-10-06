package com.kwaou.libraryadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kwaou.libraryadmin.adapters.ComplaintAdapter;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.models.Complaint;

import java.util.ArrayList;

public class ComplaintActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back;
    RecyclerView recyclerViewComplaint;
    ArrayList<Complaint> complaints;
    public  static ProgressDialog progressDialog;
    TextView nocomplaints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);


        back = findViewById(R.id.back);
        nocomplaints = findViewById(R.id.nocomplaints);
        recyclerViewComplaint = findViewById(R.id.recyclerViewComplaint);
        recyclerViewComplaint.setLayoutManager(new LinearLayoutManager(this));

        back.setOnClickListener(this);

        fetchComplaints();
    }

    private void fetchComplaints() {
        progressDialog.show();
        DatabaseReference comRefs = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_COMPLAINTS);

        comRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaints = new ArrayList<>();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Complaint complaint = ds.getValue(Complaint.class);
                    if(complaint!=null){
                        complaints.add(complaint);
                    }
                }
                ComplaintAdapter adapter = new ComplaintAdapter(ComplaintActivity.this, complaints);
                recyclerViewComplaint.setAdapter(adapter);
                if(complaints.isEmpty()){
                    recyclerViewComplaint.setVisibility(View.GONE);
                    nocomplaints.setVisibility(View.VISIBLE);
                }else{
                    recyclerViewComplaint.setVisibility(View.VISIBLE);
                    nocomplaints.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == back){
            finish();
        }
    }
}
