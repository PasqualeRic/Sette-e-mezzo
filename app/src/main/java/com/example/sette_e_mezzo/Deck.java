package com.example.sette_e_mezzo;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class Deck extends ArrayList<Card> {

    private static Deck istance;
    private static Random rnd = new Random();


    private Deck(){
        updateDeck();
    }

    private void updateDeck(){
        // carico tutte le carte
        add(new Card("b1",R.drawable.b1,1));
        add(new Card("b2",R.drawable.b2,2));
        add(new Card("b3",R.drawable.b3,3));
        add(new Card("b4",R.drawable.b4,4));
        add(new Card("b5",R.drawable.b5,5));
        add(new Card("b6",R.drawable.b6,6));
        add(new Card("b7",R.drawable.b7,7));
        add(new Card("b8",R.drawable.b8,0.5));
        add(new Card("b9",R.drawable.b9,0.5));
        add(new Card("b10",R.drawable.b10,0.5));
        add(new Card("c1",R.drawable.c1,1));
        add(new Card("c2",R.drawable.c2,2));
        add(new Card("c3",R.drawable.c3,3));
        add(new Card("c4",R.drawable.c4,4));
        add(new Card("c5",R.drawable.c5,5));
        add(new Card("c6",R.drawable.c6,6));
        add(new Card("c7",R.drawable.c7,7));
        add(new Card("c8",R.drawable.c8,0.5));
        add(new Card("c9",R.drawable.c9,0.5));
        add(new Card("c10",R.drawable.c10,0.5));
        add(new Card("d1",R.drawable.d1,1));
        add(new Card("d2",R.drawable.d2,2));
        add(new Card("d3",R.drawable.d3,3));
        add(new Card("d4",R.drawable.d4,4));
        add(new Card("d5",R.drawable.d5,5));
        add(new Card("d6",R.drawable.d6,6));
        add(new Card("d7",R.drawable.d7,7));
        add(new Card("d8",R.drawable.d8,0.5));
        add(new Card("d9",R.drawable.d9,0.5));
        add(new Card("d10",R.drawable.d10,0));
        add(new Card("s1",R.drawable.s1,1));
        add(new Card("s2",R.drawable.s2,2));
        add(new Card("s3",R.drawable.s3,3));
        add(new Card("s4",R.drawable.s4,4));
        add(new Card("s5",R.drawable.s5,5));
        add(new Card("s6",R.drawable.s6,6));
        add(new Card("s7",R.drawable.s7,7));
        add(new Card("s8",R.drawable.s8,0.5));
        add(new Card("s9",R.drawable.s9,0.5));
        add(new Card("s10",R.drawable.s10,0.5));

    }

    //pattern singleton
    public static Deck getIstance(){
        if(istance==null)
            istance = new Deck();
        return istance;
    }

    //metodo per estrarre una carta casualemente
    public Card pull(){
        Card card;
        do{
            card = istance.get(rnd.nextInt(40));
        }while(card.isExtracted());
        card.setExtracted();
        return card;
    }

    //metodo per ripristinare il mazzo
    public void restoreDeck(){
        istance.clear();
        updateDeck();
    }

    public Card getCardById(String id){
        for(int i=0;i<istance.size();i++){
            Card card = istance.get(i);
            if(card.getId().equals(id))
                return card;
        }
        Log.d("addCard","null");
        return null;
    }


}
