package com.example.sette_e_mezzo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardAdapter  extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    ArrayList<Card> dataset;

    public CardAdapter(ArrayList<Card> dataset){
        this.dataset=dataset;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public CardViewHolder(View v){
            super(v);
            imageView = v.findViewById(R.id.ivCard);
        }
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_big,parent,false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.imageView.setImageResource(dataset.get(position).getIdImage());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void clear(){ dataset.clear(); }

}

