package edu.uiuc.cs427app;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.HashMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    Button backToLogin;
    Button registerButton;
    FirebaseAuth mAuth;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextInputEditText nameInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        backToLogin = findViewById(R.id.backToLogin);
        registerButton = findViewById(R.id.registerUser);

        emailInput = findViewById(R.id.registerEmailBox);
        passwordInput = findViewById(R.id.registerPasswordBox);
        nameInput = findViewById(R.id.registerNameBox);

        Spinner colorSpinner = findViewById(R.id.registerThemeBox);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.colors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);


        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(emailInput.getText());
                String password = String.valueOf(passwordInput.getText());
                String name = String.valueOf(nameInput.getText());
                String theme = colorSpinner.getSelectedItem().toString();



                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(RegisterActivity.this, user.getEmail() + ", your account is created",
                                            Toast.LENGTH_SHORT).show();

                                    User userObject = new User(name, email, user.getUid(), theme);

                                    City city1 = new City("Boston", "23.23", "32.32");
                                    City city2 = new City("New York", "25.23", "35.32");

                                    userObject.addCity(city1);
                                    userObject.addCity(city2);

                                    db.collection("users").document(user.getUid())
                                            .set(userObject)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                                    //String selectedTheme = mapColorToTheme(theme);
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    //intent.putExtra("theme", selectedTheme);
                                                    intent.putExtra("user", userObject);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error writing document", e);
                                                }
                                            });
                                    
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("user", userObject);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });

//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

    }
//    private String mapColorToTheme(String color) {
//        HashMap<String, String> colorToThemeMap = new HashMap<>();
//        colorToThemeMap.put("Purple", "Theme.MyFirstApp");
//        colorToThemeMap.put("Green", "Theme.MyFirstApp2");
//        colorToThemeMap.put("Blue", "Theme.MyFirstApp3");
//
//        return colorToThemeMap.get(color);
//    }
}