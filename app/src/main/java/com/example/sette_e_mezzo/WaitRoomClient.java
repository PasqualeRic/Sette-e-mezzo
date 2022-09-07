package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;

public class WaitRoomClient extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    Integer num;
    String id;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_room_client);
        spinner = findViewById(R.id.progressBar2);
        spinner.setVisibility(View.VISIBLE);

        socket.getSocket().on(Utils.partita, args -> {

            try {
                JSONObject j = new JSONObject(args[0].toString());
                num = j.getInt(Utils.nplayers);
                id = j.getString(Utils.idServer);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (num == 2) {
                Intent i = new Intent(WaitRoomClient.this, G2PClientActivity.class);
                i.putExtra(Utils.idServer, id);
                startActivity(i);
            } else if (num == 3) {
                Intent i = new Intent(WaitRoomClient.this, G3PClientActivity.class);
                i.putExtra(Utils.idServer, id);
                startActivity(i);
            } else if (num == 4) {
                Intent i = new Intent(WaitRoomClient.this, G4PClientActivity.class);
                i.putExtra(Utils.idServer, id);
                startActivity(i);
            }

        });
    }
}