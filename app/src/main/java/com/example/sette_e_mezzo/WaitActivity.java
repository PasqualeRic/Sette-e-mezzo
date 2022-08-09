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

import java.util.ArrayList;

import io.socket.client.Ack;

public class WaitActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    private ProgressBar spinner;
    int nPlayers;

    String name, number;
    TextView text1, text2, n, text3, text4, tvRoomFull,tvWaitPlayers;
    ProgressBar pbWaitClient;
    RecyclerView rvClientes;
    RecyclerView.LayoutManager layoutManager;
    ClientAdapter clientAdapter;
    ArrayList<String> usernameClients;
    Button start;
    Integer conta = 1;
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

        n = findViewById(R.id.set);
        text2 = findViewById(R.id.tvWaitPlayers);
        text4 = findViewById(R.id.numberPlayers);
        spinner = (ProgressBar)findViewById(R.id.pbWaitClient);
        spinner.setVisibility(View.VISIBLE);
        socket.getSocket().on("invioPlayer", args -> {
               // Log.wtf("p","p"+args[0]);
                name = args[0].toString();
                number = args[1].toString();
                conta = conta + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        usernameClients.add(args[0].toString());
                        clientAdapter.notifyItemInserted(usernameClients.size()-1);

                        if(nPlayers==usernameClients.size()+1){
                            tvRoomFull.setVisibility(View.VISIBLE);
                            pbWaitClient.setVisibility(View.INVISIBLE);
                            tvWaitPlayers.setVisibility(View.INVISIBLE);
                        }

                        /*text1.setText(name);
                        text4.setText("/"+number);
                        n.setText(conta.toString());
                        Log.wtf("n", "number"+number);
                        Log.wtf("n", "conta"+conta);*/


                    }
                });

            });

        start = findViewById(R.id.btnStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.getSocket().emit("startGame",(Ack) args -> {

                });
                Intent i = new Intent(WaitActivity.this, Game2PlayersActivity.class);
                startActivity(i);
            }
        });
    }
}