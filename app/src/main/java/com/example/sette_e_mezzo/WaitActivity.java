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
import android.widget.Toast;

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
        nPlayers = Integer.parseInt(getIntent().getStringExtra(Utils.nplayers));

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

        socket.getSocket().on(Utils.invioPlayer, args -> {

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
                if(conta>1) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put(Utils.nplayers, idClients.size() + 1);
                        obj.put(Utils.idServer, socket.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.getSocket().emit(Utils.startGame, obj, (Ack) args -> {
                    });

                    JSONArray json = new JSONArray();

                    for (int i = 0; i < idClients.size(); i++) {
                        Card card = Deck.getIstance().pull();
                        JSONObject client = new JSONObject();
                        try {
                            client.put(Utils.idClient, idClients.get(i));
                            client.put(Utils.name, usernameClients.get(i));
                            client.put(Utils.card, card.toJSON());
                            json.put(client);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    socket.getSocket().emit(Utils.sendFirstCard, json, (Ack) args1 -> {
                    });

                    Intent i = null;
                    if (idClients.size() + 1 == 2) {
                        i = new Intent(WaitActivity.this, G2PServerActivity.class);
                    } else if (idClients.size() + 1 == 3) {
                        i = new Intent(WaitActivity.this, G3PServerActivity.class);
                    } else if (idClients.size() + 1 == 4) {
                        i = new Intent(WaitActivity.this, G4PServerActivity.class);
                    }

                    i.putExtra(Utils.idClients, idClients);
                    i.putExtra(Utils.names, usernameClients);
                    i.putExtra(Utils.idCard, Deck.getIstance().pull().getId());
                    i.putExtra(Utils.idGame, getIntent().getStringExtra(Utils.idGame));
                    startActivity(i);

                    if (idClients.size() > 1)
                        socket.getSocket().emit(Utils.isYourTurn, idClients.get(0));
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.toastStartGame), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }
}