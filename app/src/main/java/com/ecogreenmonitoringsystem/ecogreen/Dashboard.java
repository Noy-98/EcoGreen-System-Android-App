package com.ecogreenmonitoringsystem.ecogreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    TextView humidityTextView, moistureTextView, temperatureTextView;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);
        //Icon Hooks
        menuIcon = findViewById(R.id.menu_icon);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        navigationDrawer();

        //TextView
        humidityTextView = findViewById(R.id.humid);
        moistureTextView = findViewById(R.id.moist);
        temperatureTextView = findViewById(R.id.temp);

        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("SensorsData");


        // Add a ValueEventListener to update TextViews when data changes
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        Object value = snapshot.getValue();

                        // Update TextViews based on the key
                        if (key != null && value != null) {
                            switch (key) {
                                case "Humidity":
                                    humidityTextView.setText("Humidity: " + value + "%");
                                    checkHumidityWarning(Integer.parseInt(value.toString()));
                                    break;
                                case "Moisture":
                                    moistureTextView.setText("Moisture: " + value + "%");
                                    checkMoistureWarning(Integer.parseInt(value.toString()));
                                    break;
                                case "Temperature":
                                    temperatureTextView.setText("Temperature: " + value + "°C");
                                    checkTemperatureWarning(Integer.parseInt(value.toString()));
                                    break;
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Handle any errors
                Log.e("Firebase", "Error: " + databaseError.getMessage());

            }
        });

    }

    private void checkTemperatureWarning(int temperatureValue) {
        if (temperatureValue >= 50) {
            showWarningDialog("The Temperature is Very High");
        } else if (temperatureValue <= 10) {
            showWarningDialog("The Temperature is Very Low");
        }
    }

    private void checkMoistureWarning(int moistureValue) {
        if (moistureValue == 1024) {
            showWarningDialog("Soil is Dry");
        } else if (moistureValue <= 300) {
            showWarningDialog("Soil is Very Wet");
        }
    }

    private void checkHumidityWarning(int humidityValue) {
        if (humidityValue >= 85) {
            showWarningDialog("The Amount water vapor is very high");
        } else if (humidityValue <= 30) {
            showWarningDialog("The Amount water vapor is very low");
        }
    }

    private void showWarningDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Warning")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Navigation Drawer Function
    private void navigationDrawer() {
        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            // Handle Home item
            // You can add your code for handling the "Home" item here
        } else if (item.getItemId() == R.id.nav_control) {

            Intent intent = new Intent(this, Controls.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.nav_charts) {

            Intent intent = new Intent(this, Charts.class);
            startActivity(intent);

        }
        // Add more conditions for other menu items as needed

        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}