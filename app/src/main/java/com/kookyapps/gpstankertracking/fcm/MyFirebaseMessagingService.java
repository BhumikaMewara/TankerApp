package com.kookyapps.gpstankertracking.fcm;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import com.kookyapps.gpstankertracking.Activity.Notifications;
import com.kookyapps.gpstankertracking.Activity.RequestDetails;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    String timestamp;
    private static final int REQUEST_CODE = 1;
    private static final int NOTIFICATION_ID = 6578;
    private NotificationUtilsFcm notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Data : " + remoteMessage.getData().toString());

        //  timestamp = Calendar.getInstance().getTime();

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            //handleNotification(remoteMessage.getNotification().getBody());
            try {
                Map<String, String> s = remoteMessage.getData();
                //String txn_id = s.get("");
                Log.e(TAG, "string: " + s.toString());
                JSONObject json = new JSONObject(s);
                //JSONObject data = json.getJSONObject("message");
                //showNotifications(data);
                //Log.e("data123",data.toString());
                //Log.e(TAG, "Data Payload: " + data.toString());
                showNotifications("Seremo",json);
                //handleDataMessage(data);
            } catch (Exception e) {
                Log.e(TAG, "Exception at Ini: " + e.getMessage());
                e.printStackTrace();
            }


        }

        // Check if message contains a data payload.
        /*if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                JSONObject data = json.getJSONObject("message");
                handleDataMessage(data);
            } catch (Exception e) {
                Log.e(TAG, "Exception at Ini: " + e.getMessage());
            }
        }*/
    }

    private void handleNotification(String message) {
        if (!NotificationUtilsFcm.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,200);
            // play notification sound
            NotificationUtilsFcm notificationUtils = new NotificationUtilsFcm(getApplicationContext());
            notificationUtils.playNotificationSound();
            /*showNotifications("Seremo",message);
            showNotifications();*/
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            //  JSONObject data = json.getJSONObject("data");

            String course_id = json.getString("course_id");
            String type = json.getString("type");
            String message = json.getString("message");

            String imageUrl = "";

            timestamp = DateFormat.getDateTimeInstance().format(new Date());


            Log.e(TAG, "course_id: " + course_id);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "type : " + type);


            if (!NotificationUtilsFcm.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,200);
            } else {
                // app is in background, show the notification in notification tray

                Intent resultIntent = new Intent(getApplicationContext(), Notifications.class);

                resultIntent.putExtra("message", message);
                resultIntent.putExtra("course_id", course_id);
                resultIntent.putExtra("type", type);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), "Water Tanker", message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), course_id, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /*
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent)
    {
        notificationUtils = new NotificationUtilsFcm(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /*
     * Showing notification with text and image
     */

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp
            , Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtilsFcm(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    private void showNotifications(String title, JSONObject json) {
        //Intent i = new Intent(this, FirstActivity.class);
        //FirstActivity.newNotification();


        try {
            String txn_id = json.getString("transaction_id");
            String not_id = json.getString("notification_id");
            String message = json.getString("message");

            SharedPrefUtil.setPreferences(getApplicationContext(), Constants.SHARED_PREF_NOTICATION_TAG, Constants.SHARED_NOTIFICATION_UPDATE_KEY, "yes");
            if (!NotificationUtilsFcm.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            }
            timestamp = DateFormat.getDateTimeInstance().format(new Date());
            //PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE,
            //      i, PendingIntent.FLAG_UPDATE_CURRENT);
            final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String NOTIFICATION_CHANNEL_ID = "101";
            Intent intent = new Intent(this, RequestDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Bundle b = new Bundle();
            b.putString("start_from", "notification");
            b.putString("transaction_id", txn_id);
            b.putString("notification_id", not_id);
            b.putString("ispush","1");
            intent.putExtras(b);
            //PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, 0);

            final PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            REQUEST_CODE,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Seremo";
                String description = "Seremo Notifications";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.setVibrationPattern(new long[]{0, 1000, 500});
                channel.enableVibration(true);
                channel.enableLights(true);
                channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(getResources().getColor(R.color.colorblack))
                    .setColorized(true)
                    .setSound(alarmSound)
                    .setVibrate(new long[]{0, 1000, 500})
                    //.setWhen(getTimeMilliSec(timestamp))
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            manager.notify(NOTIFICATION_ID, builder.build());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}