package com.example.pastryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class SavedRecipesActivity extends AppCompatActivity {

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
    ImageView goHome;
    //BUTTON
    Button notVerifiedBtn;
    //TEXTVIEW TO DISPLAY USERS WHO ARE NOT VERIFIED
    TextView notVerifiedTV;
    //AUTHENTICATION
    FirebaseAuth myAuth;
    //LOGGED USER
    FirebaseUser currUser;
    DatabaseReference refUser;
    ArrayList<String> favoredByUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //GETTING THE RECYCLERVIEW FROM THE XML FILE
        recyclerView = findViewById(R.id.homeRecipesView);
        search = (EditText) findViewById(R.id.searchBox);
        notVerifiedBtn = findViewById(R.id.verifyBtn);
        notVerifiedTV = findViewById(R.id.notVerifiedTV);
        addNewRecipeBtn = findViewById(R.id.navRecyclerHome).findViewById(R.id.plus);
        goToProfile = findViewById(R.id.navRecyclerHome).findViewById(R.id.profile);
        goHome = findViewById(R.id.navRecyclerHome).findViewById(R.id.home);
        //GETTING THE REFERENCE TO THE DATABASE
        ref = FirebaseDatabase.getInstance().getReference().child("Recipes");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        favoredByUser = new ArrayList<String>();
        recipes = new ArrayList<Recipe>();


        myAuth = FirebaseAuth.getInstance();
        currUser = myAuth.getCurrentUser();

        refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(myAuth.getUid());

        addNewRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedRecipesActivity.this, NewRecipeActivity.class));
            }
        });

        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedRecipesActivity.this, HomePageActivity.class));
            }
        });

        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedRecipesActivity.this, ProfileActivity.class));
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
                } catch (Exception e) {

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //GET ALL FAVORED
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User helpUser = snapshot.getValue(User.class);
                if (helpUser.getFavoredRecipes().size() > 0) {
                    for (String rep : helpUser.getFavoredRecipes()) {
                        ref.child(rep).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Recipe temp = snapshot.getValue(Recipe.class);
                                recipes.add(temp);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                myAdapter = new HomeRecyclerViewAdapter(SavedRecipesActivity.this, recipes);
                recyclerView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
