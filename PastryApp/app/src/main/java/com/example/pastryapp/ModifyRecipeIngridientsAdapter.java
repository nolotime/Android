package com.example.pastryapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ModifyRecipeIngridientsAdapter extends RecyclerView.Adapter<ModifyRecipeIngridientsAdapter.MyViewHolder> {

    ArrayList<String> ingridients, quantities;
    Context context;

    ModifyRecipeActivity modifyRecipeActivity;

    public ModifyRecipeIngridientsAdapter(ArrayList<String> ingridients, ArrayList<String> quantities, Context context) {
        this.ingridients = ingridients;
        this.quantities = quantities;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.new_ingridient_layout, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.ingridient.setText(ingridients.get(position));
        holder.quantity.setText(quantities.get(position));
        modifyRecipeActivity = (ModifyRecipeActivity)context;

        holder.ingridient.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ingridients.set(position, holder.ingridient.getText().toString());
                modifyRecipeActivity.modifyIngridients(ingridients, holder.ingridient);
            }
        });

        holder.quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                quantities.set(position, holder.quantity.getText().toString());
                modifyRecipeActivity.modifyQuantities(quantities, holder.quantity);
                //holder.quantity.clearFocus();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingridients.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        EditText ingridient, quantity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ingridient = itemView.findViewById(R.id.IngridientName);
            quantity = itemView.findViewById(R.id.Quantity);
        }
    }
}
