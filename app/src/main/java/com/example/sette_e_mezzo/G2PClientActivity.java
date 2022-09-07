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


public class G2PClientActivity extends AppCompatActivity {

    SocketClass socket;
    String idServer;
    TextView tvResult;
    Boolean isMyTurn;

    // PLAYER
    ImageView ivMyFirstCard;
    Button btnCarta, btnStai;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    TextView tvNameMyPlayer;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapter myCardAdapter;
    Double myScore;

    //DELAER
    ImageView ivFirstCardDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    CardAdapter cardAdapterDealer;
    ArrayList<Card> dealerCards;
    String idFirstCardDealer;
    Double scoreDealer;
    TextView tvNameDealer;

    //variabili di servizio
    String idClient, idFirstCard, idCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g2p);

        socket = new SocketClass();

        isMyTurn=true;
        idServer = getIntent().getStringExtra(Utils.idServer);

        //  PLAYER
        ivMyFirstCard = findViewById(R.id.ivMyFirstCard);
        btnCarta = findViewById(R.id.btnCarta);
        btnStai = findViewById(R.id.btnStai);
        tvNameMyPlayer = findViewById(R.id.tvMyName);

        tvMyScore = findViewById(R.id.tvMyScore);

        myRecyclerView = findViewById(R.id.rvMyCards);
        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        myRecyclerView.setLayoutManager(layoutManager);
        myCards = new ArrayList<>();
        myCardAdapter = new CardAdapter(myCards);
        myRecyclerView.setAdapter(myCardAdapter);

        tvResult = findViewById(R.id.tvResult);

        btnCarta.setOnClickListener(v -> {
            JSONObject json = new JSONObject();
            try {
                json.put(Utils.idServer, idServer);
                json.put(Utils.idClient, socket.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.getSocket().emit(Utils.giveMeCard, json, (Ack) args -> {
            });

        });


        btnStai.setOnClickListener(v -> {

            JSONObject json = new JSONObject();
            try {
                json.put(Utils.idServer, idServer);
                json.put(Utils.idClient, socket.getId());
                json.put(Utils.score, myScore);
                json.put(Utils.idFirstCard, idFirstCard);
            } catch (Exception e) {
                e.printStackTrace();
            }

            isMyTurn = false;
            btnCarta.setVisibility(View.INVISIBLE);
            btnStai.setVisibility(View.INVISIBLE);

            isMyTurn=false;
            socket.getSocket().emit(Utils.terminateTurn, json, (Ack) args -> {
            });

        });


        //  DEALER
        ivFirstCardDealer = findViewById(R.id.ivFirstCardOtherPlayer);
        tvScoreDealer = findViewById(R.id.tvScoreOtherPlayer);
        tvNameDealer = findViewById(R.id.tvNameOtherPlayer);
        tvNameDealer.setText(R.string.dealer);

        dealerReyclerView = findViewById(R.id.rvCardsOtherPlayer);
        LinearLayoutManager lmDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        dealerReyclerView.setLayoutManager(lmDealer);

        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapter(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

        socket.getSocket().on(Utils.reciveYourFirstCard,args -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONArray array = new JSONArray(args[0].toString());
                        JSONObject json = new JSONObject(array.get(0).toString());
                        idFirstCard = json.getJSONObject(Utils.card).getString(Utils.id);
                        myScore = json.getJSONObject(Utils.card).getDouble(Utils.value);
                        tvNameMyPlayer.setText(json.getString(Utils.name));

                    }catch(Exception e){}
                    tvMyScore.setText(""+myScore);
                    ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(idFirstCard).getIdImage());
                }
            });
        });

        socket.getSocket().on(Utils.reciveCard,args -> {
            try {
                JSONObject json = new JSONObject(args[0].toString());
                idClient = json.getString(Utils.idClient);
                idCard = json.getJSONObject(Utils.card).getString(Utils.id);
                myScore += json.getJSONObject(Utils.card).getDouble(Utils.value);
            }catch(Exception e){}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if(socket.getId().equals(idClient)) {
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
                                json.put(Utils.idFirstCard,idFirstCard);
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            socket.getSocket().emit(Utils.terminateTurn,json,(Ack) args -> {});
                        }
                    }else{
                        dealerCards.add(Deck.getIstance().getCardById(idCard));
                        cardAdapterDealer.notifyItemInserted(dealerCards.size() - 1);
                    }
                }
            });
        });


        /*              OLD CLOSEROUND

        socket.getSocket().on(Utils.closeRound,args -> {
            Log.d("MONTORI",Utils.closeRound);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Dialog d = new Dialog(G2PClientActivity.this);
                        si = d.findViewById(R.id.btnSi);
                        no = d.findViewById(R.id.btnNo);
                        tvResultDialog = d.findViewById(R.id.tvResultDialog);
                        tvP1 = d.findViewById(R.id.tvP1);
                        tvP2 = d.findViewById(R.id.tvP2);
                        d.setTitle("restart");
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog);

                        JSONObject json = new JSONObject(args[0].toString());
                        idFirstCardDealer = json.getString(Utils.idFirstCard);
                        scoreDealer = json.getDouble(Utils.score);

                        ivFirstCardDealer.setImageResource(Deck.getIstance().getCardById(idFirstCardDealer).getIdImage());
                        tvScoreDealer.setText(""+scoreDealer);


                        if(tvResult.getText().equals("")) {
                            Log.d("DIALOG","tvResult vuoto");
                            if (myScore > scoreDealer) {
                                tvResult.setText(R.string.win);
                                //tvResultDialog.setText(R.string.win);
                                tvP1.setText(tvNameMyPlayer.getText()+" "+tvMyScore.getText());
                                //tvP2.setText(tvNameDealer.getText()+" "+tvScoreDealer.getText());
                            } else {
                                tvResult.setText(R.string.lose);
                                //tvResultDialog.setText(R.string.lose);
                                tvP1.setText(tvNameDealer.getText()+" "+tvScoreDealer.getText());
                                //tvP2.setText(tvNameMyPlayer.getText()+" "+tvMyScore.getText());
                            }
                        }

                        d.show();


                        si.setOnClickListener(v->{
                            JSONObject j = new JSONObject();
                            try {
                                j.put(Utils.idClient, socket.getId());
                                j.put(Utils.bool, true);
                                j.put(Utils.name,tvNameMyPlayer.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            socket.getSocket().emit(Utils.continueGame, j ,(Ack) args -> {});
                            d.hide();
                            d.cancel();
                            finish();
                        });

                        no.setOnClickListener(v -> {
                            JSONObject j = new JSONObject();
                            try {
                                j.put(Utils.idClient, socket.getId());
                                j.put(Utils.bool, false);
                                j.put(Utils.name,tvNameMyPlayer.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            socket.getSocket().emit(Utils.continueGame,j ,(Ack) args -> {});
                            socket.getSocket().emit(Utils.deletePlayer, socket.getId() ,(Ack) args -> {});
                            socket.disconnection();
                            Intent intent = new Intent(G2PClientActivity.this, MenuActivity.class);
                            startActivity(intent);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("DIALOG","errore: "+e.getMessage());
                    }
                }
            });
        });*/

        socket.getSocket().on(Utils.closeRound,args -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String tmpP1="", tmpP2="";

                        JSONObject json = new JSONObject(args[0].toString());
                        idFirstCardDealer = json.getString(Utils.idFirstCard);
                        scoreDealer = json.getDouble(Utils.score);

                        ivFirstCardDealer.setImageResource(Deck.getIstance().getCardById(idFirstCardDealer).getIdImage());
                        tvScoreDealer.setText(""+scoreDealer);

                        if(tvResult.getText().equals("")) {
                            if (myScore > scoreDealer) {
                                tvResult.setText(R.string.win);
                                tmpP1 = tvNameMyPlayer.getText()+" "+tvMyScore.getText();
                                tmpP2 = tvNameDealer.getText()+" "+tvScoreDealer.getText();
                            } else {
                                tvResult.setText(R.string.lose);
                                tmpP1 = tvNameDealer.getText()+" "+tvScoreDealer.getText();
                                tmpP2 = tvNameMyPlayer.getText()+" "+tvMyScore.getText();
                            }
                        }else{
                            tmpP1 = tvNameDealer.getText()+" "+tvScoreDealer.getText();
                            tmpP2 = tvNameMyPlayer.getText()+" "+tvMyScore.getText();
                            //tmpP2 = getString(R.string.over);
                        }

                        CustomDialog d = new CustomDialog(G2PClientActivity.this,tvResult.getText().toString(),tmpP1,tmpP2,"","");
                        d.setTitle("restart");
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog);
                        d.show();

                        d.getBtnYes().setOnClickListener(v->{
                            JSONObject j = new JSONObject();
                            try {
                                j.put(Utils.idClient, socket.getId());
                                j.put(Utils.bool, true);
                                j.put(Utils.name,tvNameMyPlayer.getText().toString());
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
                                j.put(Utils.name,tvNameMyPlayer.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            socket.getSocket().emit(Utils.continueGame,j ,(Ack) args -> {});
                            socket.getSocket().emit(Utils.deletePlayer, socket.getId() ,(Ack) args -> {});
                            socket.disconnection();
                            Intent intent = new Intent(G2PClientActivity.this, MenuActivity.class);
                            startActivity(intent);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Player
        outState.putString(Utils.idFirstCard,idFirstCard);
        outState.putStringArrayList("myCards", Utils.getIdCards(myCards));
        outState.putDouble("myScore",myScore);

        //Dealer
        outState.putString("idFirstCardDealer",idFirstCardDealer);
        outState.putStringArrayList("dealerCards", Utils.getIdCards(dealerCards));
        if(scoreDealer!=null)
            outState.putDouble("scoreDealer",scoreDealer);

        outState.putString("tvResult",tvResult.getText().toString());

        outState.putBoolean("isMyTurn",isMyTurn);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedIstanceState){
        super.onRestoreInstanceState(savedIstanceState);

        // Player
        idFirstCard = savedIstanceState.getString(Utils.idFirstCard);
        myCards = Utils.getCardsById(savedIstanceState.getStringArrayList("myCards"));
        myScore = savedIstanceState.getDouble("myScore");

        ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(idFirstCard).getIdImage());
        tvMyScore.setText(""+myScore);

        myCardAdapter = new CardAdapter(myCards);
        myRecyclerView.setAdapter(myCardAdapter);

        tvResult.setText(savedIstanceState.getString("tvResult"));

        isMyTurn = savedIstanceState.getBoolean("isMyTurn");
        if(!isMyTurn){
            btnCarta.setVisibility(View.INVISIBLE);
            btnStai.setVisibility(View.INVISIBLE);
        }

        // Dealer
        idFirstCardDealer = savedIstanceState.getString("idFirstCardDealer");
        dealerCards = Utils.getCardsById(savedIstanceState.getStringArrayList("dealerCards"));


        cardAdapterDealer = new CardAdapter(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

        scoreDealer = savedIstanceState.getDouble("scoreDealer");
        if(tvResult.getText().toString()!="") {
            ivFirstCardDealer.setImageResource(Deck.getIstance().getCardById(idFirstCardDealer).getIdImage());
            tvScoreDealer.setText(""+scoreDealer);
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