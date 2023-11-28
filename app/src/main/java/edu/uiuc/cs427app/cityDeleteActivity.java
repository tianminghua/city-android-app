package edu.uiuc.cs427app;

import android.os.Handler;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import db.dbHelper;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class cityDeleteActivity extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView recyclerView;
    private CityDeleteAdapter adapter;
    private List<String> cityList;
    private dbHelper myDbHelper;
    private long userId;
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
        setContentView(R.layout.activity_city_delete);

        // city delete
        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userId = getIntent().getLongExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID is not passed properly", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        myDbHelper = new dbHelper(this);
        cityList = myDbHelper.getCityListForUser(userId);
        adapter = new CityDeleteAdapter(cityList, new CityDeleteAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                confirmAndDelete(position);
            }
        });
        recyclerView.setAdapter(adapter);

        //back to front
        ImageButton buttonBack = findViewById(R.id.backButton);
        buttonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // return ro front
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;
        }
    }

    // confirm delete or cancel
    private void confirmAndDelete(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this city?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    // delete if confirm
                    public void onClick(DialogInterface dialog, int which) {
                        myDbHelper.deleteCityForUser(userId, cityList.get(position));
                        cityList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(cityDeleteActivity.this, "City deleted successfully", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 300);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}