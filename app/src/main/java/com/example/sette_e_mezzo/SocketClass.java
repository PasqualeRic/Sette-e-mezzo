package com.example.sette_e_mezzo;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketClass {
    private static Socket socket = null;
    public void connection(){
        try {
            socket = IO.socket("http://10.0.2.2:3000");
            socket.connect();
        }catch (URISyntaxException e) {
            Log.wtf("prova","prova");
            e.printStackTrace();
        }
    }
    public void disconnection(){
        socket.disconnect();
    }
    public Socket getSocket() {
        if(socket == null)
        {
            connection();
        }
        return socket;
    }
}

