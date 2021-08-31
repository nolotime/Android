package com.example.pastryapp;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterRecipes extends Filter {
    ArrayList<Recipe> allRecipes;
    HomeRecyclerViewAdapter myAdapter;

    public FilterRecipes(ArrayList<Recipe> allRecipes, HomeRecyclerViewAdapter myAdapter) {
        this.allRecipes = allRecipes;
        this.myAdapter = myAdapter;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults result = new FilterResults();
        //CHECKING IF CONSTRAINT IS NOT EMPTY OR NULL
        if(constraint != null && constraint.length()>0){
            //AVOID CASE SENSITIVITY
            constraint = constraint.toString().toUpperCase();
            //ARRAYLIST TO STORE FILTERED DATA
            ArrayList<Recipe> filteredRecipes = new ArrayList<Recipe>();
            //CHECKING EVERY RECIPE IF IT CONTRAINS CONSTRAINT
            for(int i = 0; i < allRecipes.size(); i++){
                //IF THE RECIPE CONTAINS THE CONSTRAINT IT IS ADDED TO THE FILTERED LIST
                if(allRecipes.get(i).getName().toUpperCase().contains(constraint)){
                    filteredRecipes.add(allRecipes.get(i));
                }
            }
            result.count = filteredRecipes.size();
            result.values = filteredRecipes;
        }else{
            result.count = allRecipes.size();
            result.values = allRecipes;
        }
        return result;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        myAdapter.recipes = (ArrayList<Recipe>)results.values;

        myAdapter.notifyDataSetChanged();
    }
}
