package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;


public class Game2PlayersActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();

    Button btnRiGioca;
    TextView tvResult;

    // PLAYER
    Button btnCarta;
    Button btnStai;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapter myCardAdapter;

    //pulsanti dealer
    Button btnCartaDealer;
    Button btnStaiDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    CardAdapter cardAdapterDealer;
    ArrayList<Card> dealerCards;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game2players);

        // PLAYER
        btnCarta = findViewById(R.id.btnCarta);
        btnStai = findViewById(R.id.btnStai);

        tvMyScore = findViewById(R.id.tvMyScore);
        tvMyScore.setText("0");

        myRecyclerView = findViewById(R.id.myRecyclerView);
        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        myRecyclerView.setLayoutManager(layoutManager);
        myCards = new ArrayList<>();
        myCardAdapter = new CardAdapter(myCards);
        myRecyclerView.setAdapter(myCardAdapter);

        // DEALER
        btnCartaDealer = findViewById(R.id.btnCartaDealer);
        btnStaiDealer = findViewById(R.id.btnStaiDealer);

        tvScoreDealer = findViewById(R.id.tvDealerScore);


        dealerReyclerView = findViewById(R.id.recyclerViewDealer);
        LinearLayoutManager lmDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        dealerReyclerView.setLayoutManager(lmDealer);

        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapter(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

        btnRiGioca = findViewById(R.id.btnRestore);
        tvResult = findViewById(R.id.tvResult);

        btnCarta.setOnClickListener(v -> {

            Card card = Deck.getIstance().pull();
            double score = Double.parseDouble(tvMyScore.getText().toString());
            score+=card.getValue();
            tvMyScore.setText(String.valueOf(score));
            if(score>=7.5){
                btnCarta.setVisibility(v.INVISIBLE);
                btnStai.setVisibility(v.INVISIBLE);

                if(score>7.5) {
                    tvResult.setText("HAI PERSO");
                    btnRiGioca.setVisibility(v.VISIBLE);
                }else {
                    tvScoreDealer.setText("0");
                    dealerTurn();
                }
            }
            myCards.add(card);
            myCardAdapter.notifyItemInserted(myCards.size()-1);
        });

        btnStai.setOnClickListener(v -> {
            btnCarta.setVisibility(View.INVISIBLE);
            btnStai.setVisibility(View.INVISIBLE);
            tvScoreDealer.setText("0");
            JSONObject item = new JSONObject();
            try {
                item.put("id",socket.getSocket().id());
                item.put("point", tvMyScore.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.getSocket().emit("midGame",item,(Ack) args ->{
            });
            socket.getSocket().on("turno", args ->{
                Log.wtf("p", "p"+args);
            });
            socket.getSocket().emit("prova", true, (Ack) args ->{

            });

            //dealerTurn();
        });

        btnRiGioca.setOnClickListener(v->{
            Deck.getIstance().restoreDeck();
            tvMyScore.setText("0");
            myCardAdapter.clear();
            myCardAdapter.notifyDataSetChanged();
            btnCarta.setVisibility(v.VISIBLE);
            btnStai.setVisibility(v.VISIBLE);
            btnRiGioca.setVisibility(v.INVISIBLE);
            tvResult.setText("");
            tvScoreDealer.setText("");
            cardAdapterDealer.clear();
            cardAdapterDealer.notifyDataSetChanged();
            btnCartaDealer.setVisibility(v.INVISIBLE);
            btnStaiDealer.setVisibility(v.INVISIBLE);
            btnCarta.callOnClick();
        });

        btnCartaDealer.setOnClickListener(v -> {
            Card card = Deck.getIstance().pull();
            double score = Double.parseDouble(tvScoreDealer.getText().toString());
            score+=card.getValue();
            tvScoreDealer.setText(String.valueOf(score));
            if(score>=7.5){
                btnCartaDealer.setVisibility(v.INVISIBLE);
                btnStaiDealer.setVisibility(v.INVISIBLE);
                btnRiGioca.setVisibility(v.VISIBLE);

                if(score>7.5) {
                    tvResult.setText("HAI VINTO");
                }else{
                    tvResult.setText("HAI PERSO");
                }
            }
            dealerCards.add(card);
            cardAdapterDealer.notifyItemInserted(dealerCards.size()-1);
        });

        btnStaiDealer.setOnClickListener(v -> {
            if(Double.parseDouble(tvMyScore.getText().toString())>Double.parseDouble(tvScoreDealer.getText().toString())){
                tvResult.setText("HAI VINTO");
            }else{
                tvResult.setText("HAI PERSO");
            }
            btnRiGioca.setVisibility(v.VISIBLE);
            btnCartaDealer.setVisibility(v.INVISIBLE);
            btnStaiDealer.setVisibility(v.INVISIBLE);
        });

        btnCarta.callOnClick();

    }

    private void dealerTurn(){
        btnCarta.setVisibility(View.INVISIBLE);
        btnStai.setVisibility(View.INVISIBLE);
        btnCartaDealer.setVisibility(View.VISIBLE);
        btnStaiDealer.setVisibility(View.VISIBLE);
    }
}