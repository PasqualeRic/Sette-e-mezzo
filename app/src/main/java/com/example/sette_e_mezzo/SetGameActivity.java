package com.example.sette_e_mezzo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;

public class SetGameActivity extends AppCompatActivity {
    SocketClass socket = new SocketClass();
    Button btnPlay;
    EditText name;
    Spinner nPlayers;

    public String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_game);

        socket.getSocket().emit(Utils.createGame, null, args -> {
            this.id = (String) args[0];
        });

        name = findViewById(R.id.gameName);
        nPlayers = findViewById(R.id.nPlayers);

        btnPlay = findViewById(R.id.button);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!name.getText().toString().equals("")) {
                    JSONObject item = new JSONObject();
                    try {
                        item.put(Utils.id, id);
                        item.put(Utils.name, name.getText().toString());
                        item.put(Utils.nplayers, nPlayers.getSelectedItem().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.getSocket().emit(Utils.confGame, item, (Ack) args -> {});

                    Intent wait = new Intent(SetGameActivity.this, WaitActivity.class);
                    wait.putExtra(Utils.nplayers, nPlayers.getSelectedItem().toString());
                    wait.putExtra(Utils.idGame, id);
                    startActivity(wait);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.toastServer), Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

    }
}