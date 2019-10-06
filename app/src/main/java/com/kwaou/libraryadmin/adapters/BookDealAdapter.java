package com.kwaou.libraryadmin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.models.BookDeal;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookDealAdapter extends RecyclerView.Adapter<BookDealAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BookDeal> bookDeals;
    private String TAG = BookDealAdapter.class.getSimpleName();

    public BookDealAdapter(Context context , ArrayList<BookDeal> bookDeals){
        this.context = context;
        this.bookDeals = bookDeals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.deal_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            BookDeal bookDeal = bookDeals.get(i);
            Log.d(TAG, bookDeal.getLender().getPicUrl());
                if (bookDeal.isForSale()) {
                    holder.forSale.setText("Sale");
                    Glide.with(context).load(bookDeal.getOld().getBookArrayList().get(0).getPicUrl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.book_one_image);
                    Glide.with(context).load(bookDeal.getLender().getPicUrl()).placeholder(R.drawable.ic_users).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.image_owner_one);
                    Glide.with(context).load(bookDeal.getReceiver().getPicUrl()).placeholder(R.drawable.ic_users).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.image_owner_two);

                    holder.owner_name_one.setText("Owned by " + bookDeal.getLender().getName());
                    holder.price_one.setText(bookDeal.getOld().getPrice() + "");
                    holder.noofboks_one.setText(bookDeal.getOld().getBookArrayList().size() + "");

                    holder.book_two.setVisibility(View.GONE);
                    holder.pricelinear_two.setVisibility(View.GONE);
                    holder.owner_name_two.setText("Bought by " + bookDeal.getReceiver().getName());
                } else {
                    holder.forSale.setText("Exchange");
                    holder.priceLinear_one.setVisibility(View.GONE);
                    holder.pricelinear_two.setVisibility(View.GONE);

                    Glide.with(context).load(bookDeal.getOld().getBookArrayList().get(0).getPicUrl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.book_one_image);
                    Glide.with(context).load(bookDeal.getNewbook().getBookArrayList().get(0).getPicUrl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.book_two_image);
                    Glide.with(context).load(bookDeal.getLender().getPicUrl()).placeholder(R.drawable.ic_users).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.image_owner_one);
                    Glide.with(context).load(bookDeal.getReceiver().getPicUrl()).placeholder(R.drawable.ic_users).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.image_owner_two);

                    holder.owner_name_one.setText(bookDeal.getLender().getName());
                    holder.owner_name_two.setText(bookDeal.getReceiver().getName());

                    holder.price_one.setText(bookDeal.getNewbook().getPrice() + "");
                    holder.price_two.setText(bookDeal.getOld().getPrice() + "");

                    holder.noofboks_one.setText(bookDeal.getNewbook().getBookArrayList().size() + "");
                    holder.noofbooks_two.setText(bookDeal.getOld().getBookArrayList().size() + "");

                }

    }

    @Override
    public int getItemCount() {
        return bookDeals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView  book_one_image, book_two_image;
        CircleImageView image_owner_one, image_owner_two;
        TextView owner_name_one, owner_name_two;
        TextView price_one, price_two, noofboks_one, noofbooks_two;
        LinearLayout priceLinear_one, pricelinear_two;
        LinearLayout item_one, item_two, book_two;
        TextView forSale;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            forSale = itemView.findViewById(R.id.forSale);

            book_one_image = itemView.findViewById(R.id.image_one);
            book_two_image = itemView.findViewById(R.id.image_two);

            image_owner_one = itemView.findViewById(R.id.owner_image_one);
            image_owner_two = itemView.findViewById(R.id.owner_image_two);

            owner_name_one = itemView.findViewById(R.id.owner_name_one);
            owner_name_two = itemView.findViewById(R.id.owner_name_two);

            price_one = itemView.findViewById(R.id.price_one);
            price_two = itemView.findViewById(R.id.price_two);

            noofboks_one = itemView.findViewById(R.id.noofbooks_one);
            noofbooks_two = itemView.findViewById(R.id.noofbooks_two);

            priceLinear_one = itemView.findViewById(R.id.priceLinear_one);
            pricelinear_two = itemView.findViewById(R.id.priceLinear_two);

            item_one = itemView.findViewById(R.id.item_one);
            item_two = itemView.findViewById(R.id.item_two);

            book_two = itemView.findViewById(R.id.book_two);

        }
    }
}
