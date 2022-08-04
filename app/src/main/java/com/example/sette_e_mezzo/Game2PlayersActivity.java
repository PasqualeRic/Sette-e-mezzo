package com.example.sette_e_mezzo;

import static android.widget.LinearLayout.HORIZONTAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;



public class Game2PlayersActivity extends AppCompatActivity {

    Button btnCarta;
    Button btnStai;
    ArrayList<Card> listOfCards;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapter  cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game2players);

        btnCarta = findViewById(R.id.btnCarta);
        btnStai = findViewById(R.id.btnStai);

        recyclerView = findViewById(R.id.myRecyclerView);

        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        listOfCards = new ArrayList<Card>();

        cardAdapter = new CardAdapter(listOfCards);
        recyclerView.setAdapter(cardAdapter);

        btnCarta.setOnClickListener(v -> {
            listOfCards.add(Deck.getIstance().pull());
            cardAdapter.notifyItemInserted(listOfCards.size()-1);
        });

    }
}