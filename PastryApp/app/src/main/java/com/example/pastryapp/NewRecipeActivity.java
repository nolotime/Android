package com.example.pastryapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class NewRecipeActivity extends AppCompatActivity {

    //INGRIDIENTS RECYCLER VIEW
    RecyclerView recyclerView;
    //STEPS RECYCLER VIEW
    RecyclerView recyclerView2;

    //PICKING AN IMAGE
    private static final int PICK_IMAGE_REQUEST = 1;
    Button chooseImageBtn;
    TextView fileNameET;
    ImageView imageView;
    Uri imageUri;
    StorageReference storageReference;
    DatabaseReference ref;

    //INGRIDIENTS OF THE RECIPE
    ArrayList<String> ingridientNames = new ArrayList<String>();
    ArrayList<String> ingridientQuantity = new ArrayList<String>();

    //RECIPE STEPS
    ArrayList<String> stepDescription = new ArrayList<String>();
    ArrayList<Double> stepTime = new ArrayList<Double>();

    //DISPLAYING MESSAGES TO THE USER WHILE SAVING THE RECIPE
    ProgressDialog pd;

    //TO GET THE USER THAT IS CREATING THE RECIPE
    FirebaseAuth myAuth;

    EditText ingridientNameInput;
    EditText ingridientQuantityInput;
    EditText stepDescriptionInput;
    EditText stepTimeInput;
    EditText nameInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        ingridientNameInput = (EditText) findViewById(R.id.ingredients).findViewById(R.id.IngridientName);
        ingridientQuantityInput = (EditText) findViewById(R.id.ingredients).findViewById(R.id.Quantity);

        stepDescriptionInput = (EditText) findViewById(R.id.steps).findViewById(R.id.stepDescription);
        stepTimeInput = (EditText) findViewById(R.id.steps).findViewById(R.id.stepTime);

        nameInput = (EditText) findViewById(R.id.recipeName);

        myAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recipeIngridients);
        recyclerView2 = findViewById(R.id.recipeSteps);

        chooseImageBtn = findViewById(R.id.photoBtn);
        fileNameET = findViewById(R.id.photoET);
        imageView = findViewById(R.id.photoIV);
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        ref = FirebaseDatabase.getInstance().getReference("Recipes");

        pd = new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    //ADD NEW INGRIDIENT TO THE LIST
    public void addNewIngridient(View view) {
        String name = ingridientNameInput.getText().toString();
        String quantity = ingridientQuantityInput.getText().toString();

        if (name.isEmpty() || quantity.isEmpty()) {
            Toast.makeText(this, "You can't add an ingridient without igridient name/quantity", Toast.LENGTH_SHORT).show();
        } else {
            ingridientNames.add(name);
            ingridientQuantity.add(quantity);

            populateIngridientsRecyclerView();
        }

    }

    //SHOW ADDED INGRIDIENTS IN RECYCLERVIEW
    private void populateIngridientsRecyclerView() {

        IngridientsRecyclerViewAdapter ingridientAdapter = new IngridientsRecyclerViewAdapter(this, ingridientNames, ingridientQuantity);
        recyclerView.setAdapter(ingridientAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ingridientNameInput.setText("");
        ingridientQuantityInput.setText("");
    }

    //ADD NEW STEP TO THE LIST
    public void addNewStep(View view) {
        String instruction = stepDescriptionInput.getText().toString();
        String time = stepTimeInput.getText().toString();

        if (instruction.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "You can't add a step without step instruction/time", Toast.LENGTH_SHORT).show();
        } else {
            stepDescription.add(instruction);
            stepTime.add(Double.parseDouble(time));

            populateStepRecyclerView();
        }
    }

    //SHOW STEPS IN RECYCLERVIEW
    private void populateStepRecyclerView() {

        StepsRecyclerViewAdapter stepAdapter = new StepsRecyclerViewAdapter(this, stepDescription, stepTime);
        recyclerView2.setAdapter(stepAdapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        stepDescriptionInput.setText("");
        stepTimeInput.setText("");
    }

    //SAVING THE RECIPE
    public void saveRecipe(View view) {
        if (ingridientNames.size() != 0 && stepDescription.size() != 0) {

            pd.setMessage("Saving your recipe, please wait");
            pd.show();

            String recipeName = nameInput.getText().toString();

            //GETTING THE LOGGED USER AS AUTHOR OF THE RECIPE
            String author = myAuth.getUid();
            long timeStamp = System.currentTimeMillis();

            String imageId = uploadFile();

            //ADD IMAGE URI
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Id", author + timeStamp);
            hashMap.put("Name", recipeName);
            hashMap.put("Ingridients", ingridientNames);
            hashMap.put("Quantity", ingridientQuantity);
            hashMap.put("Instructions", stepDescription);
            hashMap.put("Timers", stepTime);
            hashMap.put("Author", author);
            hashMap.put("ImageUri", imageId);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recipes");
            ref.child(author + timeStamp)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i("Success", "Added a recipe");
                            pd.dismiss();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.i("Failure", "Could not add recipe");
                            pd.dismiss();

                        }
                    });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private String uploadFile() {
        if (imageUri != null) {
            String imageId = myAuth.getUid() + System.currentTimeMillis() + "." + getFileExtension(imageUri);
            StorageReference fileRef = storageReference.child(imageId);
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.d("Download url", url);
                            Toast.makeText(NewRecipeActivity.this, "Image upload successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            return imageId;
        } else {
            return "";
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            fileNameET.setText(imageUri.toString());
            Picasso.get().load(imageUri).fit().into(imageView);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}