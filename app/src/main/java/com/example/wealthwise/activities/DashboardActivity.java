package com.example.wealthwise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wealthwise.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;

    private Button addExpenseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        addExpenseButton = findViewById(R.id.addExpenseButton);

        pieChart = findViewById(R.id.pieChart);
        loadCategoryWiseData();

        barChart = findViewById(R.id.barChart);
        loadMonthlySpendingData();


        addExpenseButton.setOnClickListener(v -> {
//            Toast.makeText(DashboardActivity.this, "Button clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DashboardActivity.this, ExpenseEntryActivity.class);
            startActivity(intent);
        });


    }


    private void loadCategoryWiseData() {

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(30f, "Entertainment"));
        entries.add(new PieEntry(20f, "Transportation"));
        entries.add(new PieEntry(50f, "Household Expenses"));

        // If entries list is empty, log or handle error
        if (entries.isEmpty()) {
            Log.d("PieChart", "No data available to display");
            return;
        }


        PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
        PieData data = new PieData(dataSet);

        for (PieEntry entry : entries) {
            Log.d("PieEntry", "Label: " + entry.getLabel() + ", Value: " + entry.getValue());
        }

        pieChart.setData(data);
        pieChart.invalidate();

    }


    private void loadMonthlySpendingData() {

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1,500)); //Jan Spending
        entries.add(new BarEntry(2,600)); //Feb Spending
        entries.add(new BarEntry(3,450)); //Mar Spending

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Spending");
        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.invalidate();


    }


}
