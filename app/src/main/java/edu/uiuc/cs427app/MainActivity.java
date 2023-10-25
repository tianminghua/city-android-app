package edu.uiuc.cs427app;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.uiuc.cs427app.databinding.ActivityMainBinding;

import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private CityAdapter cityAdapter;
    List<String>cityList = new ArrayList<>(); // city list to show
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private FirebaseAuth mAuth;
    //private FirebaseFirestore db;




    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout programmatically

        // Show city list
        //cityList = DBManager.queryAllCityName();
//        if(cityList.size()==0){
//            cityList.add("Champaign");
//            cityList.add("LA");
//            cityList.add("NYC");
//            cityAdapter.notifyDataSetChanged();
//        }

        recyclerView = findViewById(R.id.recyclerView);
        cityAdapter = new CityAdapter(cityList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cityAdapter);
        loadData();




//        Button buttonChampaign = findViewById(R.id.buttonChampaign);
//        Button buttonChicago = findViewById(R.id.buttonChicago);
//        Button buttonLA = findViewById(R.id.buttonLA);
        Button buttonNew = findViewById(R.id.buttonAddLocation);

        Button buttonLogout = findViewById(R.id.logout);
        TextView welcomeNote = findViewById(R.id.welcomeNote);

        Button buttonDelete = findViewById(R.id.listManagementButton);


//        buttonChampaign.setOnClickListener(this);
//        buttonChicago.setOnClickListener(this);
//        buttonLA.setOnClickListener(this);
        buttonNew.setOnClickListener(this);

        buttonLogout.setOnClickListener(this);

        // show the user name after login
//        mAuth = FirebaseAuth.getInstance();
//
//        User currUser = (User)getIntent().getSerializableExtra("user");
//
//        if (currUser == null) {
//            mAuth.signOut();
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            welcomeNote.setVisibility(View.VISIBLE);
//            welcomeNote.setText("Welcome Back, " + currUser.getName() +
//                    "\n" + "Email: " + currUser.getEmail() + "    UI Theme: " + currUser.getTheme());
//        }

        buttonDelete.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
//            case R.id.buttonChampaign:
//                intent = new Intent(this, DetailsActivity.class);
//                intent.putExtra("city", "Champaign");
//                startActivity(intent);
//                break;
//            case R.id.buttonChicago:
//                intent = new Intent(this, DetailsActivity.class);
//                intent.putExtra("city", "Chicago");
//                startActivity(intent);
//                break;
//            case R.id.buttonLA:
//                intent = new Intent(this, DetailsActivity.class);
//                intent.putExtra("city", "Los Angeles");
//                startActivity(intent);
//                break;
            case R.id.buttonAddLocation:
                // Implement this action to add a new location to the list of locations
                intent = new Intent(this,CitySearchActivity.class);
                startActivity(intent);
                break;
            case R.id.listManagementButton:
                // Implement this action to add a new location to the list of locations
                intent = new Intent(this,cityDeleteActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;

        }
    }

    // test when click show detail, it will get city name
    public void showDetails(View view) {
        int itemPosition = recyclerView.getChildLayoutPosition((View) view.getParent());
        String cityName = cityList.get(itemPosition);
        Toast Toast = null;
        Toast.makeText(this, "Showing details for " + cityName, Toast.LENGTH_SHORT).show();
    }

    // load city data
    private void loadData() {
        cityList.add("New York");
        cityList.add("Los Angeles");
        cityList.add("Chicago");
        //Log.d("MainActivity", "Loaded cities: " + cityList.size());
        cityAdapter.notifyDataSetChanged();
    }
}



