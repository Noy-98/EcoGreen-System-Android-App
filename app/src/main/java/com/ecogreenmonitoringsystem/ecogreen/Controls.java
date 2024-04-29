package com.ecogreenmonitoringsystem.ecogreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Controls extends AppCompatActivity {

    ImageView backBtn;
    private Button onButton, offButton, exhaustOnButton, exhaustOffButton, lightsOnButton, lightsOffButton;
    private DatabaseReference databaseRef, exhaustDatabaseRef, lightsDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_controls);

        // Initialize the Firebase Realtime Database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("/SensorsData/Controls/WaterControl");
        exhaustDatabaseRef = FirebaseDatabase.getInstance().getReference("/SensorsData/Controls/ExhaustControl");
        lightsDatabaseRef = FirebaseDatabase.getInstance().getReference("/SensorsData/Controls/LightControl");

        onButton = findViewById(R.id.onButton);
        offButton = findViewById(R.id.offButton);

        exhaustOnButton = findViewById(R.id.onButton2);
        exhaustOffButton = findViewById(R.id.offButton2);

        lightsOnButton = findViewById(R.id.onButton3);
        lightsOffButton = findViewById(R.id.offButton3);

        lightsOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightsDatabaseRef.setValue("1");
                showToast("Lights has been turned OFF");
            }
        });

        lightsOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightsDatabaseRef.setValue("0");
                showToast("Lights has been turned ON");
            }
        });


        exhaustOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exhaustDatabaseRef.setValue("1");
                showToast("Exhaust Fan has been turned OFF");
            }
        });

        exhaustOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exhaustDatabaseRef.setValue("0");
                showToast("Exhaust Fan has been turned ON");
            }
        });

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the "W" value to 1 in Firebase when the "ON" button is clicked
                databaseRef.setValue("0");
                showToast("Watering has been turned ON");
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the "W" value to 0 in Firebase when the "OFF" button is clicked
                databaseRef.setValue("1");
                showToast("Watering has been turned OFF");
            }
        });

        //Hooks
        backBtn = findViewById(R.id.back_p);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Controls.super.onBackPressed();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}