package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;

public class WaitActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    private ProgressBar spinner;

    String name, number;
    TextView text1, text2, n, text3, text4;
    Button start;
    Integer conta = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        n = findViewById(R.id.set);
        text1 = findViewById(R.id.setname);
        text2 = findViewById(R.id.textView);
        text3 = findViewById(R.id.textView);
        text4 = findViewById(R.id.numberPlayers);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        socket.getSocket().on("invioPlayer", args -> {
               // Log.wtf("p","p"+args[0]);
                name = args[0].toString();
                number = args[1].toString();
                conta = conta + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text1.setText(name);
                        text4.setText(number);
                        text3.setText("Puoi startare la partita!");
                        n.setText(conta.toString());
                        Log.wtf("n", "number"+number);
                        Log.wtf("n", "conta"+conta);

                       if(conta.toString().equals("/"+number))
                        {
                            spinner.setVisibility(View.GONE);
                        }
                    }
                });

            });

        start = findViewById(R.id.btnStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.getSocket().emit("startGame",(Ack) args -> {

                });
                Intent i = new Intent(WaitActivity.this, Game2PlayersActivity.class);
                startActivity(i);
            }
        });
    }
}