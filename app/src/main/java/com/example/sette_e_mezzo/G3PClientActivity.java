package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class G3PClientActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    String idServer;
    TextView tvResult;
    // PLAYER1
    ImageView ivMyFirstCardMyPlayer;
    Button btnCarta3, btnStai3;
    ArrayList<Card> myCardsPlayer1;
    TextView tvScoreMyPlayer;
    RecyclerView rvMyPlayer;
    RecyclerView.LayoutManager layoutManagerP1;
    CardAdapter myCardAdapterP1;
    // PLAYER2
    ImageView imageView;
    Button btnCarta, btnStai;
    ArrayList<Card> myCardsPlayer2;
    TextView tvP2;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManagerP2;
    CardAdapter myCardAdapterP2;

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
        setContentView(R.layout.activity_game3_players);
        idServer = getIntent().getStringExtra("idServer");


    }
}