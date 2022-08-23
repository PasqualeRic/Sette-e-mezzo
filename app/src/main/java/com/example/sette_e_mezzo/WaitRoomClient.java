package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONObject;

import io.socket.client.Ack;

public class WaitRoomClient extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_room_client);
        spinner = (ProgressBar)findViewById(R.id.progressBar2);
        spinner.setVisibility(View.VISIBLE);


        socket.getSocket().on("partita", args -> {
            Intent i = new Intent(WaitRoomClient.this, G4PClientActivity.class);
            i.putExtra("idServer",args[0].toString());
            startActivity(i);
        });

    }
}