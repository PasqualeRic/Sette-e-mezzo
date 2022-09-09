package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;

public class MenuActivity extends AppCompatActivity {
    Button btnDealer, btnPlayer;
    SocketClass socket = new SocketClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        socket.connection();
        btnDealer = findViewById(R.id.btnDealer);
        btnPlayer = findViewById(R.id.btnPlayer);

        btnDealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent set = new Intent(MenuActivity.this, SetGameActivity.class);
                    startActivity(set);
            }
        });

        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent player = new Intent(MenuActivity.this, PlayerActivity.class);
                startActivity(player);
            }
        });

    }
}