package com.example.pastryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IngridientsRecyclerViewAdapter extends RecyclerView.Adapter<IngridientsRecyclerViewAdapter.MyViewHolder> {

    ArrayList<String> ingridientNames, ingridientQuantities;
    Context context;

    //CONSTRUCTOR
    public IngridientsRecyclerViewAdapter(Context ct, ArrayList<String> names, ArrayList<String> quantity){
        context = ct;
        ingridientNames = names;
        ingridientQuantities = quantity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ingridients_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textName.setText(ingridientNames.get(position));
        holder.textQuant.setText(ingridientQuantities.get(position));
    }

    @Override
    public int getItemCount() {
        return ingridientNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textName, textQuant;

        public MyViewHolder(@NonNull View view){
            super(view);

            textName = view.findViewById(R.id.textView4);
            textQuant = view.findViewById(R.id.textView5);
        }
    }
}
