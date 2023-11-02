package edu.uiuc.cs427app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
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
public class WeatherActivity extends AppCompatActivity {
    private TextView cityTextView;
    private TextView dateTextView;
    private TextView temperatureTextView;
    private TextView weatherConditionTextView;
    private TextView humidityTextView;
    private TextView windConditionTextView;

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
        setContentView(R.layout.activity_city_weather);

        cityTextView = findViewById(R.id.CityName);
        dateTextView = findViewById(R.id.Date);
        temperatureTextView = findViewById(R.id.Temp);
        weatherConditionTextView = findViewById(R.id.Weather);
        humidityTextView = findViewById(R.id.Hum);
        windConditionTextView = findViewById(R.id.Wind);

        String cityName = getIntent().getStringExtra("cityName");
        String locationKey = getIntent().getStringExtra("locationKey");

        fetchWeatherData(locationKey, cityName);
    }

    private interface WeatherCallback {
        void onSuccess(String Date, String temperature, String weatherCondition, String humidity, String windCondition);
        void onFailure();
    }

    private void fetchWeatherData(String locationKey, String cityname) {
        if(locationKey == null)
            Log.d("LocationKey", "Location Key: " );
        String url = "https://dataservice.accuweather.com/currentconditions/v1/" + locationKey;
        String apiKey = getString(R.string.ACCU_API_KEY);

        String fullUrl = String.format("%s?apikey=%s&details=%s", url, apiKey, "true");

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullUrl,
                response -> {
                    try {
                        JSONArray jsonResponseArray = new JSONArray(response);
                        JSONObject jsonResponse = jsonResponseArray.getJSONObject(0);;
                        String date = jsonResponse.getString("LocalObservationDateTime");
                        String dateSub = date.substring(0, 16);
                        String dateFinal = dateSub.replace('T', ' ');
                        String weatherCondition = jsonResponse.getString("WeatherText");
                        String humidity = jsonResponse.isNull("RelativeHumidity") ? "N/A" : jsonResponse.getString("RelativeHumidity");

                        JSONObject temperatureObject = jsonResponse.getJSONObject("Temperature");
                        JSONObject metricObject = temperatureObject.getJSONObject("Metric");
                        double temperatureValue = metricObject.getDouble("Value");
                        String temperature = String.format("%.2f °C", temperatureValue);

                        JSONObject windObject = jsonResponse.getJSONObject("Wind").getJSONObject("Speed").getJSONObject("Metric");
                        String windSpeed = windObject.getString("Value");
                        String speedUnit = windObject.getString("Unit");
                        JSONObject windDirectionObject = jsonResponse.getJSONObject("Wind").getJSONObject("Direction");
                        String windDirection = windDirectionObject.getString("English");
                        String windCondition = windSpeed + " " + speedUnit + " " + windDirection;

                        runOnUiThread(() -> {
                            cityTextView.setText("City Name: " + cityname);
                            dateTextView.setText("Date & Time: " + dateFinal);
                            temperatureTextView.setText("Temperature: " + temperature);
                            weatherConditionTextView.setText("Weather: " + weatherCondition);
                            humidityTextView.setText("Humidity: " + humidity + "%");
                            windConditionTextView.setText("Wind: " + windCondition);
                        });

                    } catch (JSONException e) {
                        Log.e("WeatherActivity", "JSON parsing error: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show());
                    }
                }, error -> {
            Log.e("WeatherActivity", "Network error: " + error.getMessage());
            runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show());
        });

        queue.add(stringRequest);
    }

    // enable back button
    public void onBackButtonClicked(View view) {
        finish();
    }
}
