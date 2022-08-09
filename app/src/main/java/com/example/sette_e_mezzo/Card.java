package com.example.sette_e_mezzo;

import android.widget.ImageView;

import org.json.JSONObject;

public class Card{

    private String backCard = "";
    private String id;
    private int idImage;
    private double value;
    private boolean extracted;

    public Card(String id,int idImage,double value) {
        extracted = false;
        this.id=id;
        this.idImage=idImage;
        this.value=value;
    }

    // GETTER
    public String getId(){return id;}
    public double getValue(){return value;}
    public boolean isExtracted(){return extracted;}
    public int getIdImage(){return idImage;}

    // SETTER
    public void setExtracted(){ extracted=true;}

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("value", value);
            json.put("idImage", idImage);
            json.put("extracted", extracted);
        }catch(Exception e){}

        return json;
    }
    //public String toString(){ return "id: "+id+", valore: "+value; }
}
