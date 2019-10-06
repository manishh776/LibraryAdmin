package com.kwaou.libraryadmin.activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.adapters.BookDealAdapter;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.models.BookDeal;

import java.util.ArrayList;

public class BookDealsActivity extends AppCompatActivity {

    ImageView back;
    RecyclerView recyclerViewDeals;
    ArrayList<BookDeal> bookDeals;
    ProgressDialog progressDialog;
    public  static boolean forSale = true;
    TextView txtOrders;
    boolean payment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_deals);

        txtOrders = findViewById(R.id.txtorders);
        forSale = getIntent().getBooleanExtra("forSale",true);
        if(!forSale){
            txtOrders.setText("Exchanges");
        }

        payment = getIntent().getBooleanExtra("payment",false);

        if(payment){
            txtOrders.setText("Payments");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        back = findViewById(R.id.back);
        recyclerViewDeals = findViewById(R.id.recyclerViewDeals);
        recyclerViewDeals.setLayoutManager(new LinearLayoutManager(this));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchDeals();
    }

    private void fetchDeals() {
        progressDialog.show();
        DatabaseReference dealsRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_BOOK_DEALS);

        dealsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookDeals = new ArrayList<>();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    BookDeal deal = ds.getValue(BookDeal.class);
                    if(deal!=null){
                        if(!forSale){
                            if(!deal.isForSale())
                                bookDeals.add(deal);
                        }
                        else if(payment){
                            if(deal.isForSale())
                                bookDeals.add(deal);
                        }
                        else{
                            bookDeals.add(deal);
                        }
                    }

                }
                BookDealAdapter dealAdapter = new BookDealAdapter(BookDealsActivity.this, bookDeals);
                recyclerViewDeals.setAdapter(dealAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
