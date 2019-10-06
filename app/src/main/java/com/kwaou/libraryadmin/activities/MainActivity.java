package com.kwaou.libraryadmin.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kwaou.libraryadmin.ComplaintActivity;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.UsersActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView category, nocontent, payment, transfer, books, orders;
    TextView users, complaints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        category = findViewById(R.id.textViewCategory);
        nocontent = findViewById(R.id.textviewnocontent);

        orders = findViewById(R.id.orders);
        complaints = findViewById(R.id.complaints);
        payment = findViewById(R.id.payment);
        transfer = findViewById(R.id.transfer);
        books = findViewById(R.id.books);
        users = findViewById(R.id.users);
        complaints = findViewById(R.id.complaints);

        category.setOnClickListener(this);
        payment.setOnClickListener(this);
        transfer.setOnClickListener(this);
        books.setOnClickListener(this);
        orders.setOnClickListener(this);
        users.setOnClickListener(this);
        complaints.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == category){
            startActivity(new Intent(this, CategoryActivity.class));
        }
        else if(view == payment){
            Intent intent = new Intent(this, BookDealsActivity.class);
            intent.putExtra("payment",true);
            startActivity(intent);
        }
        else if(view == transfer){
            Intent intent = new Intent(this, BookDealsActivity.class);
            intent.putExtra("forSale",false);
            startActivity(intent);
        }
        else if(view == books){
            startActivity(new Intent(this, AllMyBooksActivity.class));
        }
        else if(view == orders){
            startActivity(new Intent(this, BookDealsActivity.class));
        }
        else if(view == users){
            startActivity(new Intent(this, UsersActivity.class));
        }
        else if(view == complaints){
            startActivity(new Intent(this, ComplaintActivity.class));
        }
    }
}
