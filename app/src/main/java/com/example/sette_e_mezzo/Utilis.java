package com.example.sette_e_mezzo;

import java.util.ArrayList;

public class Utilis {

    public static ArrayList<String> getIdCards(ArrayList<Card> cards){

        ArrayList<String> idCards = new ArrayList<>();

        for(int i=0;i<cards.size();i++){
            idCards.add(cards.get(i).getId());
        }

        return idCards;
    }

    public static ArrayList<Card> getCardsById(ArrayList<String> idCards){

        ArrayList<Card> cards = new ArrayList<>();

        for(int i=0;i<idCards.size();i++){
            cards.add(Deck.getIstance().getCardById(idCards.get(i)));
        }

        return cards;
    }

}
