package com.example.sette_e_mezzo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;

public class G3PClientActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    String idServer;
    TextView tvResult;
    Boolean isMyTurn;
    Button si, no;

    // PLAYER1 - MyPlayer - bottom
    Button btnCarta, btnStai;
    ImageView imageViewPlayer1;
    ArrayList<Card> myCardsPlayer1;
    TextView tvScorePlayer1;
    RecyclerView recyclerViewPlayer1;
    RecyclerView.LayoutManager layoutManagerP1;
    CardAdapter myCardAdapterP1;
    String myIdFirstCard, myId;
    Double myScore;
    TextView tvNameMyPlayer;

    // PLAYER2 - left
    ImageView imageViewPlayer2;
    TextView tvScorePlayer2;
    ArrayList<Card> myCardsPlayer2;
    RecyclerView recyclerViewPlayer2;
    RecyclerView.LayoutManager layoutManagerP2;
    CardAdapterSmall myCardAdapterP2;
    Double scoreP2;
    String idClient2, idFCPlayer2;
    TextView tvNamePlayer2;

    // DELAER - top
    ImageView imageViewDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    RecyclerView.LayoutManager layoutManagerDealer;
    CardAdapter cardAdapterDealer;
    ArrayList<Card> dealerCards;
    Double scoreDealer;
    String idFCDealer;
    TextView tvNameDealer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3_players);

        isMyTurn=false;
        idServer = getIntent().getStringExtra("idServer");

        tvResult = findViewById(R.id.tvResultG3);
        btnCarta = findViewById(R.id.btnCarta3);
        btnStai = findViewById(R.id.btnStai3);

        // MyPlayer
        imageViewPlayer1= findViewById(R.id.imageViewPlayer1);
        myCardsPlayer1 = new ArrayList<>();
        tvScorePlayer1 = findViewById(R.id.tvScorePlayer1);
        layoutManagerP1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        recyclerViewPlayer1 = findViewById(R.id.recyclerViewPlayer1);
        recyclerViewPlayer1.setLayoutManager(layoutManagerP1);
        myCardAdapterP1 = new CardAdapter(myCardsPlayer1);
        recyclerViewPlayer1.setAdapter(myCardAdapterP1);
        tvNameMyPlayer = findViewById(R.id.tvNameMyPlayer);

        // Player 2 - Sinistra
        imageViewPlayer2 = findViewById(R.id.imageViewPlayer2);
        myCardsPlayer2 = new ArrayList<>();
        tvScorePlayer2 = findViewById(R.id.tvScorePlayer2);
        layoutManagerP2 = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        recyclerViewPlayer2 = findViewById(R.id.recyclerViewPlayer2);
        recyclerViewPlayer2.setLayoutManager(layoutManagerP2);
        myCardAdapterP2 = new CardAdapterSmall(myCardsPlayer2,90);
        recyclerViewPlayer2.setAdapter(myCardAdapterP2);
        tvNamePlayer2 = findViewById(R.id.tvNamePlayer2);

        // Dealer
        imageViewDealer = findViewById(R.id.imageViewDealer);
        tvScoreDealer = findViewById(R.id.tvScoreDealer);
        layoutManagerDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        dealerReyclerView = findViewById(R.id.recyclerViewDealer);
        dealerReyclerView.setLayoutManager(layoutManagerDealer);
        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapter(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);
        tvNameDealer = findViewById(R.id.tvNamePlayerTop);
        tvNameDealer.setText(R.string.dealer);

       btnCarta.setOnClickListener(v -> {
           Log.d("givemecard", "givemecard");
            JSONObject json = new JSONObject();
            try {
                json.put("idServer",idServer);
                json.put("idClient",socket.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.getSocket().emit("giveMeCard", json ,(Ack) args -> {});
        });

        btnStai.setOnClickListener(v -> {
            JSONObject json = new JSONObject();
            try{
                json.put("idServer",idServer);
                json.put("idClient",socket.getId());
                json.put("score",myScore);
                json.put("idFirstCard",myIdFirstCard);
            }catch(Exception e){
                e.printStackTrace();
            }

            btnCarta.setVisibility(View.INVISIBLE);
            btnStai.setVisibility(View.INVISIBLE);

            isMyTurn=false;
            socket.getSocket().emit("terminateTurn",json,(Ack) args -> {});
        });

        socket.getSocket().on("reciveYourFirstCard",args -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("BETA", "reciveYourFirstCard - G3P");

                    String idClient, idFirstCard, name;
                    Double value;
                    //Log.d("BETA", "args[0]:"+args[0].toString());

                    try {
                        JSONArray array = new JSONArray(args[0].toString());
                        //Log.d("NAME", array+"");
                        //Log.d("NAME", array.length()+"");
                        for(int i=0;i< array.length();i++){
                            Log.d("ALFA-reciveYourFirstCard",i+") -> "+array.get(i).toString());
                            JSONObject json = new JSONObject(array.get(i).toString());
                            idClient = json.getString("idClient");
                            name = json.getString("name");
                            idFirstCard = json.getJSONObject("card").getString("id");
                            value = json.getJSONObject("card").getDouble("value");
                            //Log.d("NAME","name: "+name);
                           // Log.d("NAME","idClient.equals(socket.getId() -> "+idClient+" == "+socket.getId());
                            if(idClient.equals(socket.getId())){
                                myId = idClient;
                                myIdFirstCard = idFirstCard;
                                myScore = value;
                                tvNameMyPlayer.setText(name);
                            }else{
                                idClient2 = idClient;
                                tvNamePlayer2.setText(name);
                            }
                        }

                    }catch(Exception e){ Log.d("BETA", "errore, "+e.toString());}


                    tvScorePlayer1.setText(""+myScore);
                    imageViewPlayer1.setImageResource(Deck.getIstance().getCardById(myIdFirstCard).getIdImage());
                }
            });
        });
        socket.getSocket().on("myTurn", args -> {
            Log.d("MONTORI","myTurn");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isMyTurn=true;
                    btnCarta.setVisibility(View.VISIBLE);
                    btnStai.setVisibility(View.VISIBLE);
                }
            });
        });
        socket.getSocket().on("reciveCard",args -> {
            Log.d("ALFA","reciveCard");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String idClient="", idCard="";
                    Double score=0.0;

                    try {
                        JSONObject json = new JSONObject(args[0].toString());
                        idClient = json.getString("idClient");
                        idCard = json.getJSONObject("card").getString("id");
                        score = json.getJSONObject("card").getDouble("value");

                    }catch(Exception e){}

                    if(socket.getId().equals(idClient)) {

                        myScore+=score;
                        tvScorePlayer1.setText(""+myScore);
                        myCardsPlayer1.add(Deck.getIstance().getCardById(idCard));
                        myCardAdapterP1.notifyItemInserted(myCardsPlayer1.size() - 1);

                        if(myScore>=7.5){
                            isMyTurn=false;
                            btnCarta.setVisibility(View.INVISIBLE);
                            btnStai.setVisibility(View.INVISIBLE);
                            if(myScore>7.5){
                                tvResult.setText(R.string.lose);
                            }

                            JSONObject json = new JSONObject();
                            try{
                                json.put("idServer",idServer);
                                json.put("idClient",socket.getId());
                                json.put("score",myScore);
                                json.put("idFirstCard",myIdFirstCard);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            socket.getSocket().emit("terminateTurn",json,(Ack) args -> {});
                        }
                    }else if(idClient.equals(idClient2)){
                        Log.d("ALFA","idClient2");
                        myCardsPlayer2.add(Deck.getIstance().getCardById(idCard));
                        myCardAdapterP2.notifyItemInserted(myCardsPlayer2.size() - 1);
                    }else{
                        Log.d("ALFA","dealer");
                        dealerCards.add(Deck.getIstance().getCardById(idCard));
                        cardAdapterDealer.notifyItemInserted(dealerCards.size() - 1);
                    }
                }
            });
        });

        socket.getSocket().on("closeRound",args -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONArray json = new JSONArray(args[0].toString());
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject client = json.getJSONObject(i);
                            if (client.getString("idClient").equals(idClient2)) {
                                idFCPlayer2 = client.getString("idFirstCard");
                                imageViewPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());
                                scoreP2 = client.getDouble("score");
                                tvScorePlayer2.setText("" + scoreP2);
                            } else if (client.getString("idClient").equals(idServer)) {
                                idFCDealer = client.getString("idFirstCard");
                                imageViewDealer.setImageResource(Deck.getIstance().getCardById(idFCDealer).getIdImage());
                                scoreDealer = client.getDouble("score");
                                tvScoreDealer.setText("" + scoreDealer);
                                Dialog d = new Dialog(G3PClientActivity.this);
                                d.setTitle("restart");
                                d.setCancelable(false);
                                d.setContentView(R.layout.dialog);
                                d.show();
                                si = d.findViewById(R.id.btnSi);
                                no = d.findViewById(R.id.btnNo);

                                si.setOnClickListener(v->{
                                    JSONObject j = new JSONObject();
                                    try {
                                        j.put("idClient", socket.getId());
                                        j.put("bool", true);
                                        j.put("name", tvNameMyPlayer.getText().toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    socket.getSocket().emit("continueGame", j ,(Ack) args -> {});
                                    d.hide();
                                    d.cancel();
                                    finish();
                                });
                                no.setOnClickListener(v -> {
                                    JSONObject j = new JSONObject();
                                    try {
                                        j.put("idClient", socket.getId());
                                        j.put("bool", false);
                                        j.put("name", tvNameMyPlayer.getText().toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    socket.getSocket().emit("continueGame",j ,(Ack) args -> {});
                                    socket.getSocket().emit("deletePlayer", socket.getId() ,(Ack) args -> {});
                                    socket.disconnection();
                                    Intent intent = new Intent(G3PClientActivity.this, MenuActivity.class);
                                    startActivity(intent);
                                });
                            }
                        }

                        //se tvResult è vuota myScore è <=7.5
                        if(tvResult.getText().equals("")){

                            if((scoreDealer<=7.5 && myScore<=scoreDealer)){
                                tvResult.setText(R.string.lose);
                            }else if((scoreP2<=7.5 && myScore>=scoreP2) && (scoreDealer<=7.5 && myScore>scoreDealer)){
                                // il mio punteggio è il più alto e non ha sballato nessuno
                                tvResult.setText(R.string.win);
                            }else if(scoreP2>7.5 && (scoreDealer<=7.5 && myScore>scoreDealer)){
                                // il mio punteggio è il più alto e non ha sballato p2
                                tvResult.setText(R.string.win);
                            }else if((scoreP2<=7.5 && myScore>=scoreP2) && scoreDealer>7.5){
                                // il mio punteggio è il più alto e non ha sballato dealer
                                tvResult.setText(R.string.win);
                            }else if(scoreP2>7.5 && scoreDealer>7.5){
                                // il mio punteggio è il più alto e non hanno sballato p3 e dealer
                                tvResult.setText(R.string.win);
                            }else{
                                tvResult.setText(R.string.lose);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        socket.getSocket().on("overSize",args -> {
            Log.d("BETA","overSize");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String idFirstCard, idClient;
                        Double score;

                        JSONObject json = new JSONObject(args[0].toString());
                        idClient = json.getString("idClient");
                        idFirstCard = json.getString("idFirstCard");
                        score = json.getDouble("score");

                        if (idClient.equals(idClient2)) {
                            Log.d("BETA","client2");
                            idFCPlayer2 = idFirstCard;
                            imageViewPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());
                            scoreP2 = score;
                            tvScorePlayer2.setText(""+scoreP2);
                        }

                    } catch (JSONException e) {
                        Log.d("BETA","errore");
                        e.printStackTrace();
                    }

                }
            });
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // MyPlayer - bottom
        outState.putString("myIdFirstCard",myIdFirstCard);
        outState.putStringArrayList("myCardsPlayer1",Utilis.getIdCards(myCardsPlayer1));
        outState.putDouble("myScore",myScore);
        outState.putString("myName",tvNameMyPlayer.getText().toString());

        // Player 2 - left
        outState.putString("idFCPlayer2",idFCPlayer2);
        outState.putStringArrayList("myCardsPlayer2",Utilis.getIdCards(myCardsPlayer2));
        outState.putString("tvScorePlayer2",tvScorePlayer2.getText().toString());
        outState.putString("idClient2",idClient2);
        outState.putString("nameOtherClient",tvNamePlayer2.getText().toString());
        if(scoreP2!=null)
            outState.putDouble("scoreP2",scoreP2);

        // Dealer - top
        outState.putString("idFCDealer",idFCDealer);
        outState.putStringArrayList("dealerCards",Utilis.getIdCards(dealerCards));
        outState.putString("tvScoreDealer",tvScoreDealer.getText().toString());
        if(scoreDealer!=null)
            outState.putDouble("scoreDealer",scoreDealer);

        outState.putString("tvResult",tvResult.getText().toString());
        outState.putBoolean("isMyTurn",isMyTurn);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedIstanceState){
        super.onRestoreInstanceState(savedIstanceState);

        // MyPlayer
        myIdFirstCard = savedIstanceState.getString("myIdFirstCard");
        myCardsPlayer1 = Utilis.getCardsById(savedIstanceState.getStringArrayList("myCardsPlayer1"));
        myScore = savedIstanceState.getDouble("myScore");
        tvNameMyPlayer.setText(savedIstanceState.getString("myName"));

        imageViewPlayer1.setImageResource(Deck.getIstance().getCardById(myIdFirstCard).getIdImage());
        tvScorePlayer1.setText(""+myScore);

        myCardAdapterP1 = new CardAdapter(myCardsPlayer1);
        recyclerViewPlayer1.setAdapter(myCardAdapterP1);

        // Player 2
        idFCPlayer2 = savedIstanceState.getString("idFCPlayer2");
        myCardsPlayer2 = Utilis.getCardsById(savedIstanceState.getStringArrayList("myCardsPlayer2"));
        scoreP2 = savedIstanceState.getDouble("scoreP2");
        tvScorePlayer2.setText(savedIstanceState.getString("tvScorePlayer2"));
        idClient2 = savedIstanceState.getString("idClient2");
        tvNamePlayer2.setText(savedIstanceState.getString("nameOtherClient"));

        if(!tvScorePlayer2.getText().toString().equals(""))
            imageViewPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());

        myCardAdapterP2 = new CardAdapterSmall(myCardsPlayer2,90);
        recyclerViewPlayer2.setAdapter(myCardAdapterP2);

        // Dealer
        idFCDealer = savedIstanceState.getString("idFCDealer");
        dealerCards= Utilis.getCardsById(savedIstanceState.getStringArrayList("dealerCards"));
        scoreDealer = savedIstanceState.getDouble("scoreDealer");
        tvScoreDealer.setText(savedIstanceState.getString("tvScoreDealer"));
        if(!tvScoreDealer.getText().toString().equals(""))
            imageViewDealer.setImageResource(Deck.getIstance().getCardById(idFCDealer).getIdImage());

        layoutManagerDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        dealerReyclerView.setLayoutManager(layoutManagerDealer);
        cardAdapterDealer = new CardAdapter(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

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
        socket.getSocket().off("overSize");
        socket.getSocket().off("closeRound");
        socket.getSocket().off("reciveCard");
        socket.getSocket().off("myTurn");
        socket.getSocket().off("reciveYourFirstCard");
    }
    @Override
    protected void onStop(){
        super.onStop();
        socket.getSocket().off("overSize");
        socket.getSocket().off("closeRound");
        socket.getSocket().off("reciveCard");
        socket.getSocket().off("myTurn");
        socket.getSocket().off("reciveYourFirstCard");
    }
}