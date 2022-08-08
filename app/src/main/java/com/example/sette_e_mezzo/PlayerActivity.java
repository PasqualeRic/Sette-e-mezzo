package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;

public class PlayerActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    Button btnJoin;
    EditText player;
    Spinner n;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnJoin = findViewById(R.id.button2);
        n = findViewById(R.id.player);
        player = findViewById(R.id.name);




        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject item = new JSONObject();
                try {
                    item.put("id", socket.getSocket().id());
                    item.put("numberOfPlayers", n.getSelectedItem().toString());
                    item.put("name", player.getText().toString());
                    socket.getSocket().emit("joinGame",item, (Ack) args ->{

                    });
                    Intent i = new Intent(PlayerActivity.this, WaitRoomClient.class);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}