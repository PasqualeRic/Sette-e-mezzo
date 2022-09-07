package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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

        // ogni volta che viene aperta l'app viene impostato
        Intent i = new Intent(MainActivity.this, PlayAgain.class);
        PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this, ALARM_REQ_CODE, i,PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //testato con invio della notifica dopo tre secondi, impostato su 2 giorni
        int time = 60 * 60 * 24 * 2;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,1000*3,pending);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}