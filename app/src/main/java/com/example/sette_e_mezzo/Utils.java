package com.example.sette_e_mezzo;

import java.util.ArrayList;

public class Utils {

    //      STRINGHE PER EVITARE L'HARDCODING

    //      chiavi
    public static String idClient="idClient";
    public static String idClients="idClients";
    public static String idServer="idServer";
    public static String name="name";
    public static String nplayers="nplayers";
    public static String score="score";
    public static String idFirstCard="idFirstCard";
    public static String bool="bool";
    public static String idCard="idCard";
    public static String names="names";
    public static String card="card";
    public static String id="id";
    public static String value="value";

    //      metodi js
    public static String invioPlayer="invioPlayer";
    public static String startGame="startGame";
    public static String isYourTurn="isYourTurn";
    public static String sendFirstCard="sendFirstCard";
    public static String partita="partita";
    public static String createGame="createGame";
    public static String confGame="confGame";
    public static String joinGame="joinGame";
    public static String sendCard="sendCard";
    public static String requestCard="requestCard";
    public static String clientTerminate="clientTerminate";
    public static String overSize="overSize";
    public static String resContinueGame="resContinueGame";
    public static String deleteGame="deleteGame";
    public static String terminateTurn="terminateTurn";
    public static String giveMeCard="giveMeCard";
    public static String myTurn="myTurn";
    public static String reciveYourFirstCard="reciveYourFirstCard";
    public static String reciveCard="reciveCard";
    public static String closeRound="closeRound";
    public static String continueGame="continueGame";
    public static String deletePlayer="deletePlayer";
    

    // preso in input un ArrayList di Card restituisce un ArrayList contenente gli id delle card
    public static ArrayList<String> getIdCards(ArrayList<Card> cards){

        ArrayList<String> idCards = new ArrayList<>();

        for(int i=0;i<cards.size();i++){
            idCards.add(cards.get(i).getId());
        }

        return idCards;
    }

    // preso in input un ArrayList di id restituisce un Array contente le relative card
    public static ArrayList<Card> getCardsById(ArrayList<String> idCards){

        ArrayList<Card> cards = new ArrayList<>();

        for(int i=0;i<idCards.size();i++){
            cards.add(Deck.getIstance().getCardById(idCards.get(i)));
        }

        return cards;
    }

}
