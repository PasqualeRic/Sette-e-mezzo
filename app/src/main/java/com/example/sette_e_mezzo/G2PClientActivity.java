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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;


public class G2PClientActivity extends AppCompatActivity {

    SocketClass socket = new SocketClass();
    String idServer;
    TextView tvResult;

    // PLAYER
    ImageView ivMyFirstCard;
    Button btnCarta, btnStai;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapter myCardAdapter;

    //DELAER
    ImageView ivFirstCardDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    CardAdapter cardAdapterDealer;
    ArrayList<Card> dealerCards;

    Double myScore;
    String idClient, idFirstCard, idCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g2p);

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
            Log.d("debug","giveMeCard");
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
                json.put("idFirstCard",idFirstCard);
            }catch(Exception e){
                e.printStackTrace();
            }

            btnCarta.setVisibility(View.INVISIBLE);
            btnStai.setVisibility(View.INVISIBLE);

            socket.getSocket().emit("terminateTurn",json,(Ack) args -> {});

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
            try {
                JSONObject json = new JSONObject(args[0].toString());
                idClient = json.getString("idClient");
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

            try {
                Log.wtf("prova", "p"+args[1]);
                JSONObject json = new JSONObject(args[0].toString());
                idClient = json.getString("idClient");
                idCard = json.getJSONObject("card").getString("id");
                myScore += json.getJSONObject("card").getDouble("value");

            }catch(Exception e){}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(socket.getId().equals(idClient)) {
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
                                json.put("idClient",socket.getId());
                                json.put("score",myScore);
                                json.put("idFirstCard",idFirstCard);
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            socket.getSocket().emit("terminateTurn",json,(Ack) args -> {});
                        }
                    }else{
                        Log.d("debug","else");
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
                        String idFirstCardDealer = json.getString("idFirstCard");
                        Double scoreDealer = json.getDouble("score");
                        ivFirstCardDealer.setImageResource(Deck.getIstance().getCardById(idFirstCardDealer).getIdImage());
                        tvScoreDealer.setText("" + scoreDealer);
                        if(tvResult.getText().equals("")) {
                            if (myScore > scoreDealer) {
                                tvResult.setText(R.string.win);
                            } else {
                                tvResult.setText(R.string.lose);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            });
        });

    }

}