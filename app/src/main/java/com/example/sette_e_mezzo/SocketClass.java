package com.example.sette_e_mezzo;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketClass {
    private static Socket socket = null;
    public void connection(){
        try {
            socket = IO.socket("https://sette-e-mezzo.herokuapp.com");
            socket.connect();
        }catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){ return socket.connected();}
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
    public String getId(){ return socket.id();}
}

