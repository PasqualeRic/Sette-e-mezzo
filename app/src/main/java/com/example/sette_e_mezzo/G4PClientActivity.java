package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class G4PClientActivity extends AppCompatActivity {

    SocketClass socket = new SocketClass();
    TextView tvResult;
    Button btnCarta, btnStai;

    // MyPlayer
    ImageView ivMyFirstCard;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapterSmall myCardAdapter;
    Double myScore;

    // Player 2 - Sinistra
    ImageView ivFCPlayer2;
    ArrayList<Card> cardsP2;
    TextView tvScoreP2;
    RecyclerView rvPlayer2;
    RecyclerView.LayoutManager lmPlayer2;
    CardAdapterSmall adapterP2;
    Double scoreP2;

    // Player 3 - Destra
    ImageView ivFCPlayer3;
    ArrayList<Card> cardsP3;
    TextView tvScoreP3;
    RecyclerView rvPlayer3;
    RecyclerView.LayoutManager lmPlayer3;
    CardAdapterSmall adapterP3;
    Double scoreP3;

    // Dealer
    ImageView ivFirstCardDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    RecyclerView.LayoutManager lmDealer;
    CardAdapterSmall cardAdapterDealer;
    ArrayList<Card> dealerCards;
    Double scoreDealer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4_players);

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

        // Player 2 - Sinistra
        ivFCPlayer2 = findViewById(R.id.ivFCPlayer2);
        cardsP2 = new ArrayList<>();
        tvScoreP2 = findViewById(R.id.tvScoreP2);
        lmPlayer2 = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        rvPlayer2 = findViewById(R.id.rvCardsPlayer2);
        rvPlayer2.setLayoutManager(lmPlayer2);
        adapterP2 = new CardAdapterSmall(cardsP2);
        rvPlayer2.setAdapter(adapterP2);

        // Player 3 - Destra
        ivFCPlayer3 = findViewById(R.id.ivFCPlayer3);
        cardsP3 = new ArrayList<>();
        tvScoreP3 = findViewById(R.id.tvScoreP3);
        lmPlayer3 = new LinearLayoutManager(this, RecyclerView.VERTICAL,true);
        rvPlayer3 = findViewById(R.id.rvCardsPlayer3);
        rvPlayer3.setLayoutManager(lmPlayer3);
        adapterP3 = new CardAdapterSmall(cardsP3);
        rvPlayer3.setAdapter(adapterP3);

        // Dealer
        ivFirstCardDealer = findViewById(R.id.ivFCPlayer4);
        tvScoreDealer = findViewById(R.id.tvScoreP4);
        lmDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        dealerReyclerView = findViewById(R.id.rvCardsPlayer4);
        dealerReyclerView.setLayoutManager(lmDealer);
        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapterSmall(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

        btnCarta.setOnClickListener(v -> {
            Card card = Deck.getIstance().pull();
            myCards.add(card);
            tvMyScore.setText(card.getValue()+"");
            myCardAdapter.notifyItemInserted(myCards.size()-1);

            card = Deck.getIstance().pull();
            cardsP2.add(card);
            tvScoreP2.setText(card.getValue()+"");
            adapterP2.notifyItemInserted(cardsP2.size()-1);

            card = Deck.getIstance().pull();
            cardsP3.add(card);
            tvScoreP3.setText(card.getValue()+"");
            adapterP3.notifyItemInserted(cardsP3.size()-1);

            card = Deck.getIstance().pull();
            dealerCards.add(card);
            tvScoreDealer.setText(card.getValue()+"");
            cardAdapterDealer.notifyItemInserted(dealerCards.size()-1);
        });
    }
}