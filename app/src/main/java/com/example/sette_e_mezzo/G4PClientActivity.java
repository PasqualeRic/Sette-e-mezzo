package com.example.sette_e_mezzo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

    SocketClass socket = new SocketClass();
    TextView tvResult;
    Button btnCarta, btnStai;
    Boolean isMyTurn;

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
        idServer = getIntent().getStringExtra(Utils.idServer);

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
                json.put(Utils.idServer,idServer);
                json.put(Utils.idClient,socket.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.getSocket().emit(Utils.giveMeCard, json ,(Ack) args -> {});
        });

        btnStai.setOnClickListener(v -> {
            JSONObject json = new JSONObject();
            try{
                json.put(Utils.idServer,idServer);
                json.put(Utils.idClient,socket.getId());
                json.put(Utils.score,myScore);
                json.put(Utils.idFirstCard,myIdFirstCard);
            }catch(Exception e){
                e.printStackTrace();
            }

            btnCarta.setVisibility(View.INVISIBLE);
            btnStai.setVisibility(View.INVISIBLE);

            isMyTurn=false;
            socket.getSocket().emit(Utils.terminateTurn,json,(Ack) args -> {});
        });

        socket.getSocket().on(Utils.reciveYourFirstCard,args -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String idClient, idFirstCard,name;
                    Double value;
                    try {
                        JSONArray array = new JSONArray(args[0].toString());
                        for(int i=0;i< array.length();i++){
                            JSONObject json = new JSONObject(array.get(i).toString());
                            name = json.getString(Utils.name);
                            idClient = json.getString(Utils.idClient);
                            idFirstCard = json.getJSONObject(Utils.card).getString(Utils.id);
                            value = json.getJSONObject(Utils.card).getDouble(Utils.value);

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


        socket.getSocket().on(Utils.myTurn, args -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isMyTurn=true;
                    btnCarta.setVisibility(View.VISIBLE);
                    btnStai.setVisibility(View.VISIBLE);
                }
            });
        });

        socket.getSocket().on(Utils.reciveCard,args -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String idClient="", idCard="";
                    Double score=0.0;

                    try {
                        JSONObject json = new JSONObject(args[0].toString());
                        idClient = json.getString(Utils.idClient);
                        idCard = json.getJSONObject(Utils.card).getString(Utils.id);
                        score = json.getJSONObject(Utils.card).getDouble(Utils.value);

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
                                json.put(Utils.idServer,idServer);
                                json.put(Utils.idClient,socket.getId());
                                json.put(Utils.score,myScore);
                                json.put(Utils.idFirstCard,myIdFirstCard);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            socket.getSocket().emit(Utils.terminateTurn,json,(Ack) args -> {});
                        }
                    }else if(idClient.equals(idClient2)){
                        cardsP2.add(Deck.getIstance().getCardById(idCard));
                        adapterP2.notifyItemInserted(cardsP2.size() - 1);
                    }else if(idClient.equals(idClient3)){
                        cardsP3.add(Deck.getIstance().getCardById(idCard));
                        adapterP3.notifyItemInserted(cardsP3.size() - 1);
                    }else{
                        dealerCards.add(Deck.getIstance().getCardById(idCard));
                        cardAdapterDealer.notifyItemInserted(dealerCards.size() - 1);
                    }
                }
            });
        });

        socket.getSocket().on(Utils.closeRound,args -> {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String tmpP1="", tmpP2="", tmpP3="", tmpP4="";
                    try {
                        JSONArray json = new JSONArray(args[0].toString());
                        for (int i = 0; i < json.length(); i++) {

                            JSONObject client = json.getJSONObject(i);
                            if (client.getString(Utils.idClient).equals(idClient2)) {
                                ivFCPlayer2.setImageResource(Deck.getIstance().getCardById(client.getString(Utils.idFirstCard)).getIdImage());
                                scoreP2 = client.getDouble(Utils.score);
                                tvScoreP2.setText("" + scoreP2);
                                idFCPlayer2 = client.getString(Utils.idFirstCard);
                            } else if (client.getString(Utils.idClient).equals(idClient3)) {
                                ivFCPlayer3.setImageResource(Deck.getIstance().getCardById(client.getString(Utils.idFirstCard)).getIdImage());
                                scoreP3 = client.getDouble(Utils.score);
                                tvScoreP3.setText("" + scoreP3);
                                idFCPlayer3 = client.getString(Utils.idFirstCard);
                            } else if (client.getString(Utils.idClient).equals(idServer)) {
                                ivFirstCardDealer.setImageResource(Deck.getIstance().getCardById(client.getString(Utils.idFirstCard)).getIdImage());
                                scoreDealer = client.getDouble(Utils.score);
                                tvScoreDealer.setText("" + scoreDealer);
                                idFCDealer = client.getString(Utils.idFirstCard);
                            }
                        }


                        //se tvResult è vuota myScore è <=7.5
                        if(tvResult.getText().equals("")){
                            if((scoreDealer<=7.5 && myScore<=scoreDealer)){
                                tvResult.setText(R.string.lose);
                                tmpP1=tvNameP4.getText()+" "+tvScoreDealer.getText();
                                tmpP2=tvNameP2.getText()+" "+tvScoreP2.getText();
                                tmpP3=tvNameP3.getText()+" "+tvScoreP3.getText();
                                tmpP4=tvMyName.getText()+" "+tvMyScore.getText();
                            }else if((scoreP2<=7.5 && myScore>=scoreP2) && (scoreP3<=7.5 && myScore>=scoreP3) && (scoreDealer<=7.5 && myScore>scoreDealer)){
                                // il mio punteggio è il più alto e non ha sballato nessuno
                                tvResult.setText(R.string.win);
                                tmpP1=tvMyName.getText()+" "+tvMyScore.getText();
                                tmpP2=tvNameP2.getText()+" "+tvScoreP2.getText();
                                tmpP3=tvNameP3.getText()+" "+tvScoreP3.getText();
                                tmpP4=tvNameP4.getText()+" "+tvScoreDealer.getText();
                            }else if(scoreP2>7.5 && (scoreP3<=7.5 && myScore>=scoreP3) && (scoreDealer<=7.5 && myScore>scoreDealer)){
                                // il mio punteggio è il più alto e ha sballato p2
                                tvResult.setText(R.string.win);
                                tmpP1=tvMyName.getText()+" "+tvMyScore.getText();
                                tmpP2=tvNameP4.getText()+" "+tvScoreDealer.getText();
                                tmpP3=tvNameP3.getText()+" "+tvScoreP3.getText();
                                tmpP4=tvNameP2.getText()+" "+tvScoreP2.getText();
                            }else if((scoreP2<=7.5 && myScore>=scoreP2) && scoreP3>7.5 &&(scoreDealer<=7.5 && myScore>scoreDealer)){
                                // il mio punteggio è il più alto e ha sballato p3
                                tvResult.setText(R.string.win);
                                tmpP1=tvMyName.getText()+" "+tvMyScore.getText();
                                tmpP2=tvNameP4.getText()+" "+tvScoreDealer.getText();
                                tmpP3=tvNameP2.getText()+" "+tvScoreP2.getText();
                                tmpP4=tvNameP3.getText()+" "+tvScoreP3.getText();
                            }else if((scoreP2<=7.5 && myScore>=scoreP2) && (scoreP3<=7.5 && myScore>=scoreP3) && scoreDealer>7.5){
                                // il mio punteggio è il più alto e ha sballato dealer
                                tvResult.setText(R.string.win);
                                tmpP1=tvMyName.getText()+" "+tvMyScore.getText();
                                tmpP2=tvNameP2.getText()+" "+tvScoreP2.getText();
                                tmpP3=tvNameP3.getText()+" "+tvScoreP3.getText();
                                tmpP4=tvNameP4.getText()+" "+tvScoreDealer.getText();
                            }else if(scoreP2>7.5 && scoreP3>7.5  && (scoreDealer<=7.5 && myScore>scoreDealer)){
                                // il mio punteggio è il più alto e hanno sballato p2 e p3
                                tvResult.setText(R.string.win);
                                tmpP1=tvMyName.getText()+" "+tvMyScore.getText();
                                tmpP2=tvNameP4.getText()+" "+tvScoreDealer.getText();
                                tmpP3=tvNameP3.getText()+" "+tvScoreP3.getText();
                                tmpP4=tvNameP2.getText()+" "+tvScoreP2.getText();
                            }else if(scoreP2>7.5 && (scoreP3<=7.5 && myScore>=scoreP3) && scoreDealer>7.5){
                                // il mio punteggio è il più alto e hanno sballato p2 e dealer
                                tvResult.setText(R.string.win);
                                tmpP1=tvMyName.getText()+" "+tvMyScore.getText();
                                tmpP2=tvNameP3.getText()+" "+tvScoreP3.getText();
                                tmpP3=tvNameP4.getText()+" "+tvScoreDealer.getText();
                                tmpP4=tvNameP2.getText()+" "+tvScoreP2.getText();
                            }else if((scoreP2<=7.5 && myScore>=scoreP2) && scoreP3>7.5 && scoreDealer>7.5){
                                // il mio punteggio è il più alto e hanno sballato p3 e dealer
                                tvResult.setText(R.string.win);
                                tmpP1=tvMyName.getText()+" "+tvMyScore.getText();
                                tmpP4=tvNameP3.getText()+" "+tvScoreP3.getText();
                                tmpP3=tvNameP4.getText()+" "+tvScoreDealer.getText();
                                tmpP2=tvNameP2.getText()+" "+tvScoreP2.getText();
                            }else if(scoreP2>7.5 && scoreP3>7.5 && scoreDealer>7.5){
                                // il mio punteggio è il più alto e hanno sballato tutti
                                tvResult.setText(R.string.win);
                                tmpP1=tvMyName.getText()+" "+tvMyScore.getText();
                                tmpP4=tvNameP3.getText()+" "+tvScoreP3.getText();
                                tmpP3=tvNameP4.getText()+" "+tvScoreDealer.getText();
                                tmpP2=tvNameP2.getText()+" "+tvScoreP2.getText();
                            }else{
                                tvResult.setText(R.string.lose);
                                tmpP2=tvMyName.getText()+" "+tvMyScore.getText();
                                tmpP4=tvNameP3.getText()+" "+tvScoreP3.getText();
                                tmpP1=tvNameP4.getText()+" "+tvScoreDealer.getText();
                                tmpP3=tvNameP2.getText()+" "+tvScoreP2.getText();
                            }
                        }else{
                            tmpP1=tvNameP4.getText()+" "+tvScoreDealer.getText();
                            tmpP2=tvNameP2.getText()+" "+tvScoreP2.getText();
                            tmpP3=tvNameP3.getText()+" "+tvScoreP3.getText();
                            tmpP4=tvMyName.getText()+" "+tvMyScore.getText();
                        }

                        CustomDialog d = new CustomDialog(G4PClientActivity.this,tvResult.getText().toString(),tmpP1,tmpP2,tmpP3,tmpP4);
                        d.setTitle("restart");
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog);
                        d.show();

                        d.getBtnYes().setOnClickListener(v->{
                            JSONObject j = new JSONObject();
                            try {
                                j.put(Utils.idClient, socket.getId());
                                j.put(Utils.bool, true);
                                j.put(Utils.name,tvMyName.getText());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            socket.getSocket().emit(Utils.continueGame, j ,(Ack) args -> {});
                            d.hide();
                            d.cancel();
                            finish();
                        });

                        d.getBtnNo().setOnClickListener(v -> {
                            JSONObject j = new JSONObject();
                            try {
                                j.put(Utils.idClient, socket.getId());
                                j.put(Utils.bool, false);
                                j.put(Utils.name,tvMyName.getText());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            socket.getSocket().emit(Utils.continueGame,j ,(Ack) args -> {});
                            socket.getSocket().emit(Utils.deletePlayer, socket.getId() ,(Ack) args -> {});
                            socket.disconnection();
                            Intent intent = new Intent(G4PClientActivity.this, MenuActivity.class);
                            startActivity(intent);
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        socket.getSocket().on(Utils.overSize,args -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String idFirstCard, idClient;
                        Double score;

                        JSONObject json = new JSONObject(args[0].toString());
                        idClient = json.getString(Utils.idClient);
                        idFirstCard = json.getString(Utils.idFirstCard);
                        score = json.getDouble(Utils.score);

                        if (idClient.equals(idClient2)) {

                            ivFCPlayer2.setImageResource(Deck.getIstance().getCardById(idFirstCard).getIdImage());
                            scoreP2 = score;
                            tvScoreP2.setText("" + scoreP2);
                            idFCPlayer2 = idFirstCard;
                        } else if(idClient.equals(idClient3)){

                            ivFCPlayer3.setImageResource(Deck.getIstance().getCardById(idFirstCard).getIdImage());
                            scoreP3 = score;
                            tvScoreP3.setText("" + scoreP3);
                            idFCPlayer3 = idFirstCard;
                        }

                    } catch (JSONException e) {
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
        outState.putStringArrayList("myCards", Utils.getIdCards(myCards));
        outState.putDouble("myScore",myScore);
        outState.putString("tvMyName",tvMyName.getText().toString());

        // Player 2
        outState.putString("idFCPlayer2",idFCPlayer2);
        outState.putStringArrayList("cardsP2", Utils.getIdCards(cardsP2));
        outState.putString("tvScoreP2",tvScoreP2.getText().toString());
        outState.putString("idClient2",idClient2);
        outState.putString("tvNameP2",tvNameP2.getText().toString());
        if(scoreP2!=null)
            outState.putDouble("scoreP2",scoreP2);

        // Player 3
        outState.putString("idFCPlayer3",idFCPlayer3);
        outState.putStringArrayList("cardsP3", Utils.getIdCards(cardsP3));
        outState.putString("tvScoreP3",tvScoreP3.getText().toString());
        outState.putString("idClient3",idClient3);
        outState.putString("tvNameP3",tvNameP3.getText().toString());
        if(scoreP3!=null)
            outState.putDouble("scoreP3",scoreP3);

        // Dealer
        outState.putString("idFCDealer",idFCDealer);
        outState.putStringArrayList("dealerCards", Utils.getIdCards(dealerCards));
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
        myCards = Utils.getCardsById(savedIstanceState.getStringArrayList("myCards"));
        myScore = savedIstanceState.getDouble("myScore");
        tvMyName.setText(savedIstanceState.getString("tvMyName"));

        ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(myIdFirstCard).getIdImage());
        tvMyScore.setText(""+myScore);

        myCardAdapter = new CardAdapterSmall(myCards,0);
        myRecyclerView.setAdapter(myCardAdapter);

        // Player 2
        idFCPlayer2 = savedIstanceState.getString("idFCPlayer2");
        cardsP2 = Utils.getCardsById(savedIstanceState.getStringArrayList("cardsP2"));
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
        cardsP3= Utils.getCardsById(savedIstanceState.getStringArrayList("cardsP3"));
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
        dealerCards= Utils.getCardsById(savedIstanceState.getStringArrayList("dealerCards"));
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
        socket.getSocket().off(Utils.overSize);
        socket.getSocket().off(Utils.closeRound);
        socket.getSocket().off(Utils.reciveCard);
        socket.getSocket().off(Utils.myTurn);
        socket.getSocket().off(Utils.reciveYourFirstCard);
    }
   @Override
    protected void onStop(){
        super.onStop();
        socket.getSocket().off(Utils.overSize);
        socket.getSocket().off(Utils.closeRound);
        socket.getSocket().off(Utils.reciveCard);
        socket.getSocket().off(Utils.myTurn);
        socket.getSocket().off(Utils.reciveYourFirstCard);
    }
}