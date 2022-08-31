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


public class G2PClientActivity extends AppCompatActivity {

    SocketClass socket;
    String idServer;
    TextView tvResult;
    Boolean isMyTurn;

    Button si, no;

    // PLAYER
    ImageView ivMyFirstCard;
    Button btnCarta, btnStai;
    ArrayList<Card> myCards;
    TextView tvMyScore;
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

    //variabili di servizio
    String idClient, idFirstCard, idCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g2p);

        socket = new SocketClass();

        isMyTurn=true;
        idServer = getIntent().getStringExtra("idServer");

        //  PLAYER
        ivMyFirstCard = findViewById(R.id.ivMyFirstCard);
        btnCarta = findViewById(R.id.btnCarta);
        btnStai = findViewById(R.id.btnStai);

        tvMyScore = findViewById(R.id.tvMyScore);

        myRecyclerView = findViewById(R.id.rvMyCards);
        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        myRecyclerView.setLayoutManager(layoutManager);
        myCards = new ArrayList<>();
        myCardAdapter = new CardAdapter(myCards);
        myRecyclerView.setAdapter(myCardAdapter);

        tvResult = findViewById(R.id.tvResult);

        btnCarta.setOnClickListener(v -> {
            Log.d("debug", "giveMeCard");
            JSONObject json = new JSONObject();
            try {
                json.put("idServer", idServer);
                json.put("idClient", socket.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.getSocket().emit("giveMeCard", json, (Ack) args -> {
            });

        });


        btnStai.setOnClickListener(v -> {

            JSONObject json = new JSONObject();
            try {
                json.put("idServer", idServer);
                json.put("idClient", socket.getId());
                json.put("score", myScore);
                json.put("idFirstCard", idFirstCard);
            } catch (Exception e) {
                e.printStackTrace();
            }

            isMyTurn = false;
            btnCarta.setVisibility(View.INVISIBLE);
            btnStai.setVisibility(View.INVISIBLE);

            isMyTurn=false;
            socket.getSocket().emit("terminateTurn", json, (Ack) args -> {
            });

        });


        //  DEALER
        ivFirstCardDealer = findViewById(R.id.ivFirstCardOtherPlayer);
        tvScoreDealer = findViewById(R.id.tvScoreOtherPlayer);

        dealerReyclerView = findViewById(R.id.rvCardsOtherPlayer);
        LinearLayoutManager lmDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        dealerReyclerView.setLayoutManager(lmDealer);

        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapter(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

        socket.getSocket().on("reciveYourFirstCard",args -> {
            Log.d("on", "on");
            try {
                JSONArray array = new JSONArray(args[0].toString());
                JSONObject json = new JSONObject(array.get(0).toString());
                //idClient = json.getString("idClient");
                idFirstCard = json.getJSONObject("card").getString("id");
                myScore = json.getJSONObject("card").getDouble("value");

            }catch(Exception e){}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMyScore.setText(""+myScore);
                    ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(idFirstCard).getIdImage());
                }
            });
        });

        socket.getSocket().on("reciveCard",args -> {
            Log.d("BETA","idCard: "+idCard);
            try {

                JSONObject json = new JSONObject(args[0].toString());
                idClient = json.getString("idClient");
                idCard = json.getJSONObject("card").getString("id");
                myScore += json.getJSONObject("card").getDouble("value");
                Log.d("BETA","idClient: "+idClient);
                Log.d("BETA","idCard: "+idCard);
            }catch(Exception e){ Log.d("BETA","errore json");}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if(socket.getId().equals(idClient)) {
                        tvMyScore.setText(""+myScore);
                        myCards.add(Deck.getIstance().getCardById(idCard));
                        Log.d("BETA"," card aggiunta ");
                        myCardAdapter.notifyItemInserted(myCards.size() - 1);
                        Log.d("BETA"," dopo il notify ");

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
                                json.put("idFirstCard",idFirstCard);
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            socket.getSocket().emit("terminateTurn",json,(Ack) args -> {});
                        }
                    }else{
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
                        JSONObject json = new JSONObject(args[0].toString());
                        idFirstCardDealer = json.getString("idFirstCard");
                        scoreDealer = json.getDouble("score");
                        Log.d("BETA","closeRound - scoreDealer:"+scoreDealer);
                        ivFirstCardDealer.setImageResource(Deck.getIstance().getCardById(idFirstCardDealer).getIdImage());
                        tvScoreDealer.setText(""+scoreDealer);
                        if(tvResult.getText().equals("")) {
                            if (myScore > scoreDealer) {
                                tvResult.setText(R.string.win);
                            } else {
                                tvResult.setText(R.string.lose);
                            }
                            Dialog d = new Dialog(G2PClientActivity.this);
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
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                socket.getSocket().emit("continueGame",j ,(Ack) args -> {});
                                socket.getSocket().emit("deletePlayer", socket.getId() ,(Ack) args -> {});
                                socket.disconnection();
                                Intent intent = new Intent(G2PClientActivity.this, MenuActivity.class);
                                startActivity(intent);
                            });
                        }
                    } catch (Exception e) {}
                }
            });
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Player
        outState.putString("idFirstCard",idFirstCard);
        outState.putStringArrayList("myCards",Utilis.getIdCards(myCards));
        outState.putDouble("myScore",myScore);

        //Dealer
        outState.putString("idFirstCardDealer",idFirstCardDealer);
        outState.putStringArrayList("dealerCards",Utilis.getIdCards(dealerCards));
        if(scoreDealer!=null)
            outState.putDouble("scoreDealer",scoreDealer);

        outState.putString("tvResult",tvResult.getText().toString());

        outState.putBoolean("isMyTurn",isMyTurn);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedIstanceState){
        super.onRestoreInstanceState(savedIstanceState);

        // Player
        idFirstCard = savedIstanceState.getString("idFirstCard");
        myCards = Utilis.getCardsById(savedIstanceState.getStringArrayList("myCards"));
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
        dealerCards = Utilis.getCardsById(savedIstanceState.getStringArrayList("dealerCards"));


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