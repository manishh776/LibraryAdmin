package com.kwaou.libraryadmin.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.adapters.CategoryAdapter;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.models.Category;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton fab;
    public  static ProgressDialog progressDialog;
    ArrayList<Category> categoryArrayList;
    RecyclerView recyclerViewCategories;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);



        back = findViewById(R.id.back);
        recyclerViewCategories = findViewById(R.id.categories);
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewCategories.setHasFixedSize(true);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        back.setOnClickListener(this);

        fetchCategories();

    }

    private void fetchCategories() {
        progressDialog.show();

        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_CATEGORIES);
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    categoryArrayList = new ArrayList<>();
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Category category = ds.getValue(Category.class);
                        if(category!=null)
                            categoryArrayList.add(category);
                    }
                progressDialog.dismiss();
                CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this, categoryArrayList);
                recyclerViewCategories.setAdapter(adapter);
                if(categoryArrayList.isEmpty())
                    findViewById(R.id.textviewnocontent).setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == fab){
            startActivity(new Intent(this, AddCategoryActivity.class));
        }
        else if(view == back){
            finish();
        }
    }
}
