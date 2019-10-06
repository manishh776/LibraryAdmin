package com.kwaou.libraryadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.models.Book;


import java.util.ArrayList;
import java.util.Locale;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Book> bookArrayList;
    private ArrayList<Book> bookArrayListUncleared, unchangedBooklist;
    private String TAG = BookAdapter.class.getSimpleName();
    public  static int PICK_BOOK_REQUEST_CODE = 100;

    public BookAdapter(Context context, ArrayList<Book> bookArrayList){
        this.context = context;
        this.bookArrayList = bookArrayList;
        unchangedBooklist = new ArrayList<>();
        unchangedBooklist.addAll(bookArrayList);
        bookArrayListUncleared =  new ArrayList<>();
        bookArrayListUncleared.addAll(unchangedBooklist);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.books_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            Book book = bookArrayList.get(i);
            if(book.getPrice() > 0)
                viewHolder.price.setVisibility(View.VISIBLE);
        Glide.with(context).load(book.getPicUrl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            price = itemView.findViewById(R.id.price);

        }
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        bookArrayList.clear();

        if (charText.length() == 0) {
            bookArrayList.addAll(bookArrayListUncleared);
            Log.d(TAG,"item added" + bookArrayList.get(0).getTitle());
        }
        else {

            for (Book  book : bookArrayListUncleared) {

                if ((book.getTitle() +book.getDesc()).toLowerCase(Locale.getDefault()).contains(charText)) {

                    bookArrayList.add(book);
                }
            }
        }
        notifyDataSetChanged();
    }


}
