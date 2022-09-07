package com.example.sette_e_mezzo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;

public class G2PServerActivity extends AppCompatActivity {

    SocketClass socket = new SocketClass();
    TextView tvResult;
    Boolean isMyTurn;

    int countClient = 0;
    int countResponse = 1;
    ArrayList<String> idRestartClients, restartNames;

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
    TextView tvNameDealer;

    // PLAYER
    ImageView ivFirstPlayer;
    TextView tvScorePlayer;
    RecyclerView rvPlayer;
    CardAdapter cardAdapterPlayer;
    ArrayList<Card> playerCards;
    Double scorePlayer;
    String idFirstCardPlayer;
    TextView tvNamePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g2p);

        idRestartClients = new ArrayList<>();
        restartNames = new ArrayList<>();


        isMyTurn=false;

        tvResult = findViewById(R.id.tvResult);

        //  DELAER
        btnCarta = findViewById(R.id.btnCarta);
        btnStai = findViewById(R.id.btnStai);
        ivMyFirstCard = findViewById(R.id.ivMyFirstCard);
        firstCard = Deck.getIstance().getCardById(getIntent().getStringExtra(Utils.idCard));
        ivMyFirstCard.setImageResource(firstCard.getIdImage());
        tvMyScore = findViewById(R.id.tvMyScore);
        myScore = firstCard.getValue();
        tvMyScore.setText(""+myScore);
        tvNameDealer = findViewById(R.id.tvMyName);
        tvNameDealer.setText(R.string.dealer);

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
        tvNamePlayer = findViewById(R.id.tvNameOtherPlayer);
        tvNamePlayer.setText(getIntent().getStringArrayListExtra(Utils.names).get(0));
        tvScorePlayer = findViewById(R.id.tvScoreOtherPlayer);
        rvPlayer = findViewById(R.id.rvCardsOtherPlayer);
        LinearLayoutManager lmPlayer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        rvPlayer.setLayoutManager(lmPlayer);

        playerCards = new ArrayList<>();
        cardAdapterPlayer = new CardAdapter(playerCards);
        rvPlayer.setAdapter(cardAdapterPlayer);

        socket.getSocket().on(Utils.requestCard,args -> {

            Card card = Deck.getIstance().pull();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Utils.idClient,args[0].toString());
                jsonObject.put(Utils.card,card.toJSON());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.getSocket().emit(Utils.sendCard,jsonObject,(Ack) args1 -> {});

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playerCards.add(card);
                    cardAdapterPlayer.notifyItemInserted(playerCards.size()-1);

                }
            });
        });

        socket.getSocket().on(Utils.clientTerminate,args -> {

            try {
                JSONObject json = new JSONObject(args[0].toString());
                scorePlayer = json.getDouble(Utils.score);
                idFirstCardPlayer = json.getString(Utils.idFirstCard);
            }catch(Exception e){}
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(scorePlayer>7.5){
                        tvScorePlayer.setText(""+scorePlayer);
                        tvResult.setText(R.string.win);
                        ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());
                        JSONObject json = new JSONObject();
                        try {
                            json.put(Utils.idFirstCard,firstCard.getId());
                            json.put(Utils.score,myScore);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());
                        socket.getSocket().emit(Utils.closeRound,json,(Ack) args->{});
                    }else{
                        isMyTurn=true;
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
                    myCardAdapter.notifyItemInserted(myCards.size() - 1);
                    myScore += card.getValue();
                    tvMyScore.setText("" + myScore);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Utils.idClient, socket.getId());
                        jsonObject.put(Utils.card, card.toJSON());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.getSocket().emit(Utils.sendCard, jsonObject, (Ack) args1 -> {
                    });

                    if (myScore >= 7.5) {
                        tvScorePlayer.setText("" + scorePlayer);
                        isMyTurn = false;
                        btnCarta.setVisibility(View.INVISIBLE);
                        btnStai.setVisibility(View.INVISIBLE);

                        if (myScore == 7.5) {
                            tvResult.setText(R.string.win);
                        } else {
                            tvResult.setText(R.string.lose);
                        }
                        JSONObject json = new JSONObject();
                        try {
                            json.put(Utils.idFirstCard, firstCard.getId());
                            json.put(Utils.score, myScore);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());
                        socket.getSocket().emit(Utils.closeRound, json, (Ack) args -> {
                        });
                    }
                }
            });
        });

        btnStai.setOnClickListener(v -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvScorePlayer.setText("" + scorePlayer);
                    btnCarta.setVisibility(View.INVISIBLE);
                    btnStai.setVisibility(View.INVISIBLE);
                    ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());

                    if (myScore >= scorePlayer) {
                        tvResult.setText(R.string.win);
                    } else {
                        tvResult.setText(R.string.lose);
                    }

                    JSONObject json = new JSONObject();
                    try {
                        json.put(Utils.idFirstCard, firstCard.getId());
                        json.put(Utils.score, myScore);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());
                    socket.getSocket().emit(Utils.closeRound, json, (Ack) args -> {
                    });
                }
            });
        });

        socket.getSocket().on(Utils.resContinueGame, args -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Deck.getIstance().restoreDeck();
                    String idClient = "", name="";
                    Boolean src = false;

                    try {
                        JSONObject json = new JSONObject(args[0].toString());
                        idClient = json.getString(Utils.idClient);
                        src = json.getBoolean(Utils.bool);
                        name = json.getString(Utils.name);
                    } catch (Exception e) { Log.d("TRY-CATCH","errore json - resContinuaGame");}

                    countResponse += 1;
                    if (src) {
                        idRestartClients.add(idClient);
                        restartNames.add(name);
                        countClient += 1;
                    }

                    if (countResponse == 2 && countClient > 0) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put(Utils.nplayers, countClient+1);
                            obj.put(Utils.idServer, socket.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (countClient == 1) {

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            socket.getSocket().emit(Utils.startGame, obj, (Ack) arg -> {});
                            JSONArray json = new JSONArray();

                            for(int i=0;i<idRestartClients.size();i++){
                                Card card = Deck.getIstance().pull();
                                JSONObject client = new JSONObject();
                                try {
                                    client.put(Utils.idClient,idRestartClients.get(i));
                                    client.put(Utils.card,card.toJSON());
                                    client.put(Utils.name,restartNames.get(i));
                                    json.put(client);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            socket.getSocket().emit(Utils.sendFirstCard,json,(Ack) args1 -> {});
                            socket.getSocket().off(Utils.requestCard);
                            socket.getSocket().off(Utils.clientTerminate);
                            Intent i = new Intent(G2PServerActivity.this, G2PServerActivity.class);
                            i.putExtra(Utils.idCard, Deck.getIstance().pull().getId());
                            i.putExtra(Utils.names,restartNames);
                            startActivity(i);
                        }

                    }else if(countResponse== 2 && countClient == 0){
                        socket.getSocket().emit(Utils.deleteGame,socket.getId());
                        Intent i = new Intent(G2PServerActivity.this, MenuActivity.class);
                        startActivity(i);
                    }
                }
            });

        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // DEALER
        outState.putString(Utils.idFirstCard,getIntent().getStringExtra(Utils.idCard));
        outState.putStringArrayList("myCards", Utils.getIdCards(myCards));
        outState.putDouble("myScore",myScore);

        // PLAYER
        outState.putString("idFirstCardPlayer",idFirstCardPlayer);
        outState.putStringArrayList("playerCards", Utils.getIdCards(playerCards));
        if(scorePlayer!=null)
            outState.putDouble("scorePlayer",scorePlayer);

        outState.putString("result",tvResult.getText().toString());

        outState.putBoolean("isMyTurn",isMyTurn);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedIstanceState){
        super.onRestoreInstanceState(savedIstanceState);

        // DEALER
        String idFirstCard = savedIstanceState.getString(Utils.idFirstCard);
        myCards = Utils.getCardsById(savedIstanceState.getStringArrayList("myCards"));
        myScore = savedIstanceState.getDouble("myScore");

        ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(idFirstCard).getIdImage());
        tvMyScore.setText(""+myScore);

        myCardAdapter = new CardAdapter(myCards);
        myRecyclerView.setAdapter(myCardAdapter);

        tvResult.setText(savedIstanceState.getString("result"));

        isMyTurn = savedIstanceState.getBoolean("isMyTurn");
        if(isMyTurn){
            btnCarta.setVisibility(View.VISIBLE);
            btnStai.setVisibility(View.VISIBLE);
        }

        // PLAYER
        idFirstCardPlayer = savedIstanceState.getString("idFirstCardPlayer");
        playerCards = Utils.getCardsById(savedIstanceState.getStringArrayList("playerCards"));

        cardAdapterPlayer = new CardAdapter(playerCards);
        rvPlayer.setAdapter(cardAdapterPlayer);

        scorePlayer = savedIstanceState.getDouble("scorePlayer");
        if(tvResult.getText().toString()!="") {
            ivFirstPlayer.setImageResource(Deck.getIstance().getCardById(idFirstCardPlayer).getIdImage());
            tvScorePlayer.setText(""+scorePlayer);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.getSocket().off(Utils.requestCard);
        socket.getSocket().off(Utils.clientTerminate);
    }

}