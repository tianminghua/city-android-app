package edu.uiuc.cs427app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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
        // Add a marker in Sydney and move the camera
        LatLng currCity = new LatLng(coordY, coordX);
        mMap.addMarker(new MarkerOptions()
                .position(currCity)
                .title(cityName));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currCity, 11));
    }
}