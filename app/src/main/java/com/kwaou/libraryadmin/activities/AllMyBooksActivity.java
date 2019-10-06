package com.kwaou.libraryadmin.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.adapters.BookPackageAdapter;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.models.Book;
import com.kwaou.libraryadmin.models.BookPackage;
import com.kwaou.libraryadmin.sqlite.KeyValueDb;

import java.util.ArrayList;

public class AllMyBooksActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<BookPackage> bookArrayList;
    private ImageView back;
    private TextView txtinfo;
    private RecyclerView recyclerViewBooks;
    private ProgressDialog progressDialog;
    public static Book newbook;
    public EditText search;
    private BookPackageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_my_books);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        bookArrayList = new ArrayList<>();

        search = findViewById(R.id.search);
        back = findViewById(R.id.back);
        txtinfo = findViewById(R.id.txtinfo);
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        recyclerViewBooks.setLayoutManager(new GridLayoutManager(this, 3));

        back.setOnClickListener(this);

        newbook = (Book) getIntent().getSerializableExtra("new");
        if (newbook != null)
            txtinfo.setText("Pick a book to exchange with");

        fetchBooks();

        handleSearch();

    }

    private void handleSearch() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void fetchBooks() {
        progressDialog.show();

        final String userid = KeyValueDb.get(this, Config.USERID, "");
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_BOOKPACKAGES);
        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookArrayList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    BookPackage bookPackage = ds.getValue(BookPackage.class);

                    if (bookPackage != null)
                        bookArrayList.add(bookPackage);
                }
                adapter = new BookPackageAdapter(AllMyBooksActivity.this, bookArrayList);
                recyclerViewBooks.setAdapter(adapter);
                if (bookArrayList.isEmpty()) {
                    findViewById(R.id.nobooks).setVisibility(View.VISIBLE);
                    recyclerViewBooks.setVisibility(View.GONE);
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
        if (view == back) {
            finish();
        }
    }
}
