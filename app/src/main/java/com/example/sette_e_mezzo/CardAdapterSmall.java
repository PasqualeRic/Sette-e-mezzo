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

    public CardAdapterSmall(ArrayList<Card> dataset){
        this.dataset=dataset;
    }

    // parte java per riferirsi all'xml
    public static class CardViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        // View v sarà la nostra Card
        public CardViewHolder(View v){
            super(v);
            imageView = v.findViewById(R.id.ivCardSmall);
        }
    }

    @NonNull
    @Override
    public CardAdapterSmall.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate(xml,parent,false)
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_small,parent,false);
        CardAdapterSmall.CardViewHolder cardViewHolder = new CardAdapterSmall.CardViewHolder(v);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapterSmall.CardViewHolder holder, int position) {
        // in questo metodo specifico il comportamento
        holder.imageView.setImageResource(dataset.get(position).getIdImage());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void clear(){ dataset.clear(); }

}
