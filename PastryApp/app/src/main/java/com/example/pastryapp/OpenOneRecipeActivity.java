package com.example.pastryapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OpenOneRecipeActivity extends AppCompatActivity {

    //PASSED DATA VARIABLES
    String recipeName, recipeId;
    ArrayList<String> ingridients, quantities, instructions;
    ArrayList<Double> timers;
    Uri imageUri;
    //RECYCLERVIEW FOR INGRIDIENTS
    RecyclerView recyclerView;
    //RECYCLERVIEW FOR STEPS
    RecyclerView recyclerView2;
    //NAME
    TextView nameTv;
    //IMAGE
    ImageView image;
    //MODIFY & DELETE
    Button modifyBtn, deleteBtn;
    DatabaseReference refRecipes;
    ProgressDialog pd;
    //AUTHENTICATION
    FirebaseAuth myAuth;
    DatabaseReference refUser;
    String currentUser;
    //FAVORITE
    Button favBtn;
    ArrayList<String> favoredByUser;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_one_recipe);

        //GETTING THE PASSED DATA AND STORING IT IN THE RIGHT VARIABLE
        recipeName = getIntent().getStringExtra("Name");
        ingridients = (ArrayList<String>) getIntent().getSerializableExtra("Ingridients");
        quantities = (ArrayList<String>) getIntent().getSerializableExtra("Quantities");
        instructions = (ArrayList<String>) getIntent().getSerializableExtra("Instructions");
        timers = (ArrayList<Double>) getIntent().getSerializableExtra("Timers");
        recipeId = getIntent().getStringExtra("Id");
        //RECIPES DON'T REQUIRE A PICTURE
        if (getIntent().getSerializableExtra("Image") != null) {
            imageUri = Uri.parse(getIntent().getSerializableExtra("Image").toString());
        }

        recyclerView = findViewById(R.id.ingridientsRV);
        recyclerView2 = findViewById(R.id.stepsRV);

        nameTv = findViewById(R.id.recipeNameId);
        nameTv.setText(recipeName);

        if (imageUri != null) {
            Picasso.get().load(imageUri).fit().into(image);
        }else{
            image.setVisibility(View.GONE);
        }

        modifyBtn = findViewById(R.id.modifyRecipeBtn);
        deleteBtn = findViewById(R.id.deleteRecipeBtn);
        refRecipes = FirebaseDatabase.getInstance().getReference().child("Recipes");

        favBtn = findViewById(R.id.favorite);
        favoredByUser = new ArrayList<String>();

        //GET THE LOGGED USERS' ID TO GET WHOLE USER DATA
        myAuth = FirebaseAuth.getInstance();
        refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(myAuth.getUid());
        currentUser = myAuth.getUid();

        //PROGRESS DIALOG
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);

        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User helpUser = snapshot.getValue(User.class);
                if (helpUser.getFavoredRecipes() != null) {
                    for (String rep : helpUser.getFavoredRecipes()) {
                        favoredByUser.add(rep);
                    }
                }
                //BOTH USER WHO POSTED THE RECUPE AND ADMIN CAN DELETE IT
                if (helpUser.role == "admin" || recipeId.contains(currentUser)) {
                    deleteBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //ONLY USER THAT POSTED THE RECIPE CAN MODIFY IT
        if (recipeId.contains(currentUser)) {
            modifyBtn.setVisibility(View.VISIBLE);
        }

        //DOESNT WORK
        //SETTING TEXT ON SAVE BUTTON - UNSAVE IF ITS ALREADY SAVED
        if (favoredByUser == null || !favoredByUser.contains(recipeId)) {
            favBtn.setText("Save");
        } else {
            favBtn.setText("Unsave");
        }

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenOneRecipeActivity.this, ModifyRecipeActivity.class);
                intent.putExtra("Name", recipeName);
                intent.putExtra("Id", recipeId);
                intent.putExtra("Ingridients", ingridients);
                intent.putExtra("Quantities", quantities);
                intent.putExtra("Instructions", instructions);
                intent.putExtra("Timers", timers);
                startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Deleting recipe...");
                pd.show();
                refRecipes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Recipe tempRecipe = dataSnapshot.getValue(Recipe.class);
                            if (tempRecipe.getId().equals(recipeId)) {
                                pd.dismiss();
                                dataSnapshot.getRef().removeValue();
                                startActivity(new Intent(OpenOneRecipeActivity.this, HomePageActivity.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DOES USER HAVE ANY SAVED RECIPES, IF HE DOES - DOES HE HAVE THIS ONE SAVED?
                if (favoredByUser == null || !favoredByUser.contains(recipeId)) {
                    //ADD TO FAVORED LIST
                    favoredByUser.add(recipeId);
                    refUser.child("FavoredRecipes").setValue(favoredByUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(OpenOneRecipeActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(OpenOneRecipeActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    favoredByUser.remove(recipeId);
                    refUser.child("FavoredRecipes").setValue(favoredByUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(OpenOneRecipeActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(OpenOneRecipeActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        IngridientsRecyclerViewAdapter ingridientAdapter = new IngridientsRecyclerViewAdapter(this, ingridients, quantities);
        recyclerView.setAdapter(ingridientAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecipeStepsRecyclerViewAdapter stepsAdapter = new RecipeStepsRecyclerViewAdapter(instructions, timers, this);
        recyclerView2.setAdapter(stepsAdapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));


    }
}