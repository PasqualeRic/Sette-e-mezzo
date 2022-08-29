package com.example.sette_e_mezzo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("NOTIFICA","BR - onReceiver");
        Intent i = new Intent(context.getApplicationContext(),MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(context.getApplicationContext(), 0, i,PendingIntent.FLAG_IMMUTABLE);

        Notification nBuilder = new NotificationCompat.Builder(context.getApplicationContext(),"CHANNEL_ID")
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle(" TITOLO ")
                .setContentText(" TESTO ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pending)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID",
                    "channel_name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("channel_description");
            notificationManager.createNotificationChannel(channel);
        }

        Log.d("NOTIFICA","ora mando la notifica");
        notificationManager.notify(1, nBuilder);
    }
}