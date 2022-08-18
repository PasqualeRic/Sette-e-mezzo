package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

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
    String myId, myIdFirstCard;

    // Player 2 - Sinistra
    ImageView ivFCPlayer2;
    ArrayList<Card> cardsP2;
    TextView tvScoreP2;
    RecyclerView rvPlayer2;
    RecyclerView.LayoutManager lmPlayer2;
    CardAdapterSmall adapterP2;
    Double scoreP2;
    String idClient2;

    // Player 3 - Destra
    ImageView ivFCPlayer3;
    ArrayList<Card> cardsP3;
    TextView tvScoreP3;
    RecyclerView rvPlayer3;
    RecyclerView.LayoutManager lmPlayer3;
    CardAdapterSmall adapterP3;
    Double scoreP3;
    String idClient3;

    // Dealer
    ImageView ivFirstCardDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    RecyclerView.LayoutManager lmDealer;
    CardAdapterSmall cardAdapterDealer;
    ArrayList<Card> dealerCards;
    Double scoreDealer;
    String idServer;

    Double scoreTmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4_players);

        idServer = getIntent().getStringExtra("idServer");

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

        // Dealer
        ivFirstCardDealer = findViewById(R.id.ivFCPlayer4);
        tvScoreDealer = findViewById(R.id.tvScorePlayer4);
        lmDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,true);
        dealerReyclerView = findViewById(R.id.rvCardsPlayer4);
        dealerReyclerView.setLayoutManager(lmDealer);
        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapterSmall(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

        btnCarta.setOnClickListener(v -> {

        });

        btnStai.setOnClickListener(v -> {

        });

        socket.getSocket().on("reciveYourFirstCard",args -> {
            String idClient, idFirstCard;
            Double value;
            try {
                JSONArray array = new JSONArray(args[0].toString());
                for(int i=0;i< array.length();i++){
                    Log.d("ALFA-reciveYourFirstCard",i+") -> "+array.get(i).toString());
                    JSONObject json = new JSONObject(array.get(i).toString());
                    idClient = json.getString("idClient");
                    idFirstCard = json.getJSONObject("card").getString("id");
                    value = json.getJSONObject("card").getDouble("value");

                    if(idClient.equals(socket.getId())){
                        myId = idClient;
                        myIdFirstCard = idFirstCard;
                        scoreTmp = value;

                    }else if(idClient2 == null)
                        idClient2 = idClient;
                    else
                        idClient3 = idClient;

                }
            }catch(Exception e){}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMyScore.setText(""+scoreTmp);
                    ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(myIdFirstCard).getIdImage());
                }
            });
        });
    }
}