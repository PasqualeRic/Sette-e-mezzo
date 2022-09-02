package com.example.sette_e_mezzo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;

public class G3PServerActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();

    int countClient = 0;
    int countResponse = 1;
    TextView tvResult;
    Integer indexClient;  //indice per tenere traccia del client di turno
    ArrayList<String> idClients;
    ArrayList<String> idRestartClients;
    ArrayList<String> names;
    Button btnCarta, btnStai;
    Boolean isMyTurn;

    // PLAYER1 player top
    ImageView imageViewPlayer1;
    String idClient1, idFCPlayer1;
    ArrayList<Card> myCardsPlayer1;
    TextView tvScorePlayer1;
    RecyclerView recyclerViewPlayer1;
    RecyclerView.LayoutManager layoutManagerP1;
    CardAdapter myCardAdapterP1;
    Double scoreP1;
    Card firstCard;
    TextView tvNamePlayer1;

    // PLAYER2 player left
    String idClient2, idFCPlayer2;
    ImageView imageViewPlayer2;
    TextView tvScorePlayer2;
    ArrayList<Card> myCardsPlayer2;
    RecyclerView recyclerViewPlayer2;
    CardAdapterSmall myCardAdapterP2;
    Double scoreP2;
    TextView tvNamePlayer2;

    //PLAYER3 player bottom - MyPlayer - Dealer
    String idFCPlayer3;
    ImageView imageViewPlayer3;
    TextView tvScorePlayer3;
    RecyclerView recyclerViewPlayer3;
    CardAdapter myCardAdapterP3;
    ArrayList<Card> myCardsPlayer3;
    Double scoreP3;
    TextView tvNamePlayer3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3_players);
        idRestartClients = new ArrayList<>();

        idClients = getIntent().getStringArrayListExtra("idClients");
        names = getIntent().getStringArrayListExtra("names");
        isMyTurn = false;

        tvResult = findViewById(R.id.tvResultG3);
        btnCarta = findViewById(R.id.btnCarta3);
        btnStai = findViewById(R.id.btnStai3);


        //top
        imageViewPlayer1 = findViewById(R.id.imageViewDealer);
        recyclerViewPlayer1 = findViewById(R.id.recyclerViewDealer);
        tvScorePlayer1 = findViewById(R.id.tvScoreDealer);
        layoutManagerP1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, true);
        recyclerViewPlayer1.setLayoutManager(layoutManagerP1);
        myCardsPlayer1 = new ArrayList<>();
        myCardAdapterP1 = new CardAdapter(myCardsPlayer1);
        recyclerViewPlayer1.setAdapter(myCardAdapterP1);
        tvNamePlayer1 = findViewById(R.id.tvNamePlayerTop);

        //bottom
        tvNamePlayer3 = findViewById(R.id.tvNameMyPlayer);
        tvNamePlayer3.setText(R.string.dealer);
        imageViewPlayer3 = findViewById(R.id.imageViewPlayer1);
        tvScorePlayer3 = findViewById(R.id.tvScorePlayer1);
        recyclerViewPlayer3 = findViewById(R.id.recyclerViewPlayer1);
        LinearLayoutManager layoutDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerViewPlayer3.setLayoutManager(layoutDealer);

        myCardsPlayer3 = new ArrayList<>();
        myCardAdapterP3 = new CardAdapter(myCardsPlayer3);
        recyclerViewPlayer3.setAdapter(myCardAdapterP3);

        //left
        tvNamePlayer2 = findViewById(R.id.tvNamePlayer2);
        imageViewPlayer2 = findViewById(R.id.imageViewPlayer2);
        tvScorePlayer2 = findViewById(R.id.tvScorePlayer2);
        recyclerViewPlayer2 = findViewById(R.id.recyclerViewPlayer2);
        LinearLayoutManager layoutPlayer2 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerViewPlayer2.setLayoutManager(layoutPlayer2);
        myCardsPlayer2 = new ArrayList<>();
        myCardAdapterP2 = new CardAdapterSmall(myCardsPlayer2,90);
        recyclerViewPlayer2.setAdapter(myCardAdapterP2);

        //----
        firstCard = Deck.getIstance().getCardById(getIntent().getStringExtra("idCard"));
        imageViewPlayer3.setImageResource(firstCard.getIdImage());
        scoreP3 = firstCard.getValue();
        tvScorePlayer3.setText("" + scoreP3);
        idFCPlayer3 = firstCard.getId();
        //----

        btnCarta.setOnClickListener(v -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Card card = Deck.getIstance().pull();
                    myCardsPlayer3.add(card);
                    myCardAdapterP3.notifyItemInserted(myCardsPlayer3.size()-1);
                    scoreP3 += card.getValue();
                    tvScorePlayer3.setText(""+scoreP3);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("idClient",socket.getId());
                        jsonObject.put("card",card.toJSON());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.getSocket().emit("sendCard",jsonObject,(Ack) args1 -> {});

                    if(scoreP3>=7.5){

                        if(scoreP3==7.5){
                            tvResult.setText(R.string.win);
                        }else{
                            tvResult.setText(R.string.lose);
                        }
                        closeRound();
                    }
                }
            });
        });


        btnStai.setOnClickListener(v -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    closeRound();
                }
            });
        });

        indexClient = 0;
        //socket.getSocket().emit("isYourTurn", idClients.get(indexClient));
        idClient2 = idClients.get(indexClient);
        tvNamePlayer2.setText(names.get(indexClient));
        idClient1 = idClients.get(indexClient + 1);
        tvNamePlayer1.setText(names.get(indexClient+1));
        Log.d("idClient2: ", idClient2+" -> "+tvNamePlayer2.getText());
        Log.d("idclient1--", idClient1+" -> "+tvNamePlayer1.getText());

        socket.getSocket().on("requestCard", args -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String idClient = args[0].toString();
                    Card card = Deck.getIstance().pull();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("idClient", idClient);
                        jsonObject.put("card", card.toJSON());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.getSocket().emit("sendCard", jsonObject, (Ack) args1 -> {
                    });
                    Log.d("IDCLIENT", idClient+"");
                    if (idClient.equals(idClient2)) {
                        Log.d("ALFA", "idClient2");
                        myCardsPlayer2.add(Deck.getIstance().getCardById(card.getId()));
                        myCardAdapterP2.notifyItemInserted(myCardsPlayer2.size() - 1);
                    } else {
                        Log.d("ALFA", "idClient3");
                        myCardsPlayer1.add(Deck.getIstance().getCardById(card.getId()));
                        myCardAdapterP1.notifyItemInserted(myCardsPlayer1.size() - 1);
                    }
                }
            });
        });

        socket.getSocket().on("clientTerminate", args -> {
            Log.d("ALFA", "clientTeminate");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String idClient = "", idFirstCard = "";
                    Double score = 0.0;
                    try {
                        JSONObject json = new JSONObject(args[0].toString());
                        score = json.getDouble("score");
                        idClient = json.getString("idClient");
                        idFirstCard = json.getString("idFirstCard");
                    } catch (Exception e) {
                    }

                    Log.d("ALFA", "idClient: " + idClient);
                    if (idClient.equals(idClient2)) {
                        idFCPlayer2 = idFirstCard;
                        Log.d("ALFA", " - client2 idFirstCard: " + idFCPlayer2);
                        scoreP2 = score;
                    } else if (idClient.equals(idClient1)) {
                        idFCPlayer1 = idFirstCard;
                        Log.d("ALFA", " - client1 idFirstCard: " + idFCPlayer1);
                        scoreP1 = score;
                    }

                    if (score > 7.5) {
                        Log.d("sballato", "sballato");
                        //l'utente che ha terminato il suo turno ha superato 7.5

                        if (idClient.equals(idClient2)) {
                            tvScorePlayer2.setText(""+scoreP2);
                            imageViewPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());
                        } else if (idClient.equals(idClient1)) {
                            tvScorePlayer1.setText(""+scoreP1);
                            imageViewPlayer1.setImageResource(Deck.getIstance().getCardById(idFCPlayer1).getIdImage());
                        }

                        JSONObject json = new JSONObject();
                        try {
                            json.put("idClient", idClient);
                            json.put("idFirstCard", idFirstCard);
                            json.put("score", score);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        socket.getSocket().emit("overSize", json, (Ack) args -> {
                        });
                    }

                    indexClient++;
                    if (indexClient < 2) {
                        socket.getSocket().emit("isYourTurn", idClients.get(indexClient));
                    } else {
                        isMyTurn = true;
                        btnCarta.setVisibility(View.VISIBLE);
                        btnStai.setVisibility(View.VISIBLE);

                        //hanno sballato tutti, vince il dealer
                        if (scoreP2 > 7.5 && scoreP3 > 7.5) {
                            closeRound();
                        }
                    }
                }
            });

        });

        socket.getSocket().on("resContinueGame", args -> {
            Log.d("resContinueGame", args[0].toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Deck.getIstance().restoreDeck();
                    String idClient = "";
                    Boolean src = false;

                    try {
                        JSONObject json = new JSONObject(args[0].toString());
                        idClient = json.getString("idClient");
                        src = json.getBoolean("bool");
                        Log.d("BETA","idClient: "+json.getString("idClient"));
                    } catch (Exception e) { Log.d("BETA","errore json - resContinuaGame");}

                    countResponse += 1;
                    if (src) {
                        idRestartClients.add(idClient);
                        countClient += 1;
                    }
                    Log.d("countResponse", countResponse + "");
                    Log.d("countClient", countClient + "");
                    if (countResponse == 3 && countClient > 0) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("nplayers", countClient+1);
                            obj.put("idserver", socket.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("dentroif", countClient + "");

                        if (countClient == 1) {
                            Log.d("dentroif", "dentro if");
                            socket.getSocket().emit("startGame", obj, (Ack) arg -> {
                            });
                            JSONArray json = new JSONArray();
                            Log.d("BETA"," -- idRestartClients --");
                            for(int i=0;i<idRestartClients.size();i++){
                                Log.d("BETA","idClient: "+idRestartClients.get(i));
                                Card card = Deck.getIstance().pull();
                                JSONObject client = new JSONObject();
                                try {
                                    client.put("idClient",idRestartClients.get(i));
                                    client.put("card",card.toJSON());
                                    json.put(client);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            socket.getSocket().emit("sendFirstCard",json,(Ack) args1 -> {});
                            socket.getSocket().off("requestCard");
                            socket.getSocket().off("clientTerminate");
                            Intent i = new Intent(G3PServerActivity.this, G2PServerActivity.class);
                            i.putExtra("idCard", Deck.getIstance().pull().getId());
                            startActivity(i);
                        }

                    }else if(countResponse== 3 && countClient == 0){
                        Intent i = new Intent(G3PServerActivity.this, MenuActivity.class);
                        startActivity(i);
                    }
                }
            });

        });
    }

    private void closeRound() {

        Log.d("ALFA", "closeRound");

        isMyTurn = false;
        btnCarta.setVisibility(View.INVISIBLE);
        btnStai.setVisibility(View.INVISIBLE);

        imageViewPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());
        imageViewPlayer1.setImageResource(Deck.getIstance().getCardById(idFCPlayer1).getIdImage());

        tvScorePlayer2.setText(""+scoreP2);
        tvScorePlayer1.setText(""+scoreP1);

        JSONArray json = new JSONArray();
        try {
            JSONObject client;
            for (int i = 0; i < idClients.size(); i++) {
                client = new JSONObject();
                if (i == 0) {
                    client.put("idClient", idClient2);
                    client.put("idFirstCard", idFCPlayer2);
                    client.put("score", scoreP2);
                } else if (i == 1) {
                    client.put("idClient", idClient1);
                    client.put("idFirstCard", idFCPlayer1);
                    client.put("score", scoreP1);
                }
                json.put(client);
            }
            client = new JSONObject();
            client.put("idClient", socket.getId());
            client.put("idFirstCard", idFCPlayer3);
            client.put("score", scoreP3);
            json.put(client);

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        //se tvResult è vuota so che myScore è <7.5
        if (tvResult.getText().equals("")) {

            if ((scoreP2 <= 7.5 && scoreP1 >= scoreP2) && (scoreP1 <= 7.5 && scoreP3 >= scoreP1)) {
                // il mio punteggio è il più alto e non ha sballato nessuno
                tvResult.setText(R.string.win);
            } else if (scoreP2 > 7.5 && (scoreP1 <= 7.5 && scoreP3 >= scoreP1)) {
                // il mio punteggio è il più alto e non ha sballato l'altro player
                tvResult.setText(R.string.win);
            } else if ((scoreP2 <= 7.5 && scoreP3 >= scoreP2) && scoreP1 > 7.5) {
                // il mio punteggio è il più alto e non ha sballato il dealer
                tvResult.setText(R.string.win);
            } else if (scoreP2 > 7.5 && scoreP1 > 7.5) {
                // hanno sballato sia l'altro player che il dealer
                tvResult.setText(R.string.win);
            } else {
                tvResult.setText(R.string.lose);
            }
        }

        socket.getSocket().emit("closeRound", json, (Ack) args -> {});
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // MyPlayer - bottom
        outState.putString("idFCPlayer3",idFCPlayer3);
        outState.putStringArrayList("myCardsPlayer3",Utilis.getIdCards(myCardsPlayer3));
        outState.putDouble("scoreP3",scoreP3);

        // Player 2 - left
        outState.putString("idFCPlayer2",idFCPlayer2);
        outState.putStringArrayList("myCardsPlayer2",Utilis.getIdCards(myCardsPlayer2));
        outState.putString("tvScorePlayer2",tvScorePlayer2.getText().toString());
        outState.putString("namePlayer2",tvNamePlayer2.getText().toString());
        if(scoreP2!=null)
            outState.putDouble("scoreP2",scoreP2);

        // Player 1 - top
        outState.putString("idFCPlayer1",idFCPlayer1);
        outState.putStringArrayList("myCardsPlayer1",Utilis.getIdCards(myCardsPlayer1));
        outState.putString("tvScorePlayer1",tvScorePlayer1.getText().toString());
        outState.putString("namePlayer1",tvNamePlayer1.getText().toString());
        if(scoreP1!=null)
            outState.putDouble("scoreP1",scoreP1);

        outState.putStringArrayList("idClients",idClients);
        outState.putInt("indexClient",indexClient);
        outState.putString("tvResult",tvResult.getText().toString());
        outState.putBoolean("isMyTurn",isMyTurn);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedIstanceState){
        super.onRestoreInstanceState(savedIstanceState);

        // MyPlayer - bottom
        idFCPlayer3 = savedIstanceState.getString("idFCPlayer3");
        myCardsPlayer3 = Utilis.getCardsById(savedIstanceState.getStringArrayList("myCardsPlayer3"));
        scoreP3 = savedIstanceState.getDouble("scoreP3");

        imageViewPlayer3.setImageResource(Deck.getIstance().getCardById(idFCPlayer3).getIdImage());
        tvScorePlayer3.setText(""+scoreP3);

        myCardAdapterP3 = new CardAdapter(myCardsPlayer3);
        recyclerViewPlayer3.setAdapter(myCardAdapterP3);

        // Player 2 - left
        idFCPlayer2 = savedIstanceState.getString("idFCPlayer2");
        myCardsPlayer2 = Utilis.getCardsById(savedIstanceState.getStringArrayList("myCardsPlayer2"));
        scoreP2 = savedIstanceState.getDouble("scoreP2");
        tvScorePlayer2.setText(savedIstanceState.getString("tvScorePlayer2"));
        tvNamePlayer2.setText(savedIstanceState.getString("namePlayer2"));
        if(!tvScorePlayer2.getText().toString().equals(""))
            imageViewPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());

        myCardAdapterP2 = new CardAdapterSmall(myCardsPlayer2,90);
        recyclerViewPlayer2.setAdapter(myCardAdapterP2);

        // Player 1 - top
        idFCPlayer1 = savedIstanceState.getString("idFCPlayer1");
        myCardsPlayer1 = Utilis.getCardsById(savedIstanceState.getStringArrayList("myCardsPlayer1"));
        scoreP1 = savedIstanceState.getDouble("scoreP1");
        tvScorePlayer1.setText(savedIstanceState.getString("tvScorePlayer1"));
        tvNamePlayer1.setText(savedIstanceState.getString("namePlayer1"));

        if(!tvScorePlayer1.getText().toString().equals(""))
            imageViewPlayer1.setImageResource(Deck.getIstance().getCardById(idFCPlayer1).getIdImage());

        myCardAdapterP1 = new CardAdapter(myCardsPlayer1);
        recyclerViewPlayer1.setAdapter(myCardAdapterP1);

        idClients = savedIstanceState.getStringArrayList("idClients");
        indexClient = savedIstanceState.getInt("indexClient");
        tvResult.setText(savedIstanceState.getString("tvResult"));

        isMyTurn = savedIstanceState.getBoolean("isMyTurn");
        if(isMyTurn){
            btnCarta.setVisibility(View.VISIBLE);
            btnStai.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.getSocket().off("requestCard");
        socket.getSocket().off("clientTerminate");
    }

}