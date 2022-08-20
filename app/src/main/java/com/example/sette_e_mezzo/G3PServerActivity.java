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

public class G3PServerActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    TextView tvResult;
    Integer indexClient;  //indice per tenere traccia del client di turno
    ArrayList<String> idClients;


    // PLAYER1 player bottom
    Button btnCarta, btnStai;
    ImageView imageViewPlayer1;
    ArrayList<Card> myCardsPlayer1;
    TextView tvScorePlayer1;
    RecyclerView recyclerViewPlayer1;
    RecyclerView.LayoutManager layoutManagerP1;
    CardAdapterSmall myCardAdapterP1;
    Double scoreP1;
    Card firstCard;

    // PLAYER2 player left
    String idClient2;
    ImageView imageViewPlayer2;
    TextView tvScorePlayer2;
    ArrayList<Card> myCardsPlayer2;
    TextView tvP2;
    RecyclerView recyclerViewPlayer2;
    RecyclerView.LayoutManager layoutManagerP2;
    CardAdapterSmallO myCardAdapterP2;
    Double scoreP2;

    //DELAER player top
    String idClient3;
    ImageView imageViewDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    CardAdapterSmall cardAdapterDealer;
    ArrayList<Card> dealerCards;
    Double scoreDealer;

    Double myScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3_players);
        Log.d("cristo server", "cazzo");
        idClients = getIntent().getStringArrayListExtra("idClients");

        tvResult = findViewById(R.id.tvResultG3);
        btnCarta = findViewById(R.id.btnCarta3);
        btnStai = findViewById(R.id.btnStai3);


        //PLAYER1
        imageViewPlayer1 = findViewById(R.id.imageViewPlayer1);
        recyclerViewPlayer1 = findViewById(R.id.recyclerViewPlayer1);
        tvScorePlayer1 = findViewById(R.id.tvScorePlayer1);
        layoutManagerP1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerViewPlayer1.setLayoutManager(layoutManagerP1);
        myCardsPlayer1 = new ArrayList<>();
        myCardAdapterP1 = new CardAdapterSmall(myCardsPlayer1);
        recyclerViewPlayer1.setAdapter(myCardAdapterP1);



        //DEALER
        imageViewDealer = findViewById(R.id.imageViewDealer);
        tvScoreDealer = findViewById(R.id.tvScoreDealer);
        dealerReyclerView = findViewById(R.id.recyclerViewDealer);
        LinearLayoutManager layoutDealer = new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
        dealerReyclerView.setLayoutManager(layoutDealer);

        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapterSmall(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

        //PLAYER2

        imageViewPlayer2 = findViewById(R.id.imageViewPlayer2);
        tvScorePlayer2 = findViewById(R.id.tvScorePlayer2);
        recyclerViewPlayer2 = findViewById(R.id.recyclerViewPlayer2);
        LinearLayoutManager layoutPlayer2 = new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
        recyclerViewPlayer2.setLayoutManager(layoutPlayer2);
        myCardsPlayer2 = new ArrayList<>();
        myCardAdapterP2 = new CardAdapterSmallO(myCardsPlayer2);
        recyclerViewPlayer2.setAdapter(myCardAdapterP2);

        //----
        firstCard = Deck.getIstance().pull();
        imageViewPlayer1.setImageResource(firstCard.getIdImage());
        scoreP1 = firstCard.getValue();
        tvScorePlayer1.setText(""+scoreP1);
        //----

        btnCarta.setOnClickListener(v -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Card card = Deck.getIstance().pull();
                    myCardsPlayer1.add(card);
                    myCardAdapterP1.notifyItemInserted(myCardsPlayer1.size()-1);
                    myScore += card.getValue();
                    tvScorePlayer1.setText(""+myScore);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("idClient",socket.getId());
                        jsonObject.put("card",card.toJSON());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.getSocket().emit("sendCard",jsonObject,(Ack) args1 -> {});

                    // TODO condizioni di fine

                }
            });
        });

        btnStai.setOnClickListener(v -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    btnCarta.setVisibility(View.INVISIBLE);
                    btnStai.setVisibility(View.INVISIBLE);

                    //....
                }
            });
        });

       /* indexClient=0;
        socket.getSocket().emit("isYourTurn",idClients.get(indexClient));
        idClient2 = idClients.get(indexClient);
        idClient3 = idClients.get(indexClient+1);

        socket.getSocket().on("clientTerminate",args -> {
            indexClient++;
            if(indexClient<3){
                socket.getSocket().emit("isYourTurn",idClients.get(indexClient));
            }else{
                //todo tocca al dealer
            }

        });*/

        /*socket.getSocket().on("requestCard",args -> {

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
                        myCardsPlayer2.add(Deck.getIstance().getCardById(card.getId()));
                        myCardAdapterP2.notifyItemInserted(myCardsPlayer2.size() - 1);
                    }else{
                        Log.d("ALFA","idClient4");
                        dealerCards.add(Deck.getIstance().getCardById(card.getId()));
                        cardAdapterDealer.notifyItemInserted(dealerCards.size() - 1);
                    }
                }
            });
        });*/

    }

}