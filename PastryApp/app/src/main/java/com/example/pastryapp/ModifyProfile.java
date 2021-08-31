package com.example.pastryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ModifyProfile extends AppCompatActivity {

    //DATA TO DISPLAY
    String name;
    //TEXT FIELDS TO DISPLAY DATA
    EditText modifyName;
    //SAVE BUTTONS
    Button saveData, savePassword;
    //PASSWORD CHANGE
    EditText currPasswordInput, newPasswordInput;
    //AUTHENTICATION
    FirebaseAuth myAuth;
    //PROGRESS DIALOG
    ProgressDialog pd;
    //DATABASE REFERENCE
    DatabaseReference refUser;
    //NAVIGATION
    ImageView homeBtn, addNewBtn, savedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        //GETTING TEXT FIELDS
        modifyName = (EditText)findViewById(R.id.editName);
        currPasswordInput = (EditText)findViewById(R.id.oldPassword);
        newPasswordInput = (EditText)findViewById(R.id.newPassword);

        refUser = FirebaseDatabase.getInstance().getReference();

        //GETTING THE DATA FROM THE PROFILE ACTIVITY
        name = getIntent().getSerializableExtra("UserName").toString();

        //FILLING TEXT FIELDS
        modifyName.setText(name);

        //BUTTONS
        saveData = findViewById(R.id.modifyProfileBtn);
        savePassword = findViewById(R.id.changePasswordBtn);

        //NAVIGATION
        homeBtn = findViewById(R.id.navRecyclerHome).findViewById(R.id.home);
        addNewBtn = findViewById(R.id.navRecyclerHome).findViewById(R.id.plus);
        savedBtn = findViewById(R.id.navRecyclerHome).findViewById(R.id.saved);

        //AUTHENTICATION
        myAuth = FirebaseAuth.getInstance();

        //PROGRESS DIALOG
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);

        //NAVIGATION ON CLICK LISTENERS
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ModifyProfile.this, HomePageActivity.class));
            }
        });

        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ModifyProfile.this, NewRecipeActivity.class));
            }
        });

        savedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ModifyProfile.this, SavedRecipesActivity.class));
            }
        });

        //ONCLICK LISTENERS
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = modifyName.getText().toString().trim();
                if(newName != null && newName.length()>0) {
                    refUser.child("Users").child(myAuth.getUid()).child("name").setValue(newName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ModifyProfile.this, "Name successfully changed", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ModifyProfile.this, ProfileActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ModifyProfile.this, "Error while updating, please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //STRINGS FOR PASSWORDS
                String currentPassword, newPassword;
                //GETTING INPUTS
                currentPassword = currPasswordInput.getText().toString().trim();
                newPassword = newPasswordInput.getText().toString().trim();

                //CHECKING INPUTS
                if(currentPassword.isEmpty()){
                    Toast.makeText(ModifyProfile.this, "Couldn't save changes until current password is confirmed", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPassword.length()<8){
                    Toast.makeText(ModifyProfile.this, "New password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                //SETTING PD AND SHOWING MESSAGE
                pd.setMessage("Saving changes, please wait");
                pd.show();

                AuthCredential authCredential = EmailAuthProvider.getCredential(myAuth.getCurrentUser().getEmail(), currentPassword);
                myAuth.getCurrentUser().reauthenticate(authCredential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                myAuth.getCurrentUser().updatePassword(newPassword)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(ModifyProfile.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ModifyProfile.this, ProfileActivity.class));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(ModifyProfile.this, "Error, couldn't save changes", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(ModifyProfile.this, "Error, couldn't save changes", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }

}