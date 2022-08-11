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

                socket.getSocket().emit("startGame",socket.getId(), (Ack) args -> {});

                for(int i=0;i<idClients.size();i++){
                    Card card = Deck.getIstance().pull();
                    JSONObject json = new JSONObject();
                    try {
                        json.put("idClient",idClients.get(i));
                        json.put("card",card.toJSON());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.d("debug",json.toString());
                    socket.getSocket().emit("sendFirstCard",json,(Ack) args1 -> {});
                }


                /*
                //Settaggio partita
                for(i<nPlayers){
                    socket.getSocket().emit("sendTo", idPlayer, card ,(Ack) args
                }

                // iterazione - da fare altrove

                socket.getSocket().emit("isYourTurn", idPlayer, card ,(Ack) args
                socket.getSocket().on("mossa", response ,(Ack) args
                if(mossa==carta){
                    socket.getSocket().emit("sendTo", idPlayer, card ,(Ack) args
                    socket.getSocket().emit("sendAll", idPlayer, card ,(Ack) args
                    if(punteggio>=7.5){
                        socket.getSocket().emit("isYourTurn", nextIdPlayer, card ,(Ack) args
                    }
                }else{   //mossa==stai
                    socket.getSocket().emit("isYourTurn", nextIdPlayer, card ,(Ack) args

                }
                socket.getSocket().emit("sendAll", idPlayer, card ,(Ack) args
                */


                Intent i = new Intent(WaitActivity.this, G2PServerActivity.class);
                i.putExtra("idClients",idClients.toArray());
                startActivity(i);

            }
        });

        /*socket.getSocket().on("clientTerminate",args -> {
            Log.d("G2P","activity sbagliata");

        });*/
    }
}