package com.example.pastryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private TextView forgotPassword;

    private String email;
    private String password;

    private FirebaseAuth myAuth;

    private ProgressDialog pd;

    public void signInFunction(View view){
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Could not sing you in, please fill the required fields!", Toast.LENGTH_SHORT).show();
        }else{
            signInUser();
        }
    }

    private void signInUser() {
        //SETTING UP THE MESSAGE TO SHOW TO THE USER WHILE HE WAITS
        pd.setMessage("Signing you in, please wait");
        pd.show();

        myAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //CHECKING THE ROLE OF THE USER, ADMINS HAVE MORE AUTHORITY THAN OTHERS
    private void checkUser() {
        FirebaseUser user = myAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String userType = ""+snapshot.child("role").getValue();
                        if(userType.equals("user")){
                            startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                        }else if(userType.equals("admin")){
                            //START ADMIN ACTIVITY - NOT IMPLEMENTED
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

    }

    public void openSignUpActivity(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.editTextTextPersonName2);
        passwordInput = findViewById(R.id.editTextTextPassword2);

        myAuth = FirebaseAuth.getInstance();

        forgotPassword = findViewById(R.id.forgotPassword);

        //MESSAGE TO THE USER, POPUP MESSAGE WITH LOADING CIRCLE
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter your email address:");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        myAuth.sendPasswordResetEmail(mail)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Reset Link sent to your email address", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Cound't send the link to your email address, please provide a valid email address.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordResetDialog.create().show();
            }
        });
    }

}