package com.example.pastryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ModifyRecipeActivity extends AppCompatActivity {

    //VARIABLES TO STORE PASSED DATA
    ArrayList<String> instructions, ingridients, quantities, originalInstructions, originalIngridients, originalQuantities;
    ArrayList<Double> timers, originalTimers;
    String recipeName, recipeId;

    //ADAPTERS TO FILL THE PAGE
    ModifyRecipeIngridientsAdapter ingridientsAdapter;
    ModifyRecipeInstructionsAdapter instructionsAdapter;

    //RECYCLERVIEWS TO FILL WITH PASSED DATA
    RecyclerView rvIngridients;
    RecyclerView rvInstructions;

    EditText nameET;

    //SAVE BUTTON
    Button saveBtn;

    //DATABASE REFERENCE
    DatabaseReference refRecipe;

    //AUTHENTICATION
    FirebaseAuth myAuth;

    EditText loseFocusET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_recipe);

        //GETTING PASSED DATA
        recipeName = getIntent().getStringExtra("Name");
        ingridients = (ArrayList<String>) getIntent().getSerializableExtra("Ingridients");
        quantities = (ArrayList<String>) getIntent().getSerializableExtra("Quantities");
        instructions = (ArrayList<String>) getIntent().getSerializableExtra("Instructions");
        timers = (ArrayList<Double>) getIntent().getSerializableExtra("Timers");
        recipeId = getIntent().getStringExtra("Id");

        //ORIGINALS
        originalIngridients = (ArrayList<String>) getIntent().getSerializableExtra("Ingridients");
        originalQuantities = (ArrayList<String>) getIntent().getSerializableExtra("Quantities");
        originalInstructions = (ArrayList<String>) getIntent().getSerializableExtra("Instructions");
        originalTimers = (ArrayList<Double>) getIntent().getSerializableExtra("Timers");

        nameET = findViewById(R.id.modifyNameET);
        nameET.setText(recipeName);

        //ADAPTERS
        ingridientsAdapter = new ModifyRecipeIngridientsAdapter(ingridients, quantities, this);
        instructionsAdapter = new ModifyRecipeInstructionsAdapter(instructions, timers, this);

        //RECYCLERVIEWS
        rvIngridients = findViewById(R.id.rvModifyIngridients);
        rvInstructions = findViewById(R.id.rvModifyInstructions);

        //ADAPTERS TO RECYCLERVIEWS
        rvInstructions.setLayoutManager(new LinearLayoutManager(this));
        rvIngridients.setLayoutManager(new LinearLayoutManager(this));
        rvInstructions.setAdapter(instructionsAdapter);
        rvIngridients.setAdapter(ingridientsAdapter);

        //REFERENCE TO THE DATABASE
        refRecipe = FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId);

        //AUTHENTICATION
        myAuth = FirebaseAuth.getInstance();

        //SAVE BUTTON
        saveBtn = findViewById(R.id.modifyBtn);

        //ONCLICK LISTENER
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LOAD LAST CHANGE
                if (loseFocusET != null) {
                    loseFocusET.clearFocus();
                }

                //DATA TO REPLACE THE ORIGINAL WITH
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Id", recipeId);
                hashMap.put("Name", recipeName);
                hashMap.put("Ingridients", ingridients);
                hashMap.put("Quantity", quantities);
                hashMap.put("Instructions", instructions);
                hashMap.put("Timers", timers);
                hashMap.put("Author", myAuth.getUid());
                //hashMap.put("ImageUri", imageId);

                refRecipe.setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ModifyRecipeActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ModifyRecipeActivity.this, OpenOneRecipeActivity.class);
                                intent.putExtra("Id", recipeId);
                                intent.putExtra("Name", recipeName);
                                intent.putExtra("Instructions", instructions);
                                intent.putExtra("Timers", timers);
                                intent.putExtra("Ingridients", ingridients);
                                intent.putExtra("Quantities", quantities);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ModifyRecipeActivity.this, "Error, couldn't update", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        nameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                modifyName(nameET.getText().toString().trim(), nameET);
            }
        });
    }

    public void modifyName(String par, EditText tempEt){
        recipeName = par;
        loseFocusET = tempEt;
    }

    public void modifyIngridients(ArrayList<String> par, EditText tempET) {
        this.ingridients = par;
        loseFocusET = tempET;
    }

    public void modifyQuantities(ArrayList<String> par, EditText tempET) {
        this.quantities = par;
        loseFocusET = tempET;
    }

    public void modifyInstructions(ArrayList<String> par, EditText tempET) {
        this.instructions = par;
        loseFocusET = tempET;
    }

    public void modifyTimers(ArrayList<Double> par, EditText tempET) {
        this.timers = par;
        loseFocusET = tempET;
    }
}