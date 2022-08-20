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

public class G3PClientActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    String idServer;
    TextView tvResult;
    // PLAYER1
    Button btnCarta, btnStai;
    ImageView imageViewPlayer1;
    ArrayList<Card> myCardsPlayer1;
    TextView tvScorePlayer1;
    RecyclerView recyclerViewPlayer1;
    RecyclerView.LayoutManager layoutManagerP1;
    CardAdapterSmall myCardAdapterP1;
    Double scoreP1;
    String myIdFirstCard, myId;
    // PLAYER2
    ImageView imageViewPlayer2;
    TextView tvScorePlayer2;
    ArrayList<Card> myCardsPlayer2;
    TextView tvP2;
    RecyclerView recyclerViewPlayer2;
    RecyclerView.LayoutManager layoutManagerP2;
    CardAdapterSmallO myCardAdapterP2;
    Double scoreP2;
    String idClient2;

    //DELAER
    ImageView imageViewDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    RecyclerView.LayoutManager layoutManagerDealer;
    CardAdapterSmall cardAdapterDealer;
    ArrayList<Card> dealerCards;
    Double scoreDealer;

    Double myScore;
    String idFirstCard, idCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3_players);

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
        myCardAdapterP1 = new CardAdapterSmall(myCardsPlayer1);
        recyclerViewPlayer1.setAdapter(myCardAdapterP1);

        // Player 2 - Sinistra
        imageViewPlayer2 = findViewById(R.id.imageViewPlayer2);
        myCardsPlayer2 = new ArrayList<>();
        tvScorePlayer2 = findViewById(R.id.tvScorePlayer1);
        layoutManagerP2 = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        recyclerViewPlayer2 = findViewById(R.id.recyclerViewPlayer2);
        recyclerViewPlayer2.setLayoutManager(layoutManagerP2);
        myCardAdapterP2 = new CardAdapterSmallO(myCardsPlayer2);
        recyclerViewPlayer2.setAdapter(myCardAdapterP2);


        // Dealer
        imageViewDealer = findViewById(R.id.imageViewDealer);
        tvScoreDealer = findViewById(R.id.tvScoreDealer);
        layoutManagerDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        dealerReyclerView = findViewById(R.id.recyclerViewDealer);
        dealerReyclerView.setLayoutManager(layoutManagerDealer);
        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapterSmall(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

       /* btnCarta.setOnClickListener(v -> {
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

            socket.getSocket().emit("terminateTurn",json,(Ack) args -> {});
        });*/
        Log.d("prima", "prima");

        socket.getSocket().on("reciveYourFirstCard",args -> {
            Log.d("BETA", "reciveYourFirstCard - G3P");

            String idClient, idFirstCard;
            Double value;
            Log.d("BETA", "args[0]:"+args[0].toString());

            try {
                JSONArray array = new JSONArray(args[0].toString());
                Log.d("array", array+"");
                for(int i=0;i< array.length();i++){
                    Log.d("ALFA-reciveYourFirstCard",i+") -> "+array.get(i).toString());
                    JSONObject json = new JSONObject(array.get(i).toString());
                    idClient = json.getString("idClient");
                    idFirstCard = json.getJSONObject("card").getString("id");
                    value = json.getJSONObject("card").getDouble("value");
                    if(idClient.equals(socket.getId())){
                        myId = idClient;
                        myIdFirstCard = idFirstCard;
                        myScore = value;

                    }else if(idClient2 == null)
                        idClient2 = idClient;
                }

            }catch(Exception e){ Log.d("BETA", "errore");}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvScorePlayer1.setText(""+myScore);
                    imageViewPlayer1.setImageResource(Deck.getIstance().getCardById(myIdFirstCard).getIdImage());
                }
            });
        });
        /*socket.getSocket().on("myTurn", args -> {
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
                        idClient = json.getString("idClient");
                        idCard = json.getJSONObject("card").getString("id");
                        score = json.getJSONObject("card").getDouble("value");

                    }catch(Exception e){}

                    if(socket.getId().equals(idClient)) {
                        Log.d("ALFA","IO");
                        myScore+=score;
                        Log.d("ALFA","tvMyScore");
                        tvScorePlayer1.setText(""+myScore);
                        Log.d("ALFA","myCards");
                        myCardsPlayer1.add(Deck.getIstance().getCardById(idCard));
                        Log.d("ALFA","myCardAdapter");
                        myCardAdapterP1.notifyItemInserted(myCardsPlayer1.size() - 1);

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
        });*/
    }
}