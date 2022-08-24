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
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_room_client);
        spinner = (ProgressBar) findViewById(R.id.progressBar2);
        spinner.setVisibility(View.VISIBLE);

        socket.getSocket().on("partita", args -> {
            Log.d("args", args[0].toString());
            try {
                JSONObject j = new JSONObject(args[0].toString());
                num = j.getInt("nplayers");
                id = j.getString("idserver");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("numero", num + "");
            if (num == 2) {
                Intent i = new Intent(WaitRoomClient.this, G2PClientActivity.class);
                i.putExtra("idServer", id);
                startActivity(i);
            } else if (num == 3) {
                Intent i = new Intent(WaitRoomClient.this, G3PClientActivity.class);
                i.putExtra("idServer", id);
                startActivity(i);
            }
        });
        /*socket.getSocket().on("reciveYourFirstCard",args -> {
            Log.d("BETA", "reciveYourFirstCard - WaitRoomClient");
        });*/
    }
}