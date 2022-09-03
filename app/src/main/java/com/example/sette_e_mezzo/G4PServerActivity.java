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

public class G4PServerActivity extends AppCompatActivity {

    SocketClass socket = new SocketClass();

    int countClient = 0;
    int countResponse = 1;
    ArrayList<String> idRestartClients, restartNames;

    TextView tvResult;
    Button btnCarta, btnStai;
    ArrayList<String> idClients, names;
    Integer indexClient;  //indice per tenere traccia del client di turno
    Boolean isMyTurn;

    // MyPlayer
    ImageView ivMyFirstCard;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapterSmall myCardAdapter;
    Double myScore;
    String myIdFC;
    TextView tvMyName;

    // Player 2 - Sinistra
    String idClient2, idFCPlayer2;
    ImageView ivFCPlayer2;
    ArrayList<Card> cardsP2;
    TextView tvScoreP2;
    RecyclerView rvPlayer2;
    RecyclerView.LayoutManager lmPlayer2;
    CardAdapterSmall adapterP2;
    Double scoreP2;
    TextView tvNameP2;

    // Player 3 - Destra
    String idClient3, idFCPlayer3;
    ImageView ivFCPlayer3;
    ArrayList<Card> cardsP3;
    TextView tvScoreP3;
    RecyclerView rvPlayer3;
    RecyclerView.LayoutManager lmPlayer3;
    CardAdapterSmall adapterP3;
    Double scoreP3;
    TextView tvNameP3;

