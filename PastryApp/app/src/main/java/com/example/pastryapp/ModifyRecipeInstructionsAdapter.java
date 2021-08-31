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

public class ModifyRecipeInstructionsAdapter extends RecyclerView.Adapter<ModifyRecipeInstructionsAdapter.MyViewHolder>{

    ArrayList<String> instructions;
    ArrayList<Double> timers;
    Context context;

    ModifyRecipeActivity modifyRecipeActivity;

    public ModifyRecipeInstructionsAdapter(ArrayList<String> instructions, ArrayList<Double> timers, Context context) {
        this.instructions = instructions;
        this.timers = timers;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.new_step_layout, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.instruction.setText(instructions.get(position));
        holder.time.setText(timers.get(position).toString());
        modifyRecipeActivity = (ModifyRecipeActivity)context;

        holder.instruction.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                instructions.set(position, holder.instruction.getText().toString());
                modifyRecipeActivity.modifyInstructions(instructions, holder.instruction);
            }
        });

        holder.time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                timers.set(position, Double.parseDouble(holder.time.getText().toString()));
                modifyRecipeActivity.modifyTimers(timers, holder.time);
            }
        });
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        EditText instruction, time;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            instruction = (EditText) itemView.findViewById(R.id.stepDescription);
            time = (EditText) itemView.findViewById(R.id.stepTime);
        }
    }
}
