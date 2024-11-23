package com.example.wealthwise.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class DollarValueFormatter extends ValueFormatter {

    private final DecimalFormat decimalFormat = new DecimalFormat("$#,###.##");

    @Override
    public String getFormattedValue(float value) {

        if (value == (long) value) {
            return "$" + String.format("%d", (long) value);

        } else {
            return decimalFormat.format(value);
        }

    }

}
