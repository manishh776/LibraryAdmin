package com.kwaou.libraryadmin.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.activities.AddCategoryActivity;
import com.kwaou.libraryadmin.activities.CategoryActivity;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.models.Book;
import com.kwaou.libraryadmin.models.Category;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Category> categoryArrayList;

    public CategoryAdapter(Context context, ArrayList<Category> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Category category = categoryArrayList.get(i);
        Glide.with(context).load(category.getPicUrl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(viewHolder.pic);
        viewHolder.name.setText(category.getName());
        viewHolder.bookCount.setText(category.getBookCount() + " Books");
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView pic, edit, delete;
        TextView name, bookCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pic = itemView.findViewById(R.id.pic);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            name = itemView.findViewById(R.id.name);
            bookCount = itemView.findViewById(R.id.bookcount);

            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Category category = categoryArrayList.get(getAdapterPosition());
            if(view == edit){
                Intent intent = new Intent(context, AddCategoryActivity.class);
                intent.putExtra("category", category);
                context.startActivity(intent);
            }
            else if(view == delete){
                showAlertDialog(category);
            }
        }
    }

    private void showAlertDialog(final Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Category " + category.getName());
        builder.setMessage("Are you sure you want to delete this category? All the books under this category will be deleted!");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                deleteCategory(category);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteCategory(final Category category) {
        CategoryActivity.progressDialog.show();
        DatabaseReference cateRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_CATEGORIES);
        cateRef.child(category.getId()).removeValue();

        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_BOOKS);
        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Book book  = ds.getValue(Book.class);
                    if(book!=null && book.getCategory().getId().equals(category.getId())){
                        deleteBook(book);
                    }
                }
                CategoryActivity.progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteBook(Book book) {
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_BOOKS);
        bookRef.child(book.getId()).removeValue();

    }
}