    // Player 4
    String idClient4, idFCPlayer4;
    ImageView ivFCPlayer4;
    ArrayList<Card> cardsP4;
    TextView tvScoreP4;
    RecyclerView rvPlayer4;
    RecyclerView.LayoutManager lmPlayer4;
    CardAdapterSmall adapterP4;
    Double scoreP4;
    TextView tvNameP4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4_players);

        idRestartClients = new ArrayList<>();
        restartNames = new ArrayList<>();

        isMyTurn=false;
        idClients = getIntent().getStringArrayListExtra("idClients");
        names = getIntent().getStringArrayListExtra("names");

        Log.d("MONTORI","names: "+idClients.toString());
        Log.d("MONTORI","names: "+names.toString());

        tvResult = findViewById(R.id.tvResult4);
        btnCarta = findViewById(R.id.btnCarta4);
        btnStai = findViewById(R.id.btnStai4);

        // MyPlayer
        ivMyFirstCard = findViewById(R.id.ivMyFC);
        myCards = new ArrayList<>();
        tvMyScore = findViewById(R.id.tvScoreMyPlayer4);
        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        myRecyclerView = findViewById(R.id.rvMyPlayer4);
        myRecyclerView.setLayoutManager(layoutManager);
        myCardAdapter = new CardAdapterSmall(myCards,0);
        myRecyclerView.setAdapter(myCardAdapter);
        tvMyName = findViewById(R.id.tvNameBottom);
        tvMyName.setText(R.string.dealer);

        // Player 2 - Sinistra
        ivFCPlayer2 = findViewById(R.id.ivFCPlayer2);
        cardsP2 = new ArrayList<>();
        tvScoreP2 = findViewById(R.id.tvScorePlayer2);
        lmPlayer2 = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        rvPlayer2 = findViewById(R.id.rvCardsPlayer2);
        rvPlayer2.setLayoutManager(lmPlayer2);
        adapterP2 = new CardAdapterSmall(cardsP2,90);
        rvPlayer2.setAdapter(adapterP2);
        tvNameP2 = findViewById(R.id.tvNameLeft);

        // Player 3 - Destra
        ivFCPlayer3 = findViewById(R.id.ivFCPlayer3);
        cardsP3 = new ArrayList<>();
        tvScoreP3 = findViewById(R.id.tvScorePlayer3);
        lmPlayer3 = new LinearLayoutManager(this, RecyclerView.VERTICAL,true);
        rvPlayer3 = findViewById(R.id.rvCardsPlayer3);
        rvPlayer3.setLayoutManager(lmPlayer3);
        adapterP3 = new CardAdapterSmall(cardsP3,270);
        rvPlayer3.setAdapter(adapterP3);
        tvNameP3 = findViewById(R.id.tvNameRight);

        // Player 4
        ivFCPlayer4 = findViewById(R.id.ivFCPlayer4);
        tvScoreP4 = findViewById(R.id.tvScorePlayer4);
        lmPlayer4 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        rvPlayer4 = findViewById(R.id.rvCardsPlayer4);
        rvPlayer4.setLayoutManager(lmPlayer4);
        cardsP4 = new ArrayList<>();
        adapterP4 = new CardAdapterSmall(cardsP4,0);
        rvPlayer4.setAdapter(adapterP4);
        tvNameP4 = findViewById(R.id.tvNameTop);

        Card myFirstCard = Deck.getIstance().getCardById(getIntent().getStringExtra("idCard"));
        myIdFC = myFirstCard.getId();
        ivMyFirstCard.setImageResource(myFirstCard.getIdImage());
        tvMyScore.setText(myFirstCard.getValue()+"");
        myScore = myFirstCard.getValue();

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

                        if(myScore==7.5){
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

        indexClient=0;
        idClient2 = idClients.get(indexClient);
        tvNameP2.setText(names.get(indexClient));
        idClient3 = idClients.get(indexClient+1);
        tvNameP3.setText(names.get(indexClient+1));
        idClient4 = idClients.get(indexClient+2);
        tvNameP4.setText(names.get(indexClient+2));

        socket.getSocket().on("requestCard",args -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String idClient = args[0].toString();
                    Card card = Deck.getIstance().pull();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("idClient",idClient);
                        jsonObject.put("card",card.toJSON());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.getSocket().emit("sendCard",jsonObject,(Ack) args1 -> {});

                    if(idClient.equals(idClient2)){
                        Log.d("ALFA","idClient2");
                        cardsP2.add(Deck.getIstance().getCardById(card.getId()));
                        adapterP2.notifyItemInserted(cardsP2.size() - 1);
                    }else if(idClient.equals(idClient3)){
                        Log.d("ALFA","idClient3");
                        cardsP3.add(Deck.getIstance().getCardById(card.getId()));
                        adapterP3.notifyItemInserted(cardsP3.size() - 1);
                    }else{
                        Log.d("ALFA","idClient4");
                        cardsP4.add(Deck.getIstance().getCardById(card.getId()));
                        adapterP4.notifyItemInserted(cardsP4.size() - 1);
                    }
                }
            });
        });

        socket.getSocket().on("clientTerminate",args -> {
            Log.d("ALFA","clientTeminate");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String idClient="", idFirstCard="";
                    Double score=0.0;
                    try {
                        JSONObject json = new JSONObject(args[0].toString());
                        score = json.getDouble("score");
                        idClient = json.getString("idClient");
                        idFirstCard = json.getString("idFirstCard");
                    }catch(Exception e){}

                    Log.d("ALFA","idClient: "+idClient);
                    if(idClient.equals(idClient2)) {
                        idFCPlayer2 = idFirstCard;
                        Log.d("ALFA"," - client2 idFirstCard: "+idFCPlayer2);
                        scoreP2 = score;
                    }else if(idClient.equals(idClient3)) {
                        idFCPlayer3 = idFirstCard;
                        Log.d("ALFA"," - client3 idFirstCard: "+idFCPlayer3);
                        scoreP3 = score;
                    }else {
                        idFCPlayer4 = idFirstCard;
                        Log.d("ALFA"," - client4 idFirstCard: "+idFCPlayer4);
                        scoreP4 = score;
                    }

                    if(score>7.5){
                        //l'utente che ha terminato il suo turno ha superato 7.5

                        if(idClient.equals(idClient2)) {
                            ivFCPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());
                            tvScoreP2.setText(""+scoreP2);
                        }else if(idClient.equals(idClient3)) {
                            ivFCPlayer3.setImageResource(Deck.getIstance().getCardById(idFCPlayer3).getIdImage());
                            tvScoreP3.setText(""+scoreP3);
                        }else {
                            ivFCPlayer4.setImageResource(Deck.getIstance().getCardById(idFCPlayer4).getIdImage());
                            tvScoreP4.setText(""+scoreP4);
                        }

                        JSONObject json = new JSONObject();
                        try {
                            json.put("idClient",idClient);
                            json.put("idFirstCard",idFirstCard);
                            json.put("score",score);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        socket.getSocket().emit("overSize",json,(Ack) args->{});
                    }

                    indexClient++;
                    if(indexClient<3){
                        socket.getSocket().emit("isYourTurn",idClients.get(indexClient));
                        Log.d("MONTORI","send isYourTurn a "+idClients.get(indexClient));
                    }else{
                        isMyTurn=true;
                        btnCarta.setVisibility(View.VISIBLE);
                        btnStai.setVisibility(View.VISIBLE);

                        //hanno sballato tutti, vince il dealer
                        if(scoreP2>7.5 && scoreP3>7.5 && scoreP4>7.5){
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
                    String idClient = "", name="";
                    Boolean src = false;

                    try {
                        JSONObject json = new JSONObject(args[0].toString());
                        idClient = json.getString("idClient");
                        src = json.getBoolean("bool");
                        name = json.getString("name");
                        Log.d("BETA","idClient: "+json.getString("idClient"));
                    } catch (Exception e) { Log.d("BETA","errore json - resContinuaGame");}

                    countResponse += 1;
                    if (src) {
                        idRestartClients.add(idClient);
                        restartNames.add(name);
                        countClient += 1;
                    }
                    Log.d("countResponse", countResponse + "");
                    Log.d("countClient", countClient + "");

                    if (countResponse == 4 && countClient > 0) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("nplayers", countClient+1);
                            obj.put("idserver", socket.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        socket.getSocket().emit("startGame", obj, (Ack) arg -> {});
                        JSONArray json = new JSONArray();

                        for(int i=0;i<idRestartClients.size();i++){
                            Log.d("RESTART","idClient: "+idRestartClients.get(i));
                            Card card = Deck.getIstance().pull();
                            JSONObject client = new JSONObject();
                            try {
                                client.put("idClient",idRestartClients.get(i));
                                client.put("card",card.toJSON());
                                client.put("name",restartNames.get(i));
                                json.put(client);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        socket.getSocket().emit("sendFirstCard",json,(Ack) args1 -> {});
                        socket.getSocket().off("requestCard");
                        socket.getSocket().off("clientTerminate");

                        Intent i = null;
                        switch (countClient){
                            case 1:
                                i = new Intent(G4PServerActivity.this, G2PServerActivity.class);
                                break;
                            case 2:
                                i = new Intent(G4PServerActivity.this, G3PServerActivity.class);
                                socket.getSocket().emit("isYourTurn", idRestartClients.get(0));
                                break;
                            case 3:
                                i = new Intent(G4PServerActivity.this, G4PServerActivity.class);
                                socket.getSocket().emit("isYourTurn", idRestartClients.get(0));
                                break;
                            default:
                                break;
                        }

                        i.putExtra("idClients",idRestartClients);
                        i.putExtra("names",restartNames);
                        i.putExtra("idCard", Deck.getIstance().pull().getId());
                        startActivity(i);

                    }else if(countResponse== 4 && countClient == 0){
                        Intent i = new Intent(G4PServerActivity.this, MenuActivity.class);
                        startActivity(i);
                    }
                }
            });
        });
    }

    private void closeRound(){

        Log.d("ALFA","closeRound");

        isMyTurn = false;
        btnCarta.setVisibility(View.INVISIBLE);
        btnStai.setVisibility(View.INVISIBLE);

        ivFCPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());
        ivFCPlayer3.setImageResource(Deck.getIstance().getCardById(idFCPlayer3).getIdImage());
        ivFCPlayer4.setImageResource(Deck.getIstance().getCardById(idFCPlayer4).getIdImage());

        tvScoreP2.setText(""+scoreP2);
        tvScoreP3.setText(""+scoreP3);
        tvScoreP4.setText(""+scoreP4);

        JSONArray json = new JSONArray();
        try {
            JSONObject client;
            for(int i=0;i<idClients.size();i++){
                client = new JSONObject();
                    if(i==0){
                        client.put("idClient",idClient2);
                        client.put("idFirstCard",idFCPlayer2);
                        client.put("score",scoreP2);
                    }else if(i==1){
                        client.put("idClient",idClient3);
                        client.put("idFirstCard",idFCPlayer3);
                        client.put("score",scoreP3);
                    }else{
                        client.put("idClient",idClient4);
                        client.put("idFirstCard",idFCPlayer4);
                        client.put("score",scoreP4);
                    }
                    json.put(client);
            }
            client = new JSONObject();
            client.put("idClient",socket.getId());
            client.put("idFirstCard",myIdFC);
            client.put("score",myScore);
            json.put(client);

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        //se tvResult è vuota so che myScore è <7.5
        if(tvResult.getText().equals("")){

            if((scoreP2<=7.5 && myScore>=scoreP2) && (scoreP3<=7.5 && myScore>=scoreP3) && (scoreP4<=7.5 && myScore>=scoreP4)){
                // il mio punteggio è il più alto e non ha sballato nessuno
                tvResult.setText(R.string.win);

            }else if(scoreP2>7.5 && (scoreP3<=7.5 && myScore>=scoreP3) && (scoreP4<=7.5 && myScore>=scoreP4)){
                // il mio punteggio è il più alto e non ha sballato p2
                tvResult.setText(R.string.win);
            }else if((scoreP2<=7.5 && myScore>=scoreP2) && scoreP3>7.5 && (scoreP4<=7.5 && myScore>=scoreP4)){
                // il mio punteggio è il più alto e non ha sballato p3
                tvResult.setText(R.string.win);
            }else if((scoreP2<=7.5 && myScore>=scoreP2) && (scoreP3<=7.5 && myScore>=scoreP3) && scoreP4>7.5){
                // il mio punteggio è il più alto e non ha sballato p4
                tvResult.setText(R.string.win);
            }else if(scoreP2>7.5 && scoreP3>7.5  && (scoreP4<=7.5 && myScore>=scoreP4)){
                // il mio punteggio è il più alto e non hanno sballato p2 e p3
                tvResult.setText(R.string.win);
            }else if(scoreP2>7.5 && (scoreP3<=7.5 && myScore>=scoreP3) && scoreP4>7.5){
                // il mio punteggio è il più alto e non hanno sballato p2 e p4
                tvResult.setText(R.string.win);
            }else if((scoreP2<=7.5 && myScore>=scoreP2) && scoreP3>7.5 && scoreP4>7.5){
                // il mio punteggio è il più alto e non hanno sballato p3 e p4
                tvResult.setText(R.string.win);
            }else{
                tvResult.setText(R.string.lose);
            }
        }

        socket.getSocket().emit("closeRound",json,(Ack) args->{});
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // MyPlayer
        outState.putString("myIdFC",myIdFC);
        outState.putStringArrayList("myCards",Utilis.getIdCards(myCards));
        outState.putDouble("myScore",myScore);

        // Player 2
        outState.putString("idFCPlayer2",idFCPlayer2);
        outState.putStringArrayList("cardsP2",Utilis.getIdCards(cardsP2));
        outState.putString("tvScoreP2",tvScoreP2.getText().toString());
        //outState.putString("tvNameP2",tvNameP2.getText().toString());
        if(scoreP2!=null)
            outState.putDouble("scoreP2",scoreP2);

        // Player 3
        outState.putString("idFCPlayer3",idFCPlayer3);
        outState.putStringArrayList("cardsP3",Utilis.getIdCards(cardsP3));
        outState.putString("tvScoreP3",tvScoreP3.getText().toString());
        //outState.putString("tvNameP3",tvNameP3.getText().toString());
        if(scoreP3!=null)
            outState.putDouble("scoreP3",scoreP3);

        // Player 4
        outState.putString("idFCPlayer4",idFCPlayer4);
        outState.putStringArrayList("cardsP4",Utilis.getIdCards(cardsP4));
        outState.putString("tvScoreP4",tvScoreP4.getText().toString());
        //outState.putString("tvNameP4",tvNameP4.getText().toString());
        if(scoreP4!=null)
            outState.putDouble("scoreP4",scoreP4);


        outState.putInt("indexClient",indexClient);
        outState.putString("result",tvResult.getText().toString());
        outState.putBoolean("isMyTurn",isMyTurn);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedIstanceState){
        super.onRestoreInstanceState(savedIstanceState);

        // MyPlayer
        myIdFC = savedIstanceState.getString("myIdFC");
        myCards = Utilis.getCardsById(savedIstanceState.getStringArrayList("myCards"));
        myScore = savedIstanceState.getDouble("myScore");

        ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(myIdFC).getIdImage());
        tvMyScore.setText(""+myScore);

        myCardAdapter = new CardAdapterSmall(myCards,0);
        myRecyclerView.setAdapter(myCardAdapter);

        // Player 2
        idFCPlayer2 = savedIstanceState.getString("idFCPlayer2");
        cardsP2 = Utilis.getCardsById(savedIstanceState.getStringArrayList("cardsP2"));
        scoreP2 = savedIstanceState.getDouble("scoreP2");
        tvScoreP2.setText(savedIstanceState.getString("tvScoreP2"));
        if(!tvScoreP2.getText().toString().equals(""))
            ivFCPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());

        adapterP2 = new CardAdapterSmall(cardsP2,90);
        rvPlayer2.setAdapter(adapterP2);

        // Player 3
        idFCPlayer3 = savedIstanceState.getString("idFCPlayer3");
        cardsP3 = Utilis.getCardsById(savedIstanceState.getStringArrayList("cardsP3"));
        scoreP3 = savedIstanceState.getDouble("scoreP3");
        tvScoreP3.setText(savedIstanceState.getString("tvScoreP3"));

        if(!tvScoreP3.getText().toString().equals(""))
            ivFCPlayer3.setImageResource(Deck.getIstance().getCardById(idFCPlayer3).getIdImage());

        adapterP3 = new CardAdapterSmall(cardsP3,270);
        rvPlayer3.setAdapter(adapterP3);

        // Player 4
        idFCPlayer4 = savedIstanceState.getString("idFCPlayer4");
        cardsP4 = Utilis.getCardsById(savedIstanceState.getStringArrayList("cardsP4"));
        scoreP4 = savedIstanceState.getDouble("scoreP4");
        tvScoreP4.setText(savedIstanceState.getString("tvScoreP4"));

        if(!tvScoreP4.getText().toString().equals(""))
            ivFCPlayer4.setImageResource(Deck.getIstance().getCardById(idFCPlayer4).getIdImage());

        adapterP4 = new CardAdapterSmall(cardsP4,0);
        rvPlayer4.setAdapter(adapterP4);


        idClients = savedIstanceState.getStringArrayList("idClients");
        indexClient = savedIstanceState.getInt("indexClient");
        tvResult.setText(savedIstanceState.getString("result"));

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