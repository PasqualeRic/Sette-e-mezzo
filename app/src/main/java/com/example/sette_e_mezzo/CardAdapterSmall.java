package com.example.sette_e_mezzo;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardAdapterSmall extends RecyclerView.Adapter<CardAdapterSmall.CardViewHolder> {

    ArrayList<Card> dataset;
    int rotation;

    public CardAdapterSmall(ArrayList<Card> dataset,int rotation){
        this.dataset=dataset;
        this.rotation=rotation;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public CardViewHolder(View v){
            super(v);
            imageView = v.findViewById(R.id.ivCardSmall);
        }
    }

    @NonNull
    @Override
    public CardAdapterSmall.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // a seconda del grado di inclinazione inseriamo il layout adatto con quell'inclinazione
        View v = null;
        if(rotation==0)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_small,parent,false);
        else if(rotation==90)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_small_left,parent,false);
        else //rotation==270
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_small_right,parent,false);

        CardAdapterSmall.CardViewHolder cardViewHolder = new CardAdapterSmall.CardViewHolder(v);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapterSmall.CardViewHolder holder, int position) {
        holder.imageView.setImageResource(dataset.get(position).getIdImage());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void clear(){ dataset.clear(); }

}
