package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    static final int ALARM_REQ_CODE = 100;
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("NOTIFICA","ONDESTROY");
        Intent i = new Intent(MainActivity.this,MyReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this, ALARM_REQ_CODE, i,PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,1000*3,pending);
        Log.d("NOTIFICA","Alarm settato");
    }
}