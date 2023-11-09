package edu.uiuc.cs427app;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
//                if (charSequence.length() >= 3) {
//                    searchCity(charSequence.toString());
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // search button
        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCity(autoCompleteTextViewCityName.getText().toString());
            }
        });

        // back to main
//        ImageButton buttonBack = findViewById(R.id.backButton);
//        buttonBack.setOnClickListener(this);
//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("BackButton", "Back button clicked!");
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(buttonBack.getWindowToken(), 0);
//                finish();
//            }
//        });

        // hide the back button when keyboard is shown
        ConstraintLayout layoutToHide = findViewById(R.id.linearLayout);

// Attach a global layout listener to detect keyboard visibility changes
        layoutToHide.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                layoutToHide.getWindowVisibleDisplayFrame(r);

                int screenHeight = layoutToHide.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                // Determine if the keyboard is visible
                if (keypadHeight > screenHeight * 0.15) { // You can adjust this threshold
                    // Keyboard is visible, hide the element
                    layoutToHide.setVisibility(View.GONE);
                } else {
                    // Keyboard is not visible, show the element
                    layoutToHide.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.backButton) {
//            Log.d("BackButton", "Back button clicked!");
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//            finish();
//        }
    }

    // add city to db
    private void searchCity(String cityName) {

        String url = "https://dataservice.accuweather.com/locations/v1/cities/autocomplete";
        String apiKey = getString(R.string.ACCU_API_KEY);
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
                // get area object and area name
                JSONObject area = cityObject.getJSONObject("AdministrativeArea");
                String areaName = area.getString("LocalizedName");
                // get country object and country name
                JSONObject country = cityObject.getJSONObject("Country");
                String countryName = country.getString("LocalizedName");
                CityWeather city = new CityWeather(cityName, locationKey);
                city.addAreaAndCountry(areaName, countryName);
                cityList.add(city);
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
            // get lon and lat
            getCityCoordinates(city.getLocationKey(), new Callback() {
                @Override
                public void onSuccess(double longitude, double latitude) {
                    // update db with lon and lat
                    boolean res = mydbHelper.addCityForUser(userId, city.getName(), city.getLocationKey(), longitude, latitude);
                    if(!res)  Log.d("FailtowriteDB", "FailtowriteDB " );
                    mydbHelper.updateUserCityKey(userId, city.getLocationKey());
                    runOnUiThread(() -> Toast.makeText(CitySearchActivity.this, "City added successfully", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onFailure() {
                    runOnUiThread(() -> Toast.makeText(CitySearchActivity.this, "Failed to get city coordinates", Toast.LENGTH_SHORT).show());
                }
            });
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 300);

    }

    // request by location key for city lon and lat
    private void getCityCoordinates(String locationKey, Callback callback) {
        String url = "https://dataservice.accuweather.com/locations/v1/" + locationKey;
        String apiKey = "NMAmiNk68RDbijC9HZ18ue7J643yCOli";

        String fullUrl = String.format("%s?apikey=%s", url, apiKey);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullUrl,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        double longitude = jsonResponse.getJSONObject("GeoPosition").getDouble("Longitude");
                        double latitude = jsonResponse.getJSONObject("GeoPosition").getDouble("Latitude");
                        callback.onSuccess(longitude, latitude);
                    } catch (JSONException e) {
                        Log.e("CitySearchActivity", "JSON parsing error: " + e.getMessage());
                        callback.onFailure();
                    }
                }, error -> {
            Log.e("CitySearchActivity", "Network error: " + error.getMessage());
            callback.onFailure();
        });

        queue.add(stringRequest);
    }

    private interface Callback {
        void onSuccess(double longitude, double latitude);
        void onFailure();
    }

    // enable back button
    public void onBackButtonClicked(View view) {
        Log.d("BackButton", "Back button clicked!");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        finish();
    }

}
