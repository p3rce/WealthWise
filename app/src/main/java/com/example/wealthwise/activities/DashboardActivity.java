package com.example.wealthwise.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

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


    private void loadCategoryWiseData() {


        new Thread(() -> {

            List<Expense> expenses = database.expenseDao().getAllExpenses();
            List<PieEntry> entries = new ArrayList<>();


            Map<String, Float> categorySums = new HashMap<>();
            for(Expense expense : expenses) {
                float currentSum = categorySums.getOrDefault(expense.getCategory(), 0f);
                categorySums.put(expense.getCategory(), currentSum + (float) expense.getAmount());
            }


            for(Map.Entry<String, Float> entry : categorySums.entrySet()) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }


            runOnUiThread(() -> {

                if (entries.isEmpty()) {

                    pieChart.clear();
                    pieChart.setNoDataText("No expenses to show");

                } else {

                    PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
                    PieData data = new PieData(dataSet);
                    pieChart.setData(data);
                    pieChart.invalidate();

                }

            });


        }).start();



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
