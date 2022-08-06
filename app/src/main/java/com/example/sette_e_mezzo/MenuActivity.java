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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnDealer = findViewById(R.id.btnDealer);
        btnDealer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    Intent set = new Intent(MenuActivity.this, SetGameActivity.class);
                    startActivity(set);
            }
        });
        btnPlayer = findViewById(R.id.btnPlayer);
        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }
}