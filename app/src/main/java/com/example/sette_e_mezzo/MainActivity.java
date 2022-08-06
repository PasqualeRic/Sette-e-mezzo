package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        socket.connection();

        Deck deck = Deck.getIstance();

        Button btnMenu = findViewById(R.id.btnNextCard);
        ImageView imageView = findViewById(R.id.imageView);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Card card = deck.pull();
                imageView.setImageResource(card.getIdImage());
            }
        });

        Button btnGame = findViewById(R.id.btnGame);
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent game2Players = new Intent(MainActivity.this, Game2PlayersActivity.class);
                //startActivity(game2Players);
                Intent menu = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(menu);
            }
        });

    }
}