package edu.uiuc.cs427app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String cityName;
    double coordX;
    double coordY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // read the user info from the Intent
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
        setContentView(R.layout.activity_map);

        // read the city information from the Intent
        cityName = getIntent().getStringExtra("cityName");
        coordX = getIntent().getDoubleExtra("coordX", 0.00);
        coordY = getIntent().getDoubleExtra("coordY", 0.00);

        TextView cityNameBox = findViewById(R.id.cityNameMap);
        TextView latBox = findViewById(R.id.lat);
        TextView lngBox = findViewById(R.id.lng);

        cityNameBox.setText(cityName);
        String latText = "Latitude: " + coordY;
        latBox.setText(latText);
        String lngText = "Longitude: " + coordX;
        lngBox.setText(lngText);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in the city location and move the camera to that location
        LatLng currCity = new LatLng(coordY, coordX);
        mMap.addMarker(new MarkerOptions()
                .position(currCity)
                .title(cityName));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currCity, 11));
    }

    // enable back button
    public void onBackButtonClicked(View view) {
        finish();
    }
}
