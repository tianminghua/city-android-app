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
import db.dbHelper;
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
    private dbHelper myDbHelper;
    List<String>mycityList = new ArrayList<>(); // city list to show
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

        Button buttonNew = findViewById(R.id.buttonAddLocation);

        Button buttonLogout = findViewById(R.id.logout);
        TextView welcomeNote = findViewById(R.id.welcomeNote);

        Button buttonDelete = findViewById(R.id.listManagementButton);


        buttonNew.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        myDbHelper = new dbHelper(this);

        // show the username after login
        mAuth = FirebaseAuth.getInstance();

        User currUser = (User)getIntent().getSerializableExtra("user");

        if (currUser == null) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            welcomeNote.setVisibility(View.VISIBLE);
            welcomeNote.setText("Welcome Back, " + currUser.getName() +
                    "\n" + "Email: " + currUser.getEmail() + "    UI Theme: " + currUser.getTheme());


            long userId = myDbHelper.ensureUserExists(currUser.getEmail());
            recyclerView = findViewById(R.id.recyclerView);
            cityAdapter = new CityAdapter(mycityList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(cityAdapter);
            loadData(userId);
        }

        buttonDelete.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonAddLocation:
                intent = new Intent(this, CitySearchActivity.class);
                User currUser = (User) getIntent().getSerializableExtra("user");
                if (currUser != null) {
                    long userId = myDbHelper.ensureUserExists(currUser.getEmail());
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.listManagementButton:
                // Implement this action to add a new location to the list of locations
                intent = new Intent(this,cityDeleteActivity.class);
                User currUser1 = (User) getIntent().getSerializableExtra("user");
                if (currUser1 != null) {
                    long userId = myDbHelper.ensureUserExists(currUser1.getEmail());
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
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
        String cityName = mycityList.get(itemPosition);
        Toast Toast = null;
        Toast.makeText(this, "Showing details for " + cityName, Toast.LENGTH_SHORT).show();
    }

    // load city data from db by username
    private void loadData(long userId) {
        mycityList.clear();
        mycityList.addAll(myDbHelper.getCityListForUser(userId));
        //mycityList = myDbHelper.getCityListForUser(userId);
        cityAdapter.notifyDataSetChanged();
    }

    // update UI to show new city list after come back from other activity
    @Override
    protected void onResume() {
        super.onResume();
        User currUser = (User) getIntent().getSerializableExtra("user");
        if (currUser != null) {
            long userId = myDbHelper.ensureUserExists(currUser.getEmail());
            loadData(userId);
        }
    }


}



