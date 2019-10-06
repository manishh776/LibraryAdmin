package com.kwaou.libraryadmin.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.models.BookPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookPackageAdapter extends RecyclerView.Adapter<BookPackageAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BookPackage> bookPackageArrayList;
    private ArrayList<BookPackage> bookArrayListUncleared, unchangedBooklist;
    private String TAG = BookPackageAdapter.class.getSimpleName();
    public  static int PICK_BOOK_REQUEST_CODE = 100;
    private ProgressDialog progressDialog;

    public BookPackageAdapter(Context context, ArrayList<BookPackage> bookPackages){
        this.context = context;
        this.bookPackageArrayList = bookPackages;
        unchangedBooklist = new ArrayList<>();
        unchangedBooklist.addAll(bookPackageArrayList);
        bookArrayListUncleared =  new ArrayList<>();
        bookArrayListUncleared.addAll(unchangedBooklist);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_package_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            BookPackage book = bookPackageArrayList.get(i);
            if(book.getPrice() > 0){
                viewHolder.linearLayoutPrice.setVisibility(View.VISIBLE);
                viewHolder.price.setText(book.getPrice()+"");
            }
            viewHolder.noofbooks.setText(book.getBookArrayList().size() + "");

        Glide.with(context).load(book.getBookArrayList().get(0).getPicUrl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return bookPackageArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView price, noofbooks;
        LinearLayout linearLayoutPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            price = itemView.findViewById(R.id.price);
            noofbooks = itemView.findViewById(R.id.noofbooks);
            linearLayoutPrice = itemView.findViewById(R.id.priceLinear);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BookPackage book = bookPackageArrayList.get(getAdapterPosition());
                }
            });
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        bookPackageArrayList.clear();

        if (charText.length() == 0) {
            bookPackageArrayList.addAll(bookArrayListUncleared);
        }
        else {

            for (BookPackage  book : bookArrayListUncleared) {

                if ((book.getBookArrayList().get(0).getTitle() +book.getBookArrayList().get(0).getDesc()).toLowerCase(Locale.getDefault()).contains(charText)) {

                    bookPackageArrayList.add(book);
                }
            }
        }
        notifyDataSetChanged();
    }


}
