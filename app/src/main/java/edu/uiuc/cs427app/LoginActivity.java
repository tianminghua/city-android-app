package edu.uiuc.cs427app;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Button registerButton;
    Button loginButton;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        registerButton = findViewById(R.id.register);
        loginButton = findViewById(R.id.loginUser);
        emailInput = findViewById(R.id.loginEmailBox);
        passwordInput = findViewById(R.id.loginPasswordBox);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }

        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(emailInput.getText());
                String password = String.valueOf(passwordInput.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success! ");
                                    Toast.makeText(LoginActivity.this, "Signin Success! " + user.getEmail(),
                                            Toast.LENGTH_SHORT).show();

                                    // fetch user profile from Firestore
                                    db.collection("users")
                                            .document(user.getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();

                                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                        String name = document.getData().get("name").toString();
                                                        String email = document.getData().get("email").toString();
                                                        int theme =Integer.valueOf(document.getData().get("theme").toString());
                                                        User userObject = new User(name, email, user.getUid(), theme);

                                                        List<Map<String, String>> cityList = (List<Map<String, String>>) document.getData().get("cities");
                                                        assert cityList != null;
                                                        for (Map<String, String> cityInfo : cityList) {
                                                            Log.d(TAG, "City data: " + cityInfo);
                                                            City newCity = new City(cityInfo.get("name"), cityInfo.get("coordX"), cityInfo.get("coordY"));
                                                            userObject.addCity(newCity);
                                                        }

                                                        // pass userObject to MainActivity
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        intent.putExtra("user", userObject);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Log.w(TAG, "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed. Try again.",
                                            Toast.LENGTH_SHORT).show();
                                    passwordInput.getText().clear();
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
}