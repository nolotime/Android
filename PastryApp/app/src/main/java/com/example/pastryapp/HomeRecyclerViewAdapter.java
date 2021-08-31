package com.example.pastryapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> implements Filterable {

    public ArrayList<Recipe> recipes, filteredRecipes;
    Context context;
    Recipe help;
    private FilterRecipes filterRecipes;

    public HomeRecyclerViewAdapter(Context ct, ArrayList<Recipe> recipeAll){
        context = ct;
        recipes = recipeAll;
        this.filteredRecipes = recipeAll;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.one_recipe_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Recipe currRecipe = recipes.get(position);
        help = currRecipe;
        holder.title.setText(currRecipe.getName());
        holder.countOfSteps.setText(String.valueOf(currRecipe.getInstructions().size()));
        double totalTime = 0;
        for(int i = 0; i < currRecipe.Timers.size(); i++){
            totalTime += currRecipe.Timers.get(i);
        }
        holder.totalTime.setText(""+totalTime+" minutes");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, OpenOneRecipeActivity.class);
                myIntent.putExtra("Id", currRecipe.getId());
                myIntent.putExtra("Name", currRecipe.getName());
                myIntent.putExtra("Instructions", currRecipe.getInstructions());
                myIntent.putExtra("Timers", currRecipe.getTimers());
                myIntent.putExtra("Ingridients", currRecipe.getIngridients());
                myIntent.putExtra("Quantities", currRecipe.getQuantity());
                myIntent.putExtra("Image", currRecipe.getImageUrl().toString());

                context.startActivity(myIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    @Override
    public Filter getFilter() {
        if(filterRecipes == null){
            filterRecipes = new FilterRecipes(filteredRecipes, this);
        }
        return filterRecipes;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title, countOfSteps, totalTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recipeTitle);
            countOfSteps = itemView.findViewById(R.id.recipeTotalSteps);
            totalTime = itemView.findViewById(R.id.recipeTotalTime);

        }
    }
}
