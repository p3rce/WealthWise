package com.example.wealthwise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wealthwise.R;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    private PieChart pieChart1, pieChart2, pieChart3;
    private Button addExpenseButton, viewAllExpensesButton;
    private WealthWiseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        pieChart1 = findViewById(R.id.pieChart1);
        pieChart2 = findViewById(R.id.pieChart2);
        pieChart3 = findViewById(R.id.pieChart3);
        addExpenseButton = findViewById(R.id.addExpenseButton);
//        viewAllExpensesButton = findViewById(R.id.viewAllExpensesButton);
        database = WealthWiseDatabase.getDatabase(getApplicationContext());

        configurePieChart(pieChart1);
        configurePieChart(pieChart2);
        configurePieChart(pieChart3);

        // Add Expense button click action
        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ExpenseEntryActivity.class);
            startActivity(intent);
        });

        // View All Expenses button click action
//        viewAllExpensesButton.setOnClickListener(v -> {
//            Intent intent = new Intent(DashboardActivity.this, ExpenseListActivity.class);
//            startActivity(intent);
//        });
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
        loadChartData(); // Refresh chart data when returning to this activity
    }

    private void configurePieChart(PieChart pieChart) {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setEntryLabelTextSize(14f);
        pieChart.setEntryLabelColor(getResources().getColor(android.R.color.white));

        // Remove the legend (bottom labels)
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
    }

    private void loadChartData() {
        // Load data for each chart
        loadCategoryWiseData(pieChart1);
        loadWeeklyData(pieChart2);
        loadMonthlyData(pieChart3);
    }

    private void loadCategoryWiseData(PieChart pieChart) {
        new Thread(() -> {
            List<Expense> expenses = database.expenseDao().getAllExpenses();
            List<PieEntry> entries = new ArrayList<>();

            Map<String, Float> categorySums = new HashMap<>();
            for (Expense expense : expenses) {
                float currentSum = categorySums.getOrDefault(expense.getCategory(), 0f);
                categorySums.put(expense.getCategory(), currentSum + (float) expense.getAmount());
            }

            for (Map.Entry<String, Float> entry : categorySums.entrySet()) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }

            runOnUiThread(() -> updatePieChart(pieChart, entries));
        }).start();
    }

    private void loadWeeklyData(PieChart pieChart) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(150f, "Week 1"));
        entries.add(new PieEntry(200f, "Week 2"));
        entries.add(new PieEntry(180f, "Week 3"));
        entries.add(new PieEntry(220f, "Week 4"));

        updatePieChart(pieChart, entries);
    }

    private void loadMonthlyData(PieChart pieChart) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(500f, "January"));
        entries.add(new PieEntry(750f, "February"));
        entries.add(new PieEntry(620f, "March"));

        updatePieChart(pieChart, entries);
    }

    private void updatePieChart(PieChart pieChart, List<PieEntry> entries) {
        if (entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("No data available");
        } else {
            PieDataSet dataSet = new PieDataSet(entries, null);

            dataSet.setColors(getResources().getColor(R.color.entertainmentColor),
                    getResources().getColor(R.color.transportationColor),
                    getResources().getColor(R.color.householdColor),
                    getResources().getColor(R.color.foodColor),
                    getResources().getColor(R.color.utilitiesColor),
                    getResources().getColor(R.color.healthColor),
                    getResources().getColor(R.color.otherColor));

            dataSet.setValueTextSize(16f);
            dataSet.setValueTextColor(getResources().getColor(android.R.color.white));
            dataSet.setValueFormatter(new DollarValueFormatter());

            PieData data = new PieData(dataSet);
            pieChart.setData(data);
            pieChart.invalidate(); // Refresh chart
        }
    }

    private static class DollarValueFormatter extends ValueFormatter {
        private final DecimalFormat decimalFormat;

        public DollarValueFormatter() {
            this.decimalFormat = new DecimalFormat("0.##");
        }

        @Override
        public String getFormattedValue(float value) {
            return "$" + decimalFormat.format(value);
        }
    }
}
