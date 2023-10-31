package edu.uiuc.cs427app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import db.dbHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CitySearchActivity extends AppCompatActivity implements View.OnClickListener {
    private AutoCompleteTextView autoCompleteTextViewCityName;
    private List<CityWeather> cityList = new ArrayList<>();
    private SearchCityAdapter cityAdapter;
    private RecyclerView recyclerViewCities;

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
        autoCompleteTextViewCityName = findViewById(R.id.searchTextView);
        recyclerViewCities = findViewById(R.id.recyclerView2);

        cityAdapter = new SearchCityAdapter(cityList, this::addCityToDatabase);
        recyclerViewCities.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCities.setAdapter(cityAdapter);

        ArrayAdapter<CityWeather> autoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cityList);
        autoCompleteTextViewCityName.setAdapter(autoAdapter);

        autoCompleteTextViewCityName.setOnItemClickListener((parent, view, position, id) -> {
            CityWeather selectedCity = cityList.get(position);
            addCityToDatabase(selectedCity);
        });

        autoCompleteTextViewCityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 3) {
                    searchCity(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // back to main
        ImageButton buttonBack = findViewById(R.id.backButton);
        buttonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            finish();
        }
    }

    // add city to db
    private void searchCity(String cityName) {


        //String url = "https://api.accuweather.com/locations/v1/cities/search";
        String url = "https://dataservice.accuweather.com/locations/v1/cities/autocomplete";
        String apiKey = "NMAmiNk68RDbijC9HZ18ue7J643yCOli";
        String query = cityName;

        String fullUrl = String.format("%s?apikey=%s&q=%s", url, apiKey, query);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullUrl,
                response -> {
                    Log.d("CitySearchActivity", "Response: " + response);
                    List<CityWeather> cityList = parseCities(response);
                    showCities(cityList);
                }, error -> {
            Log.e("CitySearchActivity", "Network error: " + error.getMessage());
        });

        queue.add(stringRequest);

    }

    private List<CityWeather> parseCities(String response) {
        List<CityWeather> cityList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cityObject = jsonArray.getJSONObject(i);
                String cityName = cityObject.getString("LocalizedName");
                String locationKey = cityObject.getString("Key");
                cityList.add(new CityWeather(cityName, locationKey));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityList;
    }

    private void showCities(List<CityWeather> cityList) {
        this.cityList.clear();
        this.cityList.addAll(cityList);
        cityAdapter.notifyDataSetChanged();
        recyclerViewCities.setVisibility(View.VISIBLE);
    }

    private void addCityToDatabase(CityWeather city) {
        long userId = getIntent().getLongExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID is not passed properly", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper mydbHelper = new dbHelper(this);
        List<String> existingCities = mydbHelper.getCityListForUser(userId);
        if (existingCities.contains(city.getName())) {
            Toast.makeText(this, "City already added", Toast.LENGTH_SHORT).show();
        } else {
            mydbHelper.addCityForUser(userId, city.getName());
            mydbHelper.updateUserCityKey(userId, city.getLocationKey());
            Toast.makeText(this, "City added successfully", Toast.LENGTH_SHORT).show();
        }
    }


}
