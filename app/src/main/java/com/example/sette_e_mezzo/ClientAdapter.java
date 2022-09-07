package com.example.sette_e_mezzo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.CardViewHolder> {

    ArrayList<String> dataset;

    public ClientAdapter(ArrayList<String> dataset){
            this.dataset=dataset;
            }

    public static class CardViewHolder extends RecyclerView.ViewHolder{

        TextView username;

        public CardViewHolder(View v){
            super(v);
            username = v.findViewById(R.id.tvUsername);
        }
    }

    @NonNull
    @Override
    public ClientAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.username,parent,false);
        ClientAdapter.CardViewHolder cardViewHolder = new ClientAdapter.CardViewHolder(v);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClientAdapter.CardViewHolder holder, int position) {
        holder.username.setText(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void clear(){ dataset.clear(); }

}

