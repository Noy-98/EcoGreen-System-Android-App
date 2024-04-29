package com.ecogreenmonitoringsystem.ecogreen;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class DateAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {

    private SimpleDateFormat dateFormat;

    public DateAxisValueFormatter() {
        // Define your date format here
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // Convert timestamp to date string
        return dateFormat.format(new Date((long) value));
    }
}