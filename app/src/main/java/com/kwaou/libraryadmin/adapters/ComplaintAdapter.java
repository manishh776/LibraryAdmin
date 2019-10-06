package com.kwaou.libraryadmin.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.kwaou.libraryadmin.ComplaintActivity;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.activities.CategoryActivity;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.models.Complaint;
import com.kwaou.libraryadmin.models.User;
import com.kwaou.libraryadmin.retrofit.RetrofitClient;
import com.kwaou.libraryadmin.sqlite.KeyValueDb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    Context context;
    ArrayList<Complaint> complaintArrayList;
    String TAG = ComplaintAdapter.class.getSimpleName();

    public ComplaintAdapter(Context context, ArrayList<Complaint> complaintArrayList){
        this.context = context;
        this.complaintArrayList = complaintArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Complaint complaint = complaintArrayList.get(position);

        Glide.with(context).load(complaint.getUser().getPicUrl()).placeholder(R.drawable.ic_users).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.image);

        holder.name.setText(complaint.getUser().getName());
        holder.title.setText(complaint.getTitle());
        holder.desc.setText(complaint.getDesc());
        holder.reply.setText(complaint.getReply().isEmpty()?"Not Replied yet": "Reply:" + complaint.getReply());
    }

    @Override
    public int getItemCount() {
        return complaintArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, reply, replyBtn;
        CircleImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            reply = itemView.findViewById(R.id.reply);
            replyBtn = itemView.findViewById(R.id.replyBtn);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);

            replyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Complaint complaint = complaintArrayList.get(getAdapterPosition());
                    showReplyAlertDialog(complaint);
                }
            });
        }
    }

    private void showReplyAlertDialog(final Complaint complaint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set up the input
        final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               String m_Text = input.getText().toString();
               if(m_Text.isEmpty()){
                   Toast.makeText(context, "Can't be empty", Toast.LENGTH_SHORT).show();
               }else{
                    notifyUser(complaint, m_Text);
               }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    private void notifyUser(Complaint complaint, String m_Text) {
        DatabaseReference comRefs = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_COMPLAINTS);
        comRefs.child(complaint.getId()).child("reply").setValue(m_Text);

        sendPush(complaint, complaint.getUser().getToken());
    }

    private void sendPush(Complaint complaint, String token) {
        Gson gson = new Gson();
        String comstr = gson.toJson(complaint);
        ComplaintActivity.progressDialog.show();
        String type = Config.COMPLAINT_REPLY;

        RetrofitClient.getInstance().getApi().sendPush(token, comstr,type)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            JSONObject obj = new JSONObject(response.body().string());
                            Log.d(TAG, response.body().string());
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                        ComplaintActivity.progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
