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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;

public class G3PServerActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    TextView tvResult;
    Integer indexClient;  //indice per tenere traccia del client di turno
    ArrayList<String> idClients;
    Button btnCarta, btnStai;

    // PLAYER1 player top

    ImageView imageViewPlayer1;
    String idClient1, idFCPlayer1;
    ArrayList<Card> myCardsPlayer1;
    TextView tvScorePlayer1;
    RecyclerView recyclerViewPlayer1;
    RecyclerView.LayoutManager layoutManagerP1;
    CardAdapterSmall myCardAdapterP1;
    Double scoreP1;
    Card firstCard;
    // PLAYER2 player left
    String idClient2, idFCPlayer2;
    ImageView imageViewPlayer2;
    TextView tvScorePlayer2;
    ArrayList<Card> myCardsPlayer2;
    TextView tvP2;
    RecyclerView recyclerViewPlayer2;
    RecyclerView.LayoutManager layoutManagerP2;
    CardAdapterSmallO myCardAdapterP2;
    Double scoreP2;

    //PLAYER3 player bottom
    String idClient3, idFCPlayer3;
    ImageView imageViewPlayer3;
    TextView tvScorePlayer3;
    RecyclerView recyclerViewPlayer3;
    CardAdapterSmall myCardAdapterP3;
    ArrayList<Card> myCardsPlayer3;
    Double scoreP3;

    Double myScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3_players);
        idClients = getIntent().getStringArrayListExtra("idClients");

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
        myCardAdapterP1 = new CardAdapterSmall(myCardsPlayer1);
        recyclerViewPlayer1.setAdapter(myCardAdapterP1);


        //bottom
        imageViewPlayer3 = findViewById(R.id.imageViewPlayer1);
        tvScorePlayer3 = findViewById(R.id.tvScorePlayer1);
        recyclerViewPlayer3 = findViewById(R.id.recyclerViewPlayer1);
        LinearLayoutManager layoutDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerViewPlayer3.setLayoutManager(layoutDealer);

        myCardsPlayer3 = new ArrayList<>();
        myCardAdapterP3 = new CardAdapterSmall(myCardsPlayer3);
        recyclerViewPlayer3.setAdapter(myCardAdapterP3);

        //left

        imageViewPlayer2 = findViewById(R.id.imageViewPlayer2);
        tvScorePlayer2 = findViewById(R.id.tvScorePlayer2);
        recyclerViewPlayer2 = findViewById(R.id.recyclerViewPlayer2);
        LinearLayoutManager layoutPlayer2 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerViewPlayer2.setLayoutManager(layoutPlayer2);
        myCardsPlayer2 = new ArrayList<>();
        myCardAdapterP2 = new CardAdapterSmallO(myCardsPlayer2);
        recyclerViewPlayer2.setAdapter(myCardAdapterP2);

        //----
        firstCard = Deck.getIstance().pull();
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
                    Log.d("scoreP1", scoreP3+"");
                    tvScorePlayer3.setText(""+scoreP3);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("idClient",socket.getId());
                        jsonObject.put("card",card.toJSON());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.getSocket().emit("sendCard",jsonObject,(Ack) args1 -> {});

                    // TODO condizioni di fine

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
        socket.getSocket().emit("isYourTurn", idClients.get(indexClient));
        idClient2 = idClients.get(indexClient);
        idClient1 = idClients.get(indexClient + 1);
        Log.d("idclient2--", idClient2+"");
        Log.d("idclient1--", idClient1+"");

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
                        Log.d("cacio", score+"");
                    } catch (Exception e) {
                    }

                    Log.d("ALFA", "idClient: " + idClient);
                    if (idClient.equals(idClient2)) {
                        idFCPlayer2 = idFirstCard;
                        Log.d("ALFA", " - client2 idFirstCard: " + idFCPlayer2);
                        scoreP2 = score;
                    } else if (idClient.equals(idClient1)) {
                        idFCPlayer1 = idFirstCard;
                        Log.d("ALFA", " - client3 idFirstCard: " + idFCPlayer1);
                        scoreP1 = score;
                    }

                    if (score > 7.5) {
                        Log.d("sballato", "sballato");
                        //l'utente che ha terminato il suo turno ha superato 7.5

                        if (idClient.equals(idClient2)) {
                            imageViewPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());
                        } else if (idClient.equals(idClient3)) {
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
                        btnCarta.setVisibility(View.VISIBLE);
                        btnStai.setVisibility(View.VISIBLE);

                        //hanno sballato tutti, vince il dealer
                        if (scoreP1 > 7.5 && scoreP2 > 7.5 && scoreP3 > 7.5) {
                            closeRound();
                        }
                    }
                }
            });

        });

        /*
            int countClient = 0
            int countResponse = 0
            on.continueGame args ->{
                recive true or false
                countResponse += 1
                if args == true:
                    countClient += 1

                if countResponse == 3 and countClient > 0
                    emit.broadcast partita -> countClient + 1,socket.getId() //mando nuovo numero giocatore e client id per restartare la partita
                    if countClient + 1 == 2:
                        start intent G2P
                    else countClient + 1 == 3:
                        start intent G3P
            }
         */


    }

    private void closeRound() {

        Log.d("ALFA", "closeRound");

        btnCarta.setVisibility(View.INVISIBLE);
        btnStai.setVisibility(View.INVISIBLE);

        imageViewPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());
        imageViewPlayer1.setImageResource(Deck.getIstance().getCardById(idFCPlayer1).getIdImage());

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
}