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

import org.json.JSONObject;

import java.util.ArrayList;


public class G2PClientActivity extends AppCompatActivity {

    SocketClass socket = new SocketClass();
    TextView tvResult;

    // PLAYER
    ImageView ivMyFirstCard;
    Button btnCarta, btnStai;
    ArrayList<Card> myCards;
    TextView tvMyScore;
    RecyclerView myRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapter myCardAdapter;

    //DELAER
    ImageView ivFirstCardDealer;
    TextView tvScoreDealer;
    RecyclerView dealerReyclerView;
    CardAdapter cardAdapterDealer;
    ArrayList<Card> dealerCards;

    Double myScore;
    String idClient, idCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g2p_client);

        //  PLAYER
        ivMyFirstCard = findViewById(R.id.ivMyFirstCard);
        btnCarta = findViewById(R.id.btnCarta);
        btnStai = findViewById(R.id.btnStai);

        tvMyScore = findViewById(R.id.tvMyScore);

        myRecyclerView = findViewById(R.id.rvMyCards);
        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        myRecyclerView.setLayoutManager(layoutManager);
        myCards = new ArrayList<>();
        myCardAdapter = new CardAdapter(myCards);
        myRecyclerView.setAdapter(myCardAdapter);

        tvResult = findViewById(R.id.tvResult);

        btnCarta.setOnClickListener(v -> {
            Log.d("debug","giveMeCard");
            //socket.getSocket().emit("giveMeCard",(Ack) args1 -> {});
        });

        btnStai.setOnClickListener(v -> {
            Log.d("debug","sendStai");
            //socket.getSocket().emit("sendStai",(Ack) args1 -> {});
        });

        //  DEALER
        ivFirstCardDealer = findViewById(R.id.ivFirstCardDealer);
        tvScoreDealer = findViewById(R.id.tvScoreDealer);

        dealerReyclerView = findViewById(R.id.rvCardsDealer);
        LinearLayoutManager lmDealer = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        dealerReyclerView.setLayoutManager(lmDealer);

        dealerCards = new ArrayList<>();
        cardAdapterDealer = new CardAdapter(dealerCards);
        dealerReyclerView.setAdapter(cardAdapterDealer);

        socket.getSocket().on("reciveYourFirstCard",args -> {
            try {
                JSONObject json = new JSONObject(args[0].toString());
                idClient = json.getString("idClient");
                idCard = json.getJSONObject("card").getString("id");
                //Log.d("debug","idCard: "+idCard);
                myScore = Deck.getIstance().getCardById(idCard).getValue();
                tvMyScore.setText(""+myScore);
            }catch(Exception e){}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivMyFirstCard.setImageResource(Deck.getIstance().getCardById(idCard).getIdImage());
                }
            });
        });

        socket.getSocket().on("reciveNewCard",args -> {

            try {
                JSONObject json = new JSONObject(args[0].toString());
                idClient = json.getString("idClient");
                idCard = json.getJSONObject("card").getString("id");
                myScore +=Deck.getIstance().getCardById(idCard).getValue();
                tvMyScore.setText(""+myScore);
                Log.d("debug","idCard: "+idCard);
            }catch(Exception e){}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(socket.getId()==idClient) {
                        myCards.add(Deck.getIstance().getCardById(idCard));
                        myCardAdapter.notifyItemInserted(myCards.size() - 1);
                        if(myScore>=7.5){
                            btnCarta.setVisibility(View.INVISIBLE);
                            btnStai.setVisibility(View.INVISIBLE);
                        }
                    }else{
                        dealerCards.add(Deck.getIstance().getCardById(idCard));
                        cardAdapterDealer.notifyItemInserted(dealerCards.size() - 1);
                    }
                }
            });
        });
    }

}