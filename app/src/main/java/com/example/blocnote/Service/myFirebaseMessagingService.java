package com.example.blocnote.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.example.blocnote.ProfileActivity;
import com.example.blocnote.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class myFirebaseMessagingService extends FirebaseMessagingService {
    private static final String IST_CANAL = "IST_CANAL";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putString("Token_device", s).apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        int notificationId = 887;

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, IST_CANAL)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)  //a resource for your custom small icon
                .setContentTitle(remoteMessage.getNotification().getTitle()) //the "title" value you sent in your notification
                .setContentText(remoteMessage.getNotification().getBody()) //ditto
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)  //dismisses the notification on click
                .setSound(defaultSoundUri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
            String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

            NotificationChannel adminChannel;
            adminChannel = new NotificationChannel(IST_CANAL, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
            adminChannel.setDescription(adminChannelDescription);
            adminChannel.enableLights(true);
            adminChannel.setLightColor(Color.RED);
            adminChannel.enableVibration(true);

                notificationManager.createNotificationChannel(adminChannel);


        }
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }
}

