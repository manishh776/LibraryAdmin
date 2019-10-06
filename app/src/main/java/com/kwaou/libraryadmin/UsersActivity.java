package com.kwaou.libraryadmin;

import android.app.ProgressDialog;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kwaou.libraryadmin.adapters.UserAdapter;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.models.User;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    ImageView back;
    RecyclerView recyclerViewUsers;
    ArrayList<User> userArrayList;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        back = findViewById(R.id.back);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchUsers();
    }

    private void fetchUsers() {
        progressDialog.show();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_USERS);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArrayList = new ArrayList<>();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if(user!=null)
                        userArrayList.add(user);
                }
                UserAdapter adapter = new UserAdapter(UsersActivity.this, userArrayList);
                recyclerViewUsers.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
