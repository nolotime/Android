package com.example.pastryapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {

    //RECYCLERVIEW FOR RECIPES
    RecyclerView recyclerView;
    //REFERENCE TO THE REALTIME DATABASE
    DatabaseReference ref;
    //ADAPTER FOR THE RECYCLERVIEW
    HomeRecyclerViewAdapter myAdapter;
    //LISTS WITH DATA TO DISPLAY
    ArrayList<Recipe> recipes;
    //SEARCH
    EditText search;
    //NAVIGATION
    ImageView addNewRecipeBtn;
    ImageView goToProfile;
    ImageView goToSaved;
    //BUTTON
    Button notVerifiedBtn;
    //TEXTVIEW TO DISPLAY USERS WHO ARE NOT VERIFIED
    TextView notVerifiedTV;
    //AUTHENTICATION
    FirebaseAuth myAuth;
    //LOGGED USER
    FirebaseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //GETTING THE RECYCLERVIEW FROM THE XML FILE
        recyclerView = findViewById(R.id.homeRecipesView);
        search = (EditText)findViewById(R.id.searchBox);
        notVerifiedBtn = findViewById(R.id.verifyBtn);
        notVerifiedTV = findViewById(R.id.notVerifiedTV);
        addNewRecipeBtn = findViewById(R.id.navRecyclerHome).findViewById(R.id.plus);
        goToProfile = findViewById(R.id.navRecyclerHome).findViewById(R.id.profile);
        goToSaved = findViewById(R.id.navRecyclerHome).findViewById(R.id.saved);
        //GETTING THE REFERENCE TO THE DATABASE
        ref = FirebaseDatabase.getInstance().getReference().child("Recipes");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipes = new ArrayList<Recipe>();

        myAdapter = new HomeRecyclerViewAdapter(this, recipes);
        recyclerView.setAdapter(myAdapter);

        myAuth = FirebaseAuth.getInstance();
        currUser = myAuth.getCurrentUser();

        if(!currUser.isEmailVerified()){
            notVerifiedTV.setVisibility(View.VISIBLE);
            notVerifiedBtn.setVisibility(View.VISIBLE);

            notVerifiedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser tempUser = myAuth.getCurrentUser();
                    tempUser.sendEmailVerification()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(HomePageActivity.this, "Verification email has been sent to the provided email address", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HomePageActivity.this, "Error, couldn't send verification email to the provided email address", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }

        addNewRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePageActivity.this, NewRecipeActivity.class));
            }
        });

        goToSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePageActivity.this, SavedRecipesActivity.class));
            }
        });

        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePageActivity.this, ProfileActivity.class));
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    myAdapter.getFilter().filter(s);
                }
                catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                recipes.removeAll(recipes);
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    recipes.add(recipe);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}