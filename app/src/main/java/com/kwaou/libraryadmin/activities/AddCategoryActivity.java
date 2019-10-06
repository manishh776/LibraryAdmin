package com.kwaou.libraryadmin.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.helper.ImagePicker;
import com.kwaou.libraryadmin.models.Category;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AddCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ACCESS_STORAGE_PERMISSIONS_REQUEST = 123;
    private static final int PICK_IMAGE = 111;
    private static final String TAG = AddCategoryActivity.class.getSimpleName();
    ImageView back, pic;
    EditText name;
    Button saveBtn;
    private Uri filePathUri;
    private boolean image_selected = false;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private Category category;
    private boolean toEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        TextView title = findViewById(R.id.title);
        storageReference = FirebaseStorage.getInstance().getReference();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToAccessStorage();
        }

        initViews();

        category = (Category) getIntent().getSerializableExtra("category");
        if (category != null) {
            title.setText("Edit Category");
            toEdit = true;
            name.setText(category.getName());
            Glide.with(this).load(category.getPicUrl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(pic);
        }


    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);


        back = findViewById(R.id.back);
        pic = findViewById(R.id.pic);
        name = findViewById(R.id.name);
        saveBtn = findViewById(R.id.saveBtn);


        back.setOnClickListener(this);
        pic.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            finish();
        } else if (view == pic) {
            openGallery();
        } else if (view == saveBtn) {

            if (!toEdit) {
                if (valid())
                    addCategory();
            } else {
                String txt = name.getText().toString();
                if (!txt.isEmpty()) {
                    boolean nameChanged = !txt.toLowerCase().equals(category.getName().toLowerCase());
                    if (nameChanged || image_selected) {
                        updateCategory(nameChanged);
                    } else {
                        Toast.makeText(this, "no changes to save", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Can't be empty", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    private void updateCategory(boolean nameChanged) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_CATEGORIES);
        if(nameChanged){
            category.setName(name.getText().toString());
        }
        if(image_selected){
            uploadImage();
        }else{
            categoryRef.child(category.getId()).setValue(category);
            Toast.makeText(this, "category updated", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void uploadImage() {
        if(!progressDialog.isShowing())
            progressDialog.show();
        Log.d(TAG, filePathUri.getPath());
        final StorageReference booksRef = storageReference.child("books/categories/images" + System.currentTimeMillis());
        booksRef.putFile(filePathUri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return booksRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    String url = String.valueOf(task.getResult());
                    DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_CATEGORIES);
                    if(!toEdit) {
                        String id = categoryRef.push().getKey();
                        String catname = name.getText().toString();

                        Category category = new Category(id, catname, url, 0);
                        categoryRef.child(id).setValue(category);
                        Toast.makeText(AddCategoryActivity.this, "category added", Toast.LENGTH_SHORT).show();
                    }else{
                        category.setPicUrl(url);
                        categoryRef.child(category.getId()).setValue(category);
                        Toast.makeText(AddCategoryActivity.this, "category updated", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }
        });
    }

    private void addCategory() {
        progressDialog.show();
        final String txt = name.getText().toString();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_CATEGORIES);

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Category category = ds.getValue(Category.class);
                    if (category != null && category.getName().toLowerCase().equals(txt.toLowerCase())) {
                        progressDialog.dismiss();
                        Toast.makeText(AddCategoryActivity.this, "category already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                uploadImage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private boolean valid() {
        boolean allOkay = true;

        if (TextUtils.isEmpty(name.getText())) {
            allOkay = false;
            name.setError("Can't be empty");
        }

        if (!image_selected) {
            allOkay = false;
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }

        return allOkay;

    }


    private void openGallery() {
        Intent intent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(intent, PICK_IMAGE);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToAccessStorage() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    ACCESS_STORAGE_PERMISSIONS_REQUEST);


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {

            filePathUri = ImagePicker.getImageFromResult(this, resultCode, data);
            Bitmap bitmapAvatar;
            try {
                bitmapAvatar = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePathUri);
                pic.setImageBitmap(bitmapAvatar);
                image_selected = true;
            } catch (FileNotFoundException e) {
                Drawable drawableAvatar = getResources().getDrawable(R.drawable.ic_image_black_24dp);
                bitmapAvatar = ((BitmapDrawable) drawableAvatar).getBitmap();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
