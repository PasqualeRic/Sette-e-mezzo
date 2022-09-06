package com.example.sette_e_mezzo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
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

public class G4PClientActivity extends AppCompatActivity {

    public static String strIdClient = "idClient";

    SocketClass socket = new SocketClass();
    TextView tvResult;
    Button btnCarta, btnStai;
    Boolean isMyTurn;
    Button si, no;

    // MyPlayer
    ImageView ivMyFirstCard;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapterSmall myCardAdapter;
    Double myScore;
    String myIdFirstCard;
    TextView tvMyName;

    // Player 2 - Sinistra
    ImageView ivFCPlayer2;
    ArrayList<Card> cardsP2;
    TextView tvScoreP2;
    RecyclerView rvPlayer2;
    RecyclerView.LayoutManager lmPlayer2;
    CardAdapterSmall adapterP2;
    Double scoreP2;
    String idClient2, idFCPlayer2;
    TextView tvNameP2;

    // Player 3 - Destra
    ImageView ivFCPlayer3;
    ArrayList<Card> cardsP3;
    TextView tvScoreP3;
    RecyclerView rvPlayer3;
    RecyclerView.LayoutManager lmPlayer3;
    CardAdapterSmall adapterP3;
    Double scoreP3;
    String idClient3, idFCPlayer3;
    TextView tvNameP3;

