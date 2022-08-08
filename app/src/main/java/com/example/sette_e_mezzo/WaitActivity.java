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

public class WaitActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    private ProgressBar spinner;

    String name;
    TextView text1, text2;
    Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        text1 = findViewById(R.id.setname);
        text2 = findViewById(R.id.textView);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        socket.getSocket().on("invioPlayer", args -> {
               // Log.wtf("p","p"+args[0]);
                name = args[0].toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text1.setText(name);
                    }
                });

            });

        start = findViewById(R.id.btnStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WaitActivity.this, Game2PlayersActivity.class);
                startActivity(i);
            }
        });
    }
}