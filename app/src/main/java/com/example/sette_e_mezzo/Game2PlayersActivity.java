package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;



public class Game2PlayersActivity extends AppCompatActivity {

    Button btnRiGioca;

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
        myCards = new ArrayList<Card>();
        myCardAdapter = new CardAdapter(myCards);
        myRecyclerView.setAdapter(myCardAdapter);

        // DEALER
       /* btnCartaDealer = findViewById(R.id.btnCartaDealer);
        btnStaiDealer = findViewById(R.id.btnStaiDealer);

        tvScoreDealer = findViewById(R.id.tvDealerScore);

        dealerReyclerView = findViewById(R.id.recyclerViewDealer);*/


        btnRiGioca = findViewById(R.id.btnRestore);

        btnCarta.setOnClickListener(v -> {

            Card card = Deck.getIstance().pull();
            double score = Double.parseDouble(tvMyScore.getText().toString());
            score+=card.getValue();
            tvMyScore.setText(String.valueOf(score));
            if(score>=7.5){
                btnCarta.setVisibility(v.INVISIBLE);
                btnStai.setVisibility(v.INVISIBLE);
                btnRiGioca.setVisibility(v.VISIBLE);

                if(score>7.5) {
                    Log.d("SCORE",tvMyScore.getText().toString());
                    tvMyScore.setText(tvMyScore.getText().toString() + " hai perso");
                }
            }
            myCards.add(card);
            myCardAdapter.notifyItemInserted(myCards.size()-1);
        });

        btnStai.setOnClickListener(v -> {
            btnCarta.setVisibility(v.INVISIBLE);
            btnStai.setVisibility(v.INVISIBLE);
            btnCartaDealer.setVisibility(v.VISIBLE);
            btnStaiDealer.setVisibility(v.VISIBLE);
        });

        btnRiGioca.setOnClickListener(v->{
            Deck.getIstance().restoreDeck();
            tvMyScore.setText("0");
            myCardAdapter.clear();
            myCardAdapter.notifyDataSetChanged();
            btnCarta.setVisibility(v.VISIBLE);
            btnStai.setVisibility(v.VISIBLE);
            btnRiGioca.setVisibility(v.INVISIBLE);
        });



       btnCarta.callOnClick();

    }
}