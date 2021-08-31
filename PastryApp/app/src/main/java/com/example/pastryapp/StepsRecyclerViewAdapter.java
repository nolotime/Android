package com.example.pastryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StepsRecyclerViewAdapter extends RecyclerView.Adapter<StepsRecyclerViewAdapter.MyViewHolder> {

    ArrayList<String> stepInstructions;
    ArrayList<Double> stepTime;
    Context context;

    public StepsRecyclerViewAdapter(Context ct, ArrayList<String> step, ArrayList<Double> time){
        context = ct;
        stepInstructions = step;
        stepTime = time;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.steps_layout, parent, false);
        return new StepsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.instruction.setText(stepInstructions.get(position));
        holder.timer.setText(stepTime.get(position).toString() + " minutes");
    }

    @Override
    public int getItemCount() {
        return stepInstructions.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView instruction, timer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            instruction = itemView.findViewById(R.id.stepInstruction);
            timer = itemView.findViewById(R.id.stepTimer);
        }
    }
}
