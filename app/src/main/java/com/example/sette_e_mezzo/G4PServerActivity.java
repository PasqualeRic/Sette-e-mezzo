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

public class G4PServerActivity extends AppCompatActivity {

    SocketClass socket = new SocketClass();
    TextView tvResult;
    Button btnCarta, btnStai;
    ArrayList<String> idClients;
    Integer indexClient;  //indice per tenere traccia del client di turno

    // MyPlayer
    ImageView ivMyFirstCard;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapterSmall myCardAdapter;
    Double myScore;

    // Player 2 - Sinistra
    String idClient2;
    ImageView ivFCPlayer2;
    ArrayList<Card> cardsP2;
    TextView tvScoreP2;
    RecyclerView rvPlayer2;
    RecyclerView.LayoutManager lmPlayer2;
    CardAdapterSmall adapterP2;
    Double scoreP2;

    // Player 3 - Destra
    String idClient3;
    ImageView ivFCPlayer3;
    ArrayList<Card> cardsP3;
    TextView tvScoreP3;
    RecyclerView rvPlayer3;
    RecyclerView.LayoutManager lmPlayer3;
    CardAdapterSmall adapterP3;
    Double scoreP3;

    // Player 4
    String idClient4;
    ImageView ivFCPlayer4;
    ArrayList<Card> cardsP4;
    TextView tvScoreP4;
    RecyclerView rvPlayer4;
    RecyclerView.LayoutManager lmPlayer4;
    CardAdapterSmall adapterP4;
    Double scoreP4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4_players);

        idClients = getIntent().getStringArrayListExtra("idClients");

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

        // Player 4
        ivFCPlayer4 = findViewById(R.id.ivFCPlayer4);
        tvScoreP4 = findViewById(R.id.tvScorePlayer4);
        lmPlayer4 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        rvPlayer4 = findViewById(R.id.rvCardsPlayer4);
        rvPlayer4.setLayoutManager(lmPlayer4);
        cardsP4 = new ArrayList<>();
        adapterP4 = new CardAdapterSmall(cardsP4);
        rvPlayer4.setAdapter(adapterP4);

        Card myFirstCard = Deck.getIstance().pull();
        ivMyFirstCard.setImageResource(myFirstCard.getIdImage());
        tvMyScore.setText(myFirstCard.getValue()+"");

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

        indexClient=0;
        socket.getSocket().emit("isYourTurn",idClients.get(indexClient));
        idClient2 = idClients.get(indexClient);
        idClient3 = idClients.get(indexClient+1);
        idClient4 = idClients.get(indexClient+2);

        socket.getSocket().on("clientTerminate",args -> {
            indexClient++;
            if(indexClient<3){
                socket.getSocket().emit("isYourTurn",idClients.get(indexClient));
            }else{
                //todo tocca al dealer
            }

        });

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

    }
}