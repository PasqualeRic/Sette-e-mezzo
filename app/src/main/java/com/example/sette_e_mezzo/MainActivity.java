package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        socket.connection();

        Button btnGame = findViewById(R.id.btnGame);
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menu = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(menu);
            }
        });

        Button btnNotify = findViewById(R.id.btnNotify);

        btnNotify.setOnClickListener(view -> {

            Intent i = new Intent(MainActivity.this,MainActivity.class);
            PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, i,PendingIntent.FLAG_IMMUTABLE);

            Notification nBuilder = new NotificationCompat.Builder(this,"CHANNEL_ID")
                    .setSmallIcon(android.R.drawable.star_on)
                        .setContentTitle(" TITOLO ")
                    .setContentText(" TESTO ")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pending)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("CHANNEL_ID",
                        "channel_name",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("channel_description");
                notificationManager.createNotificationChannel(channel);
            }


            notificationManager.notify(1, nBuilder);

        });

    }
}