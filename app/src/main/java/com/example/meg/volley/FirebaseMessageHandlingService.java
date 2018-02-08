package com.example.meg.volley;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by meg on 14/11/17.
 */

public class FirebaseMessageHandlingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d("tag", "From: " + remoteMessage.getFrom());
        Log.d("tag", "Notification Message Body: " + remoteMessage.getData().get("message"));

        String tittleMessage = "DEMO";
        String message = remoteMessage.getData().get("message");

        JSONObject notificationDataJSONObject = new JSONObject(remoteMessage.getData());
        Log.d("fcm ", "Notification Remote Msg: " + notificationDataJSONObject.toString());

        sendNotification(message, tittleMessage);


        sendBroadcastMessage(notificationDataJSONObject);




    }


    private void sendBroadcastMessage(JSONObject message) {
        try {
            Intent intent = new Intent((String) message.get("routing_key"));
            intent.putExtra("message", message.toString());
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String message, String titleMessage) {
        try {
            Intent intent;
            int notificationId = ((int) (Math.random() * (99 - 11))) + 11;


            intent = new Intent(this, MainActivity.class);
            if (message != null) {
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                intent.putExtras(bundle);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);


            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

            notificationBuilder
                    .setContentTitle(titleMessage)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
