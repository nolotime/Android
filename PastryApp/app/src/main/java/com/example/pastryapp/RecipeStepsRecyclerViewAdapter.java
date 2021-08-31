package com.example.pastryapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecipeStepsRecyclerViewAdapter extends RecyclerView.Adapter<RecipeStepsRecyclerViewAdapter.MyViewHolder> {

    ArrayList<String> instructions;
    ArrayList<Double> timers;
    Context context;
    int help;
    double stepTime;

    public RecipeStepsRecyclerViewAdapter(ArrayList<String> instructions, ArrayList<Double> timers, Context context) {
        this.instructions = instructions;
        this.timers = timers;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.one_step_layout, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //FOR STEP NUMBER
        help = position + 1;
        //TIME FOR TIMER
        stepTime = timers.get(position) * 60;
        //POPULATING TEXTVIEWS AND BUTTON
        holder.stepNumb.setText("Step "+help+".");
        holder.instruction.setText(instructions.get(position));
        holder.timer.setText(timers.get(position).toString());
        //ONCLICKLISTENER ON BUTTON
        holder.timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long duration = (long)stepTime*1000;
                new CountDownTimer(duration, 1000) {
                    @Override
                    public void onTick(long l) {
                        String sDuration = String.format(Locale.ENGLISH, "%02d : %02d",
                                TimeUnit.MILLISECONDS.toMinutes(l),
                                TimeUnit.MILLISECONDS.toSeconds(l)-
                                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MICROSECONDS.toMinutes(l)));
                        Log.i("Time left: ", sDuration);
                        holder.timer.setText(sDuration);
                    }

                    @Override
                    public void onFinish() {
                        MediaPlayer mp = MediaPlayer.create(holder.itemView.getContext(), R.raw.microwave_oven_bell);
                        mp.start();
                    }
                }.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView instruction, stepNumb;
        Button timer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            stepNumb = itemView.findViewById(R.id.stepNmb);
            instruction = itemView.findViewById(R.id.StepInstruction);
            timer = itemView.findViewById(R.id.timerBtn);
        }
    }

    public void startTimer(double timePar, Button btn){

    }
}
