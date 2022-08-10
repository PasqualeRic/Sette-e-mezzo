package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import io.socket.client.Ack;

public class G2PServerActivity extends AppCompatActivity {

    SocketClass socket = new SocketClass();
    //String[] idClients;

    // DELAER
    ImageView ivMyFirstCard;
    Button btnCarta, btnStai;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapter myCardAdapter;

    // PLAYER
    ImageView ivFirstPlayer;
    TextView tvScorePlayer;
    RecyclerView rvPlayer;
    CardAdapter cardAdapterPlayer;
    ArrayList<Card> playerCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g2p);

        //idClients = getIntent().getStringArrayExtra("idClients");

        //  DELAER
        btnCarta = findViewById(R.id.btnCarta);
        btnStai = findViewById(R.id.btnStai);
        ivMyFirstCard = findViewById(R.id.ivMyFirstCard);
        Card firstCard = Deck.getIstance().pull();
        ivMyFirstCard.setImageResource(firstCard.getIdImage());
        tvMyScore = findViewById(R.id.tvMyScore);
        tvMyScore.setText(""+firstCard.getValue());

        myRecyclerView = findViewById(R.id.rvMyCards);
        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        myRecyclerView.setLayoutManager(layoutManager);
        myCards = new ArrayList<>();
        myCardAdapter = new CardAdapter(myCards);
        myRecyclerView.setAdapter(myCardAdapter);

        btnCarta.setVisibility(View.INVISIBLE);
        btnStai.setVisibility(View.INVISIBLE);

        // PLAYER
        ivFirstPlayer = findViewById(R.id.ivFirstCardOtherPlayer);
        tvScorePlayer = findViewById(R.id.tvScoreOtherPlayer);
        rvPlayer = findViewById(R.id.rvCardsOtherPlayer);
        LinearLayoutManager lmPlayer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        rvPlayer.setLayoutManager(lmPlayer);

        playerCards = new ArrayList<>();
        cardAdapterPlayer = new CardAdapter(playerCards);
        rvPlayer.setAdapter(cardAdapterPlayer);

        socket.getSocket().on("requestCard",args -> {
            Log.d("debug","requestCard");
            Log.d("debug",args[0].toString());

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("idClient",args[0].toString());
                jsonObject.put("card",Deck.getIstance().pull().toJSON());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.getSocket().emit("sendCard",jsonObject,(Ack) args1 -> {});
        });

        socket.getSocket().on("clientTerminate",args -> {
            Log.d("debug","clientTerminate");

        });

    }
}