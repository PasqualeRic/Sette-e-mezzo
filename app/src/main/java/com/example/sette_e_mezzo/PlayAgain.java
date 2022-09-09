package com.example.sette_e_mezzo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PlayAgain extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context.getApplicationContext(),MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(context.getApplicationContext(), 0, i,PendingIntent.FLAG_IMMUTABLE);

        Notification nBuilder = new NotificationCompat.Builder(context.getApplicationContext(),"CHANNEL_ID")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.textNotification))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pending)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("idChannel",
                    "playAgain",
                    NotificationManager.IMPORTANCE_DEFAULT);
            // channel.setDescription("");
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, nBuilder);
    }


}