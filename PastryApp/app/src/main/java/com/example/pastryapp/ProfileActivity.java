package com.example.pastryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    //RECYCLERVIEW FOR RECIPES
    RecyclerView recyclerView;
    //DATABASE REFERENCE TO THE REALTIME DATABASE - RECIPES
    DatabaseReference ref;
    //DATABASE REFERENCE TO THE REALTIME DATABASE - USERS
    DatabaseReference refUser;
    //LIST WITH DATA TO DISPLAY
    ArrayList<Recipe> recipes;
    //FIREBASE AUTH TO CHECK THE LOGGED USER
    FirebaseAuth myAuth;
    //LOGGED USER
    User user;
    //DISPLAY NAME
    TextView textView;
    //LOG OUT
    Button logOutBtn;
    //NAVIGATION
    ImageView homePageBtn, addNewRecipeBtn, savedBtn;

    HomeRecyclerViewAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //GETTING THE RECYCLERVIEW
        recyclerView = findViewById(R.id.homeRecipesView);
        //TEXTVIEW
        textView = findViewById(R.id.userName);
        //SETTING THE REFERENCE TO THE DATABASE
        ref = FirebaseDatabase.getInstance().getReference().child("Recipes");
        refUser = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //LOG OUT BUTTON
        logOutBtn = findViewById(R.id.logOutBtn);

        //NAVIGATION
        homePageBtn = findViewById(R.id.navRecyclerHome).findViewById(R.id.home);
        addNewRecipeBtn = findViewById(R.id.navRecyclerHome).findViewById(R.id.plus);
        savedBtn = findViewById(R.id.navRecyclerHome).findViewById(R.id.saved);

        recipes = new ArrayList<Recipe>();
        //ADAPTER TO FILL THE RECYCLERVIEW
        myAdapter = new HomeRecyclerViewAdapter(this,recipes);
        recyclerView.setAdapter(myAdapter);
        //SETTING THE AUTH
        myAuth = FirebaseAuth.getInstance();
        String currUser = myAuth.getCurrentUser().getUid();

        //NAVIGATION ON CLICK LISTENERS
        homePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HomePageActivity.class));
            }
        });

        addNewRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, NewRecipeActivity.class));
            }
        });

        savedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SavedRecipesActivity.class));
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modifyProfileIntent = new Intent(ProfileActivity.this, ModifyProfile.class);
                modifyProfileIntent.putExtra("UserName", user.getName());
                modifyProfileIntent.putExtra("Email", user.getEmail());
                startActivity(modifyProfileIntent);
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        //SETTING THE NAME OF THE USER
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    user = dataSnapshot.getValue(User.class);
                    if(user.getUid().equals(currUser)){
                        textView.setText(user.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //GETTING USER'S RECIPES
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.removeAll(recipes);
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if(recipe.getId().contains(currUser)){
                        recipes.add(recipe);
                    }
//                    recipes.add(recipe);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}