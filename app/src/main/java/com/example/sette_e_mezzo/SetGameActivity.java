package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;

public class SetGameActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    Button btnPlay;
    public String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_game);

        if(socket.getSocket().connected())
        {
            socket.getSocket().emit("createGame", null, args -> {
                this.id = (String) args[0];
                Log.wtf("pr", "pr"+id);
            });
        }
        btnPlay = findViewById(R.id.button);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject item = new JSONObject();
                try {
                    item.put("id", id);
                    item.put("name", "prova");
                    item.put("numberOfPlayers", 4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (socket.getSocket().connected())
                    socket.getSocket().emit("confGame", item, (Ack) args -> {
                        JSONObject response = (JSONObject) args[0];
                        Log.wtf("pr", "pr"+response);
                    });

                    }
        });

    }
}