package com.example.pastryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ProgressDialog pd;

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button signUpBtn;
    private ArrayList<String> favoredRecipes = new ArrayList<String>();

    private FirebaseAuth myAuth;

    public void signUpFunction(View view){
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        //CHECKING TO SEE IF THERE ARE ERRORS IN THE FORM USER SUBMITET AS A REGISTER REQUEST
        //ARE THERE ANY EMPTY FIELDS, DOES THE PASSWORD MEET THE REQUIRED MINIMUM LENGTH, DID THE USER CONFIRM THE PASSWORD
        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(SignUpActivity.this, "Please fill all the credentials!", Toast.LENGTH_SHORT).show();
        }else if (password.length() < 8){
            Toast.makeText(SignUpActivity.this, "Password must be at least 8 characters long!", Toast.LENGTH_SHORT).show();
        }else if(!confirmPassword.equals(password)){
            Toast.makeText(SignUpActivity.this, "Could not confirm password, try again.", Toast.LENGTH_SHORT).show();
        }else{
            registerUser(name, email, password);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameInput = (EditText)findViewById(R.id.editTextTextPersonName);
        emailInput = (EditText) findViewById(R.id.editTextTextPersonName3);
        passwordInput = (EditText) findViewById(R.id.editTextTextPassword);
        confirmPasswordInput = (EditText)findViewById(R.id.editTextTextPassword3);

        signUpBtn = findViewById(R.id.signUpBtn);

        myAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);
    }

    //STORE A USER OBJECT INTO A DATABASE
    private void registerUser(String name, String email, String password) {
        //DISPLAYING PROGRESS DIALOG WITH A MESSAGE TO THE USER
        pd.setMessage("Signing You Up, please wait");
        pd.show();

        //CREATING A USER IN FIREBASE AUTHENTICATION
        myAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        //SEND VERIFICATION EMAIL
                        FirebaseUser user = myAuth.getCurrentUser();
                        user.sendEmailVerification()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignUpActivity.this, "Verification email has been sent to the provided email address", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, "Error, couldn't send verification email to the provided email address", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        updateUserInfo(name, email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        pd.dismiss();
                        Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo(String name, String email) {
        pd.setMessage("Saving user info");

        long timeStamp = System.currentTimeMillis();

        String userId = myAuth.getUid();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", userId);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("role", "user");
        hashMap.put("timestamp", timeStamp);
        hashMap.put("favoredRecipes", favoredRecipes);

        //ADDING INFORMATION ABOUT THE USER TO THE DATABASE, NOT CONNECTED TO THE AUTHENTICATION PART
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //DATABASE REF IS THE PROBLEM
        ref.child(userId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("Success", "Added a user");
                        pd.dismiss();
                        Toast.makeText(SignUpActivity.this, "Account sucessufully created!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, HomePageActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.i("Failure", "Could not add user");
                        pd.dismiss();
                        Toast.makeText(SignUpActivity.this, "Account creating failed!", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}