package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;

public class WaitActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    int nPlayers;

    TextView tvNumPlayers, tvMaxPlayer, tvRoomFull,tvWaitPlayers;
    ProgressBar pbWaitClient;
    RecyclerView rvClientes;
    RecyclerView.LayoutManager layoutManager;
    ClientAdapter clientAdapter;
    ArrayList<String> usernameClients;
    ArrayList<String> idClients;
    Button start;
    Integer conta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);


        tvWaitPlayers = findViewById(R.id.tvWaitPlayers);
        pbWaitClient = findViewById(R.id.pbWaitClient);
        nPlayers = Integer.parseInt(getIntent().getStringExtra("N_PLAYERS"));

        rvClientes = findViewById(R.id.rvClients);
        usernameClients = new ArrayList<>();
        clientAdapter = new ClientAdapter(usernameClients);
        layoutManager = new LinearLayoutManager(this);
        rvClientes.setLayoutManager(layoutManager);
        rvClientes.setAdapter(clientAdapter);

        tvRoomFull = findViewById(R.id.tvRoomFull);

        tvNumPlayers = findViewById(R.id.tvNumPlayers);
        tvMaxPlayer = findViewById(R.id.tvMaxPlayer);

        idClients = new ArrayList<>();
        conta =1;
        tvNumPlayers.setText(""+conta);
        tvMaxPlayer.setText("/"+nPlayers);

        socket.getSocket().on("invioPlayer", args -> {

            idClients.add(args[2].toString());
            conta = conta + 1;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    usernameClients.add(args[0].toString());
                    clientAdapter.notifyItemInserted(usernameClients.size()-1);
                    tvNumPlayers.setText(""+conta);
                    if(nPlayers==usernameClients.size()+1){
                        tvRoomFull.setVisibility(View.VISIBLE);
                        pbWaitClient.setVisibility(View.INVISIBLE);
                        tvWaitPlayers.setVisibility(View.INVISIBLE);
                    }

                }
            });

        });

        start = findViewById(R.id.btnStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject obj = new JSONObject();
                try{
                    obj.put("nplayers",idClients.size()+1);
                    obj.put("idserver",socket.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.getSocket().emit("startGame",obj, (Ack) args -> {});

                JSONArray json = new JSONArray();

                for(int i=0;i<idClients.size();i++){
                    Card card = Deck.getIstance().pull();
                    JSONObject client = new JSONObject();
                    try {
                        client.put("idClient",idClients.get(i));
                        client.put("card",card.toJSON());
                        json.put(client);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                socket.getSocket().emit("sendFirstCard",json,(Ack) args1 -> {});

                if(idClients.size()+1 == 2){
                    Intent i = new Intent(WaitActivity.this, G2PServerActivity.class);
                    i.putExtra("idClients",idClients);
                    startActivity(i);
                }
                else if(idClients.size()+1 == 3){
                    Intent i = new Intent(WaitActivity.this, G3PServerActivity.class);
                    i.putExtra("idClients",idClients);
                    startActivity(i);
                }

            }
        });

        /*socket.getSocket().on("clientTerminate",args -> {
            Log.d("G2P","activity sbagliata");

        });*/
    }
}