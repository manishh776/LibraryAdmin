package com.kwaou.libraryadmin.firebase;

/**
 * Created by Manish on 11/22/2016.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.kwaou.libraryadmin.R;
import com.kwaou.libraryadmin.activities.MainActivity;
import com.kwaou.libraryadmin.helper.Config;
import com.kwaou.libraryadmin.models.BookPackage;
import com.kwaou.libraryadmin.models.User;
import com.kwaou.libraryadmin.sqlite.KeyValueDb;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private int notification_id = 0, message_id = 0;
    private String CHANNEL_ID = "borrow";
    String TYPE;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        KeyValueDb.set(getApplicationContext(), Config.USER_TOKEN,s,1);
        Log.d("Refreshed Token", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("onMessageReceived","Check");
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                Log.d("Try","Just got into try");
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                parseNotificationData(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }


    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void parseNotificationData(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");
            JSONObject payload = data.getJSONObject("payload");

            String message = payload.getString("message");
            String type = payload.getString("type");

            String title = "";
            switch (type){
                case Config.SALE:
                    TYPE = Config.SALE;
                    title = "Book Sold. Payment Received";
                    break;
                case Config.PURCHASED_BOOK_RECEIVED:
                    TYPE = Config.PURCHASED_BOOK_RECEIVED;
                    title = "Book has been received by the buyer.";
                    break;
                case Config.COMPLAINT:
                    TYPE = Config.COMPLAINT;
                    title = "Complaint Received";
                    break;
            }


            showNotification(title, message);

        }
        catch (Exception e) {
            Log.e("Exception",e.getMessage());
        }
    }

    private void showNotification(String title, String message) throws JSONException {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);

        Gson gson =new Gson();
        User from = null;
            JSONObject jsonObject = new JSONObject(message);
            if(TYPE.equals(Config.SALE)) {
                JSONObject fromUser = jsonObject.getJSONObject("from");
                JSONObject old = jsonObject.getJSONObject("old");
                Log.d(TAG, old.toString());
                BookPackage oldBook = gson.fromJson(old.toString(), BookPackage.class);
                from = gson.fromJson(fromUser.toString(), User.class);
                message = "A set of " + oldBook.getBookArrayList().size() + " books has been bought by" +
                        " " + from.getName() + ". Please make payment to the concerned owner";
            }else if(TYPE.equals(Config.PURCHASED_BOOK_RECEIVED)){
                JSONObject old = jsonObject.getJSONObject("old");
                BookPackage oldBook = gson.fromJson(old.toString(), BookPackage.class);
                message = oldBook.getBookArrayList().get(0).getTitle() + " named book has been received by the buyer";
            }
            else{
                JSONObject fromUser = jsonObject.getJSONObject("from");
                from = gson.fromJson(fromUser.toString(), User.class);
                message = from. getName() + " has registered a new complaint. Please Check it out";
            }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setShowWhen(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notification_id, mBuilder.build());
    }




}

