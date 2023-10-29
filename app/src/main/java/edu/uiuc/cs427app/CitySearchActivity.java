package edu.uiuc.cs427app;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import db.dbHelper;

import java.util.List;

public class CitySearchActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        User currUser = (User)getIntent().getSerializableExtra("user");

        // setup UI theme based on the theme attribute under User
        String selectedTheme = currUser.getTheme();
        if (selectedTheme != null) {
            if (selectedTheme.equals("Purple")) {
                setTheme(R.style.Theme_MyFirstApp);
            } else if (selectedTheme.equals("Green")) {
                setTheme(R.style.Theme_MyFirstApp2);
            } else if (selectedTheme.equals("Blue")) {
                setTheme(R.style.Theme_MyFirstApp3);
            }
        }

        // Show app title with username
        setTitle("Team 39 - " + currUser.getName());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        // add city
        editTextCityName = findViewById(R.id.editTextCityName);
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(this);

        // back to main
        ImageButton buttonBack = findViewById(R.id.backButton);
        buttonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton:
                addCity();
                break;

            case R.id.backButton:
                finish();
                break;
        }
    }

    // add city to db
    private void addCity() {
        String cityName = editTextCityName.getText().toString().trim();
        // input cannot be empty
        if (cityName.isEmpty()) {
            Toast.makeText(this, "City name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        // make sure get correct user id
        long userId = getIntent().getLongExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID is not passed properly", Toast.LENGTH_SHORT).show();
            return;
        }

        // check if city is in db, if not add it
        dbHelper mydbHelper = new dbHelper(this);
        List<String> existingCities = mydbHelper.getCityListForUser(userId);
        if (existingCities.contains(cityName)) {
            Toast.makeText(this, "City already added", Toast.LENGTH_SHORT).show();
        } else {
            mydbHelper.addCityForUser(userId, cityName);
            Toast.makeText(this, "City added successfully", Toast.LENGTH_SHORT).show();
        }
    }

}
