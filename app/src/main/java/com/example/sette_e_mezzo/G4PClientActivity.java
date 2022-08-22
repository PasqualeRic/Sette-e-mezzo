package com.example.sette_e_mezzo;

import androidx.annotation.NonNull;
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

public class G4PClientActivity extends AppCompatActivity {

    public static String strIdClient = "idClient";

    SocketClass socket = new SocketClass();
    TextView tvResult;
    Button btnCarta, btnStai;

    // MyPlayer
    ImageView ivMyFirstCard;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapterSmall myCardAdapter;
    Double myScore;
    String myId, myIdFirstCard;

    // Player 2 - Sinistra
    ImageView ivFCPlayer2;
    ArrayList<Card> cardsP2;
    TextView tvScoreP2;
    RecyclerView rvPlayer2;
    RecyclerView.LayoutManager lmPlayer2;
    CardAdapterSmall adapterP2;
    Double scoreP2;
    String idClient2;

    // Player 3 - Destra
    ImageView ivFCPlayer3;
    ArrayList<Card> cardsP3;
    TextView tvScoreP3;
    RecyclerView rvPlayer3;
    RecyclerView.LayoutManager lmPlayer3;
    CardAdapterSmall adapterP3;
    Double scoreP3;
    String idClient3;

    // Dealer
    ImageView ivFirstCardDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    RecyclerView.LayoutManager lmDealer;
    CardAdapterSmall cardAdapterDealer;
    ArrayList<Card> dealerCards;
    Double scoreDealer;
    String idServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4_players);

        idServer = getIntent().getStringExtra("idServer");

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
        myCardAdapter = new CardAdapterSmall(myCards);
        myRecyclerView.setAdapter(myCardAdapter);

        // Player 2 - Destra
        ivFCPlayer2 = findViewById(R.id.ivFCPlayer2);
        cardsP2 = new ArrayList<>();
        tvScoreP2 = findViewById(R.id.tvScorePlayer2);
        lmPlayer2 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        rvPlayer2 = findViewById(R.id.rvCardsPlayer2);
        rvPlayer2.setLayoutManager(lmPlayer2);
        adapterP2 = new CardAdapterSmall(cardsP2);
        rvPlayer2.setAdapter(adapterP2);

        // Player 3 - Sinistra
        ivFCPlayer3 = findViewById(R.id.ivFCPlayer3);
        cardsP3 = new ArrayList<>();
        tvScoreP3 = findViewById(R.id.tvScorePlayer3);
        lmPlayer3 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        rvPlayer3 = findViewById(R.id.rvCardsPlayer3);
        rvPlayer3.setLayoutManager(lmPlayer3);
        adapterP3 = new CardAdapterSmall(cardsP3);
        rvPlayer3.setAdapter(adapterP3);

        // Dealer
        ivFirstCardDealer = findViewById(R.id.ivFCPlayer4);
        tvScoreDealer = findViewById(R.id.tvScorePlayer4);
        lmDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        dealerReyclerView = findViewById(R.id.rvCardsPlayer4);
        dealerReyclerView.setLayoutManager(lmDealer);
        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapterSmall(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

        btnCarta.setOnClickListener(v -> {
            JSONObject json = new JSONObject();
            try {
                json.put("idServer",idServer);
                json.put(strIdClient,socket.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.getSocket().emit("giveMeCard", json ,(Ack) args -> {});
        });

        btnStai.setOnClickListener(v -> {
            JSONObject json = new JSONObject();
            try{
                json.put("idServer",idServer);
                json.put(strIdClient,socket.getId());
                json.put("score",myScore);
                json.put("idFirstCard",myIdFirstCard);
            }catch(Exception e){
                e.printStackTrace();
            }

            btnCarta.setVisibility(View.INVISIBLE);
            btnStai.setVisibility(View.INVISIBLE);

            socket.getSocket().emit("terminateTurn",json,(Ack) args -> {});
        });

        socket.getSocket().on("reciveYourFirstCard",args -> {
            String idClient, idFirstCard;
            Double value;
            try {
                JSONArray array = new JSONArray(args[0].toString());
                for(int i=0;i< array.length();i++){
                    Log.d("ALFA-reciveYourFirstCard",i+") -> "+array.get(i).toString());
                    JSONObject json = new JSONObject(array.get(i).toString());
                    idClient = json.getString(strIdClient);
                    idFirstCard = json.getJSONObject("card").getString("id");
                    value = json.getJSONObject("card").getDouble("value");

                    if(idClient.equals(socket.getId())){
                        myId = idClient;
                        myIdFirstCard = idFirstCard;
                        myScore = value;

                    }else if(idClient2 == null)
                        idClient2 = idClient;
                    else
                        idClient3 = idClient;

                }
            }catch(Exception e){}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMyScore.setText(""+myScore);
                    ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(myIdFirstCard).getIdImage());
                }
            });
        });

        socket.getSocket().on("myTurn", args -> {
            Log.d("ALFA","myTurn");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                        idClient = json.getString(strIdClient);
                        idCard = json.getJSONObject("card").getString("id");
                        score = json.getJSONObject("card").getDouble("value");

                    }catch(Exception e){}

                    if(socket.getId().equals(idClient)) {

                        myScore+=score;
                        tvMyScore.setText(""+myScore);
                        myCards.add(Deck.getIstance().getCardById(idCard));
                        myCardAdapter.notifyItemInserted(myCards.size() - 1);

                        if(myScore>=7.5){
                            btnCarta.setVisibility(View.INVISIBLE);
                            btnStai.setVisibility(View.INVISIBLE);
                            if(myScore>7.5){
                                tvResult.setText(R.string.lose);
                            }

                            JSONObject json = new JSONObject();
                            try{
                                json.put("idServer",idServer);
                                json.put(strIdClient,socket.getId());
                                json.put("score",myScore);
                                json.put("idFirstCard",myIdFirstCard);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            socket.getSocket().emit("terminateTurn",json,(Ack) args -> {});
                        }
                    }else if(idClient.equals(idClient2)){
                        Log.d("ALFA","idClient2");
                        cardsP2.add(Deck.getIstance().getCardById(idCard));
                        adapterP2.notifyItemInserted(cardsP2.size() - 1);
                    }else if(idClient.equals(idClient3)){
                        Log.d("ALFA","idClient3");
                        cardsP3.add(Deck.getIstance().getCardById(idCard));
                        adapterP3.notifyItemInserted(cardsP3.size() - 1);
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
                            Log.d("ALFA",i+")");
                            JSONObject client = json.getJSONObject(i);
                            if (client.getString(strIdClient).equals(idClient2)) {
                                Log.d("ALFA","client2");
                                ivFCPlayer2.setImageResource(Deck.getIstance().getCardById(client.getString("idFirstCard")).getIdImage());
                                scoreP2 = client.getDouble("score");
                                tvScoreP2.setText("" + scoreP2);
                            } else if (client.getString(strIdClient).equals(idClient3)) {
                                Log.d("ALFA","client2");
                                ivFCPlayer3.setImageResource(Deck.getIstance().getCardById(client.getString("idFirstCard")).getIdImage());
                                scoreP3 = client.getDouble("score");
                                tvScoreP3.setText("" + scoreP3);
                            } else if (client.getString(strIdClient).equals(idServer)) {
                                Log.d("ALFA","client2");
                                ivFirstCardDealer.setImageResource(Deck.getIstance().getCardById(client.getString("idFirstCard")).getIdImage());
                                scoreDealer = client.getDouble("score");
                                tvScoreDealer.setText("" + scoreDealer);
                            }
                        }

                        //se tvResult è vuota myScore è <=7.5
                        if(tvResult.getText().equals("")){

                            if((scoreDealer<=7.5 && myScore<=scoreDealer)){
                                tvResult.setText(R.string.lose);
                            }else if((scoreP2<=7.5 && myScore>=scoreP2) && (scoreP3<=7.5 && myScore>=scoreP3) && (scoreDealer<=7.5 && myScore>scoreDealer)){
                                // il mio punteggio è il più alto e non ha sballato nessuno
                                tvResult.setText(R.string.win);
                            }else if(scoreP2>7.5 && (scoreP3<=7.5 && myScore>=scoreP3) && (scoreDealer<=7.5 && myScore>scoreDealer)){
                                // il mio punteggio è il più alto e non ha sballato p2
                                tvResult.setText(R.string.win);
                            }else if((scoreP2<=7.5 && myScore>=scoreP2) && scoreP3>7.5 &&(scoreDealer<=7.5 && myScore>scoreDealer)){
                                // il mio punteggio è il più alto e non ha sballato p3
                                tvResult.setText(R.string.win);
                            }else if((scoreP2<=7.5 && myScore>=scoreP2) && (scoreP3<=7.5 && myScore>=scoreP3) && scoreDealer>7.5){
                                // il mio punteggio è il più alto e non ha sballato dealer
                                tvResult.setText(R.string.win);
                            }else if(scoreP2>7.5 && scoreP3>7.5  && (scoreDealer<=7.5 && myScore>scoreDealer)){
                                // il mio punteggio è il più alto e non hanno sballato p2 e p3
                                tvResult.setText(R.string.win);
                            }else if(scoreP2>7.5 && (scoreP3<=7.5 && myScore>=scoreP3) && scoreDealer>7.5){
                                // il mio punteggio è il più alto e non hanno sballato p2 e dealer
                                tvResult.setText(R.string.win);
                            }else if((scoreP2<=7.5 && myScore>=scoreP2) && scoreP3>7.5 && scoreDealer>7.5){
                                // il mio punteggio è il più alto e non hanno sballato p3 e dealer
                                tvResult.setText(R.string.win);
                            }else if(scoreP2>7.5 && scoreP3>7.5 && scoreDealer>7.5){
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
                        idClient = json.getString(strIdClient);
                        idFirstCard = json.getString("idFirstCard");
                        score = json.getDouble("score");

                        if (idClient.equals(idClient2)) {
                            Log.d("BETA","client2");
                            ivFCPlayer2.setImageResource(Deck.getIstance().getCardById(idFirstCard).getIdImage());
                            scoreP2 = score;
                            tvScoreP2.setText("" + scoreP2);
                        } else if(idClient.equals(idClient3)){
                            Log.d("BETA","client3");
                            ivFCPlayer3.setImageResource(Deck.getIstance().getCardById(idFirstCard).getIdImage());
                            scoreP3 = score;
                            tvScoreP3.setText("" + scoreP3);
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

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedIstanceState){
        super.onRestoreInstanceState(savedIstanceState);
    }
}