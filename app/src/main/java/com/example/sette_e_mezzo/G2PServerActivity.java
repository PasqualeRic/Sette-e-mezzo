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
    TextView tvResult;
    //String[] idClients;

    // DELAER
    ImageView ivMyFirstCard;
    Button btnCarta, btnStai;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapter myCardAdapter;
    Double myScore;
    Card firstCard;

    // PLAYER
    ImageView ivFirstPlayer;
    TextView tvScorePlayer;
    RecyclerView rvPlayer;
    CardAdapter cardAdapterPlayer;
    ArrayList<Card> playerCards;
    Double scorePlayer;
    String idFirstCardPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g2p);

        tvResult = findViewById(R.id.tvResult);
        //idClients = getIntent().getStringArrayExtra("idClients");

        //  DELAER
        btnCarta = findViewById(R.id.btnCarta);
        btnStai = findViewById(R.id.btnStai);
        ivMyFirstCard = findViewById(R.id.ivMyFirstCard);
        firstCard = Deck.getIstance().pull();
        ivMyFirstCard.setImageResource(firstCard.getIdImage());
        tvMyScore = findViewById(R.id.tvMyScore);
        myScore = firstCard.getValue();
        tvMyScore.setText(""+myScore);

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
        LinearLayoutManager lmPlayer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        rvPlayer.setLayoutManager(lmPlayer);

        playerCards = new ArrayList<>();
        cardAdapterPlayer = new CardAdapter(playerCards);
        rvPlayer.setAdapter(cardAdapterPlayer);

        socket.getSocket().on("requestCard",args -> {
            Log.d("debug","requestCard");
            Log.d("debug",args[0].toString());

            Card card = Deck.getIstance().pull();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("idClient",args[0].toString());
                jsonObject.put("card",card.toJSON());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.getSocket().emit("sendCard",jsonObject,(Ack) args1 -> {});

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playerCards.add(card);
                    cardAdapterPlayer.notifyItemInserted(playerCards.size()-1);

                }
            });
        });

        socket.getSocket().on("clientTerminate",args -> {
            Log.d("G2PServer","on clientTerminate");
            try {
                JSONObject json = new JSONObject(args[0].toString());
                scorePlayer = json.getDouble("score");
                idFirstCardPlayer = json.getString("idFirstCard");
                Log.d("G2PServer","ricevo come idFirstCardPlayer"+idFirstCardPlayer);
            }catch(Exception e){}
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(scorePlayer>7.5){
                        tvResult.setText(R.string.win);
                        ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());
                        JSONObject json = new JSONObject();
                        try {
                            json.put("idFirstCard",firstCard.getId());
                            json.put("score",myScore);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());
                        socket.getSocket().emit("closeRound",json,(Ack) args->{});
                    }else{
                        btnCarta.setVisibility(View.VISIBLE);
                        btnStai.setVisibility(View.VISIBLE);
                    }
                }
            });

        });

        btnCarta.setOnClickListener(v -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Card card = Deck.getIstance().pull();
                    myCards.add(card);
                    myCardAdapter.notifyItemInserted(myCards.size()-1);
                    myScore += card.getValue();
                    tvMyScore.setText(""+myScore);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("idClient",socket.getId());
                        jsonObject.put("card",card.toJSON());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.getSocket().emit("sendCard",jsonObject,(Ack) args1 -> {});

                    if(myScore>=7.5){
                        tvScorePlayer.setText(""+scorePlayer);
                        btnCarta.setVisibility(View.INVISIBLE);
                        btnStai.setVisibility(View.INVISIBLE);

                        if(myScore==7.5){
                            tvResult.setText(R.string.win);
                        }else{
                            tvResult.setText(R.string.lose);
                        }
                        JSONObject json = new JSONObject();
                        try {
                            json.put("idFirstCard",firstCard.getId());
                            json.put("score",myScore);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("G2PServer",idFirstCardPlayer);
                        ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());
                        socket.getSocket().emit("closeRound",json,(Ack) args->{});
                    }
                }
            });
        });

        btnStai.setOnClickListener(v -> {
            Log.d("G2PServer","stai");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvScorePlayer.setText(""+scorePlayer);
                    btnCarta.setVisibility(View.INVISIBLE);
                    btnStai.setVisibility(View.INVISIBLE);
                    Log.d("G2PServer","tasti tolti");
                    ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());
                    Log.d("G2PServer","carta stampata");
                    if(myScore>=scorePlayer){
                        tvResult.setText(R.string.win);
                    }else{
                        tvResult.setText(R.string.lose);
                    }
                    Log.d("G2PServer","risultato stampato");
                    JSONObject json = new JSONObject();
                    try {
                        json.put("idFirstCard",firstCard.getId());
                        json.put("score",myScore);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());
                    socket.getSocket().emit("closeRound",json,(Ack) args->{});
                }
            });
        });

    }
}