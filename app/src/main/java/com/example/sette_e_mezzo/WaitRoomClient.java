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
            Log.wtf("k", "k"+args[0].toString());
           /* runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.wtf("p","p"+args[0].toString());
                    Log.wtf("p","p"+args[1].toString());
                    if(args[1].toString().equals("2")) {
                        Intent i = new Intent(WaitRoomClient.this, G2PClientActivity.class);
                        i.putExtra("idServer", args[0].toString());
                        startActivity(i);
                    }
                    else if(args[1].toString().equals("3")){
                        Intent i = new Intent(WaitRoomClient.this, G3PClientActivity.class);
                        i.putExtra("idServer", args[0].toString());
                        startActivity(i);
                    }

                }
            });*/
        });

    }
}