    // Dealer
    ImageView ivFirstCardDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    RecyclerView.LayoutManager lmDealer;
    CardAdapterSmall cardAdapterDealer;
    ArrayList<Card> dealerCards;
    Double scoreDealer;
    String idServer, idFCDealer;
    TextView tvNameP4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4_players);

        isMyTurn=false;
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
        myCardAdapter = new CardAdapterSmall(myCards,0);
        myRecyclerView.setAdapter(myCardAdapter);
        tvMyName = findViewById(R.id.tvNameBottom);

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

        // Dealer
        ivFirstCardDealer = findViewById(R.id.ivFCPlayer4);
        tvScoreDealer = findViewById(R.id.tvScorePlayer4);
        lmDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        dealerReyclerView = findViewById(R.id.rvCardsPlayer4);
        dealerReyclerView.setLayoutManager(lmDealer);
        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapterSmall(dealerCards,0);
        dealerReyclerView.setAdapter(cardAdapterDealer);
        tvNameP4 = findViewById(R.id.tvNameTop);
        tvNameP4.setText(R.string.dealer);

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

            isMyTurn=false;
            socket.getSocket().emit("terminateTurn",json,(Ack) args -> {});
        });

        socket.getSocket().on("reciveYourFirstCard",args -> {
            Log.d("MONTORI","reciveYourFirstCard");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String idClient, idFirstCard,name;
                    Double value;
                    try {
                        JSONArray array = new JSONArray(args[0].toString());
                        for(int i=0;i< array.length();i++){
                            Log.d("ALFA-reciveYourFirstCard",i+") -> "+array.get(i).toString());
                            JSONObject json = new JSONObject(array.get(i).toString());
                            name = json.getString("name");
                            idClient = json.getString(strIdClient);
                            idFirstCard = json.getJSONObject("card").getString("id");
                            value = json.getJSONObject("card").getDouble("value");

                            if(idClient.equals(socket.getId())){
                                myIdFirstCard = idFirstCard;
                                myScore = value;
                                tvMyName.setText(name);
                            }else if(idClient2 == null) {
                                idClient2 = idClient;
                                tvNameP2.setText(name);
                            }else {
                                idClient3 = idClient;
                                tvNameP3.setText(name);
                            }

                        }
                    }catch(Exception e){}

                    tvMyScore.setText(""+myScore);
                    ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(myIdFirstCard).getIdImage());
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
                            isMyTurn=false;
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
                                idFCPlayer2 = client.getString("idFirstCard");
                            } else if (client.getString(strIdClient).equals(idClient3)) {
                                ivFCPlayer3.setImageResource(Deck.getIstance().getCardById(client.getString("idFirstCard")).getIdImage());
                                scoreP3 = client.getDouble("score");
                                tvScoreP3.setText("" + scoreP3);
                                idFCPlayer3 = client.getString("idFirstCard");
                            } else if (client.getString(strIdClient).equals(idServer)) {
                                ivFirstCardDealer.setImageResource(Deck.getIstance().getCardById(client.getString("idFirstCard")).getIdImage());
                                scoreDealer = client.getDouble("score");
                                tvScoreDealer.setText("" + scoreDealer);
                                idFCDealer = client.getString("idFirstCard");

                                Dialog d = new Dialog(G4PClientActivity.this);
                                d.setTitle("restart");
                                d.setCancelable(false);
                                d.setContentView(R.layout.dialog);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                d.show();
                                si = d.findViewById(R.id.btnSi);
                                no = d.findViewById(R.id.btnNo);

                                si.setOnClickListener(v->{
                                    JSONObject j = new JSONObject();
                                    try {
                                        j.put("idClient", socket.getId());
                                        j.put("bool", true);
                                        j.put("name",tvMyName.getText());
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
                                        j.put("name",tvMyName.getText());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    socket.getSocket().emit("continueGame",j ,(Ack) args -> {});
                                    socket.getSocket().emit("deletePlayer", socket.getId() ,(Ack) args -> {});
                                    socket.disconnection();
                                    Intent intent = new Intent(G4PClientActivity.this, MenuActivity.class);
                                    startActivity(intent);
                                });
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
                            idFCPlayer2 = idFirstCard;
                        } else if(idClient.equals(idClient3)){
                            Log.d("BETA","client3");
                            ivFCPlayer3.setImageResource(Deck.getIstance().getCardById(idFirstCard).getIdImage());
                            scoreP3 = score;
                            tvScoreP3.setText("" + scoreP3);
                            idFCPlayer3 = idFirstCard;
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

        // MyPlayer
        outState.putString("myIdFirstCard",myIdFirstCard);
        outState.putStringArrayList("myCards",Utilis.getIdCards(myCards));
        outState.putDouble("myScore",myScore);
        outState.putString("tvMyName",tvMyName.getText().toString());

        // Player 2
        outState.putString("idFCPlayer2",idFCPlayer2);
        outState.putStringArrayList("cardsP2",Utilis.getIdCards(cardsP2));
        outState.putString("tvScoreP2",tvScoreP2.getText().toString());
        outState.putString("idClient2",idClient2);
        outState.putString("tvNameP2",tvNameP2.getText().toString());
        if(scoreP2!=null)
            outState.putDouble("scoreP2",scoreP2);

        // Player 3
        outState.putString("idFCPlayer3",idFCPlayer3);
        outState.putStringArrayList("cardsP3",Utilis.getIdCards(cardsP3));
        outState.putString("tvScoreP3",tvScoreP3.getText().toString());
        outState.putString("idClient3",idClient3);
        outState.putString("tvNameP3",tvNameP3.getText().toString());
        if(scoreP3!=null)
            outState.putDouble("scoreP3",scoreP3);

        // Dealer
        outState.putString("idFCDealer",idFCDealer);
        outState.putStringArrayList("dealerCards",Utilis.getIdCards(dealerCards));
        outState.putString("tvScoreDealer",tvScoreDealer.getText().toString());
        outState.putString("tvNameP4",tvNameP4.getText().toString());
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
        myCards = Utilis.getCardsById(savedIstanceState.getStringArrayList("myCards"));
        myScore = savedIstanceState.getDouble("myScore");
        tvMyName.setText(savedIstanceState.getString("tvMyName"));

        ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(myIdFirstCard).getIdImage());
        tvMyScore.setText(""+myScore);

        myCardAdapter = new CardAdapterSmall(myCards,0);
        myRecyclerView.setAdapter(myCardAdapter);

        // Player 2
        idFCPlayer2 = savedIstanceState.getString("idFCPlayer2");
        cardsP2 = Utilis.getCardsById(savedIstanceState.getStringArrayList("cardsP2"));
        scoreP2 = savedIstanceState.getDouble("scoreP2");
        tvScoreP2.setText(savedIstanceState.getString("tvScoreP2"));
        idClient2 = savedIstanceState.getString("idClient2");
        tvNameP2.setText(savedIstanceState.getString("tvNameP2"));
        if(!tvScoreP2.getText().toString().equals(""))
            ivFCPlayer2.setImageResource(Deck.getIstance().getCardById(idFCPlayer2).getIdImage());

        adapterP2 = new CardAdapterSmall(cardsP2,90);
        rvPlayer2.setAdapter(adapterP2);

        // Player 3
        idFCPlayer3 = savedIstanceState.getString("idFCPlayer3");
        cardsP3= Utilis.getCardsById(savedIstanceState.getStringArrayList("cardsP3"));
        scoreP3 = savedIstanceState.getDouble("scoreP3");
        tvScoreP3.setText(savedIstanceState.getString("tvScoreP3"));
        idClient3 = savedIstanceState.getString("idClient3");
        tvNameP3.setText(savedIstanceState.getString("tvNameP3"));
        if(!tvScoreP3.getText().toString().equals(""))
            ivFCPlayer3.setImageResource(Deck.getIstance().getCardById(idFCPlayer3).getIdImage());

        adapterP3 = new CardAdapterSmall(cardsP3,270);
        rvPlayer3.setAdapter(adapterP3);

        // Dealer
        idFCDealer = savedIstanceState.getString("idFCDealer");
        dealerCards= Utilis.getCardsById(savedIstanceState.getStringArrayList("dealerCards"));
        scoreDealer = savedIstanceState.getDouble("scoreDealer");
        tvScoreDealer.setText(savedIstanceState.getString("tvScoreDealer"));
        tvNameP4.setText(savedIstanceState.getString("tvNameP4"));
        if(!tvScoreDealer.getText().toString().equals(""))
            ivFirstCardDealer.setImageResource(Deck.getIstance().getCardById(idFCDealer).getIdImage());

        cardAdapterDealer = new CardAdapterSmall(dealerCards,0);
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