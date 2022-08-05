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
    SocketClass socket = new SocketClass();
    Button btnDealer, btnPlayer;
    public String gameId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        socket.connection();

        btnDealer = findViewById(R.id.btnDealer);
        btnDealer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    socket.getSocket().emit("createGame", null, args -> {
                        gameId = (String) args[0];
                    });
                    Intent wait = new Intent(MenuActivity.this, WaitActivity.class);
                    startActivity(wait);
            }
        });
        JSONObject data = new JSONObject();
        try {
            Log.wtf("prova", "prova"+socket.getSocket().id());
            
            data.put("id",socket.getSocket().id());
            data.put("name","pasquale");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btnPlayer = findViewById(R.id.btnPlayer);
        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.getSocket().emit("joinGame", data, (Ack) args -> {
                    Log.wtf("prova", "prova");
                });
            }
        });

    }
}