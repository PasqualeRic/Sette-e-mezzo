package com.example.sette_e_mezzo;

import android.widget.ImageView;

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
        //image.setImageResource(idImage);
    }

    // GETTER
    public double getValue(){return value;}
    public boolean isExtracted(){return extracted;}
    //spublic ImageView getImageView(){return image;}
    public int getIdImage(){return idImage;}

    // SETTER
    public void setExtracted(){ extracted=true;}
}
