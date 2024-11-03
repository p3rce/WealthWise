package com.example.wealthwise.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wealthwise.R;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;
    private Button addExpenseButton;
    private WealthWiseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        addExpenseButton = findViewById(R.id.addExpenseButton);

        database = WealthWiseDatabase.getDatabase(getApplicationContext());

        pieChart = findViewById(R.id.pieChart);
        loadCategoryWiseData();

        barChart = findViewById(R.id.barChart);
        loadMonthlySpendingData();

        configurePieChart();


        addExpenseButton.setOnClickListener(v -> {
//            Toast.makeText(DashboardActivity.this, "Button clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DashboardActivity.this, ExpenseEntryActivity.class);
            startActivity(intent);
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_view_expenses) {

            Intent intent = new Intent(this, ExpenseListActivity.class);
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadCategoryWiseData();
    }


    private void configurePieChart() {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(getResources().getColor(android.R.color.white));

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        legend.setDrawInside(false);
//        legend.setTextSize(12f);
//        legend.setFormSize(12f);

    }


    private void loadCategoryWiseData() {
        new Thread(() -> {
            List<Expense> expenses = database.expenseDao().getAllExpenses();
            List<PieEntry> entries = new ArrayList<>();

            // Aggregate expenses by category
            Map<String, Float> categorySums = new HashMap<>();
            for (Expense expense : expenses) {
                float currentSum = categorySums.getOrDefault(expense.getCategory(), 0f);
                categorySums.put(expense.getCategory(), currentSum + (float) expense.getAmount());
            }

            // Convert aggregated data to PieEntry objects
            for (Map.Entry<String, Float> entry : categorySums.entrySet()) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }

            // Update the chart on the main thread
            runOnUiThread(() -> {
                if (entries.isEmpty()) {
                    pieChart.clear();
                    pieChart.setNoDataText("No expenses available");
                } else {
                    PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");

                    // Set custom colors
                    dataSet.setColors(getResources().getColor(R.color.entertainmentColor),
                            getResources().getColor(R.color.transportationColor),
                            getResources().getColor(R.color.householdColor),
                            getResources().getColor(R.color.foodColor),
                            getResources().getColor(R.color.utilitiesColor),
                            getResources().getColor(R.color.healthColor),
                            getResources().getColor(R.color.otherColor));

                    dataSet.setValueTextSize(25f);

                    dataSet.setValueFormatter(new DollarValueFormatter()); // Show percentages for values

                    PieData data = new PieData(dataSet);
                    pieChart.setData(data);
                    pieChart.invalidate(); // Refresh chart
                }
            });
        }).start();
    }


    private static class DollarValueFormatter extends ValueFormatter {
        private final DecimalFormat decimalFormat;

        public DollarValueFormatter() {
            this.decimalFormat = new DecimalFormat("0.##");
        }


        @Override
        public String getFormattedValue(float value) {
            // Use HTML to make the text bold
            return "$" + decimalFormat.format(value);
        }

    }


    private void loadMonthlySpendingData() {

//        List<BarEntry> entries = new ArrayList<>();
//        entries.add(new BarEntry(1,500)); //Jan Spending
//        entries.add(new BarEntry(2,600)); //Feb Spending
//        entries.add(new BarEntry(3,450)); //Mar Spending
//
//        BarDataSet dataSet = new BarDataSet(entries, "Monthly Spending");
//        BarData data = new BarData(dataSet);
//        barChart.setData(data);
//        barChart.invalidate();


    }


}
