package com.example.sette_e_mezzo;

import static android.widget.LinearLayout.HORIZONTAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;



public class Game2PlayersActivity extends AppCompatActivity {

    Button btnCarta;
    Button btnStai;
    ArrayList<Card> listOfCards;
    TextView tvMyScore;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapter  cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game2players);

        btnCarta = findViewById(R.id.btnCarta);
        btnStai = findViewById(R.id.btnStai);
        tvMyScore = findViewById(R.id.tvMyScore);
        tvMyScore.setText("0");

        recyclerView = findViewById(R.id.myRecyclerView);

        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        listOfCards = new ArrayList<Card>();

        cardAdapter = new CardAdapter(listOfCards);
        recyclerView.setAdapter(cardAdapter);

        btnCarta.setOnClickListener(v -> {

            Card card = Deck.getIstance().pull();
            double score = Double.parseDouble(tvMyScore.getText().toString());
            score+=card.getValue();
            tvMyScore.setText(String.valueOf(score));
            listOfCards.add(card);
            cardAdapter.notifyItemInserted(listOfCards.size()-1);
        });

       btnCarta.callOnClick();

    }
}