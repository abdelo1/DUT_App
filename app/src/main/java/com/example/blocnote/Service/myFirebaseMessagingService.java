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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.blocnote.MessageActivity;
import com.example.blocnote.NotifActivity;
import com.example.blocnote.ProfileActivity;
import com.example.blocnote.R;
import com.example.blocnote.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class myFirebaseMessagingService extends FirebaseMessagingService {
    private static final String IST_CANAL = "IST_CANAL";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
       FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
       if(user!=null)
           updateToken(s);
    }

    private void updateToken(String s) {
       FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference  ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(s);
        ref.child(user.getUid()).setValue(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        int notificationId = 887;
        if (remoteMessage.getNotification()!=null) {

                Intent intent = new Intent(this, NotifActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        else
        {
            String sent=remoteMessage.getData().get("sent");
            String user =remoteMessage.getData().get("user");
            FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();

            if(fuser!=null && sent.equals(fuser.getUid())){
                String muser=remoteMessage.getData().get("user");
                String icon=remoteMessage.getData().get("icon");
                String title=remoteMessage.getData().get("title");
                String body=remoteMessage.getData().get("body");

                RemoteMessage.Notification notification=remoteMessage.getNotification();
                int i=Integer.parseInt(user.replaceAll("[\\D]",""));
                Intent intent =new Intent(this, MessageActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("receiverId",muser);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent=PendingIntent.getActivity(this,i, intent,PendingIntent.FLAG_ONE_SHOT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, IST_CANAL)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)  //a resource for your custom small icon
                        .setContentTitle(title) //the "title" value you sent in your notification
                        .setContentText(body) //ditto
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)  //dismisses the notification on click
                        .setSound(defaultSoundUri);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int j=0;
                if(i>0)
                  j=i;
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
                notificationManager.notify(j /* ID of notification */, notificationBuilder.build());

            }
        }
    }


}

