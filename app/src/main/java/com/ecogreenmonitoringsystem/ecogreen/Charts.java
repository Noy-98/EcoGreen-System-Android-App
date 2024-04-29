package com.ecogreenmonitoringsystem.ecogreen;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Charts extends AppCompatActivity {

    ImageView backBtn;
    PieChart pieChart;
    LineChart lineChart, lineChart2, lineChart3;
    TextView Date, Date2, Date3;

    // Firebase
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_charts);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("SensorsData");

        // Hooks
        backBtn = findViewById(R.id.back_p);
        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);
        lineChart2 = findViewById(R.id.lineChart2);
        lineChart3 = findViewById(R.id.lineChart3);
        Date = findViewById(R.id.date);
        Date2 = findViewById(R.id.date2);
        Date3 = findViewById(R.id.date3);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Charts.super.onBackPressed();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Update the PieChart
                    float temperature = dataSnapshot.child("Temperature").exists() ? dataSnapshot.child("Temperature").getValue(Float.class) : 0.0f;
                    float humidity = dataSnapshot.child("Humidity").exists() ? dataSnapshot.child("Humidity").getValue(Float.class) : 0.0f;
                    float moisture = dataSnapshot.child("Moisture").exists() ? dataSnapshot.child("Moisture").getValue(Float.class) : 0.0f;
                    String dDate = dataSnapshot.child("Date").getValue(String.class);
                    updatePieChart(temperature, humidity, moisture);

                    Date.setText(dDate);
                    Date2.setText(dDate);
                    Date3.setText(dDate);

                    // Update the LineChart
                    List<Entry> entries = new ArrayList<>();
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        long timestamp = convertDateToTimestamp(date);
                        entries.add(new Entry(timestamp, moisture));
                    }

                    Collections.sort(entries, new Comparator<Entry>() {
                        @Override
                        public int compare(Entry entry1, Entry entry2) {
                            return Long.compare((long) entry1.getX(), (long) entry2.getX());
                        }
                    });

                    updateLineChart(lineChart, entries, "Moisture");

                    // Update the LineChart2
                    List<Entry> entries2 = new ArrayList<>();
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        long timestamp = convertDateToTimestamp(date);
                        entries2.add(new Entry(timestamp, temperature));
                    }

                    Collections.sort(entries2, new Comparator<Entry>() {
                        @Override
                        public int compare(Entry entry1, Entry entry2) {
                            return Long.compare((long) entry1.getX(), (long) entry2.getX());
                        }
                    });

                    updateLineChart(lineChart2, entries2, "Temperature");

                    // Update the LineChart3
                    List<Entry> entries3 = new ArrayList<>();
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        long timestamp = convertDateToTimestamp(date);
                        entries3.add(new Entry(timestamp, humidity));
                    }

                    Collections.sort(entries3, new Comparator<Entry>() {
                        @Override
                        public int compare(Entry entry1, Entry entry2) {
                            return Long.compare((long) entry1.getX(), (long) entry2.getX());
                        }
                    });

                    updateLineChart(lineChart3, entries3, "Humidity");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Charts.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePieChart(float temperature, float humidity, float moisture) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(temperature, "Temperature"));
        entries.add(new PieEntry(humidity, "Humidity"));
        entries.add(new PieEntry(moisture, "Moisture"));

        PieDataSet dataSet = new PieDataSet(entries, "Sensor Data");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setDescription(null);
        pieChart.setCenterText("Sensor Data");
        pieChart.animateY(1000);
    }

    private void updateLineChart(LineChart lineChart, List<Entry> entries, String label) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        int color;

        // Assign color based on label
        switch (label) {
            case "Temperature":
                color = getResources().getColor(R.color.red);
                break;
            case "Humidity":
                color = getResources().getColor(R.color.brown);
                break;
            case "Moisture":
                color = getResources().getColor(R.color.blue);
                break;
            default:
                color = getResources().getColor(R.color.blue);
        }

        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DateAxisValueFormatter());
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }

    private long convertDateToTimestamp(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = sdf.parse(date);
            return parsedDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
}