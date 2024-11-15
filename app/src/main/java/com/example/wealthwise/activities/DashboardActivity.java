package com.example.wealthwise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.wealthwise.R;
import com.example.wealthwise.adapters.ChartPagerAdapter;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.CategoryTotal;
import com.example.wealthwise.models.Expense;
import com.example.wealthwise.models.MonthlyTotal;
import com.example.wealthwise.models.WeeklyTotal;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    private Button addExpenseButton, viewAllExpensesButton;
    private WealthWiseDatabase database;
    private TextView totalAmountTextView;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);  // Ensure this is called first

        // Initialize all views after setting the content view
        database = WealthWiseDatabase.getDatabase(getApplicationContext());
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ExpenseEntryActivity.class);
            startActivity(intent);
        });


        loadChartData();


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

        } else if(item.getItemId() == R.id.action_view_info) {
            showInfoDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }



    @Override
    protected void onResume() {
        super.onResume();
        calculateTotalAmountSpentThisMonth();
        loadChartData();
    }


    private void loadChartData() {

        new Thread(() -> {

            List<PieEntry> categoryData = getCategoryData();
            List<PieEntry> weeklyData = getWeeklyData();
            List<PieEntry> monthlyData = getMonthlyData();

            runOnUiThread(() -> {
                ChartPagerAdapter adapter = new ChartPagerAdapter(this, categoryData, weeklyData, monthlyData);
                viewPager.setAdapter(adapter);

                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {

                }).attach();

            });

        }).start();

    }


    private List<PieEntry> getCategoryData() {
        List<CategoryTotal> categoryTotals = database.expenseDao().getCategoryWiseTotal();
        List<PieEntry> entries = new ArrayList<>();
        for (CategoryTotal categoryTotal : categoryTotals) {
            entries.add(new PieEntry((float) categoryTotal.total, categoryTotal.category));
        }
        return entries;
    }

    private List<PieEntry> getWeeklyData() {
        List<WeeklyTotal> weeklyTotals = database.expenseDao().getWeeklyTotal();
        List<PieEntry> entries = new ArrayList<>();
        for (WeeklyTotal weeklyTotal : weeklyTotals) {
            entries.add(new PieEntry((float) weeklyTotal.total, "Week " + weeklyTotal.week));
        }
        return entries;
    }

    private List<PieEntry> getMonthlyData() {
        List<MonthlyTotal> monthlyTotals = database.expenseDao().getMonthlyTotal();
        List<PieEntry> entries = new ArrayList<>();
        for (MonthlyTotal monthlyTotal : monthlyTotals) {
            String monthName = getMonthName(monthlyTotal.month);
            entries.add(new PieEntry((float) monthlyTotal.total, monthName));
        }
        return entries;
    }




    private void showInfoDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("App Information");
        builder.setMessage("Author: Pierce Goulimis\n" +

                "Version: ALPHA\n\n" +
                "Instructions:\n" +
                "- Use 'Add Expense' to add a new expense.\n" +
                "- Use 'View All Expenses' to see a list of all expenses.\n" +
                "- The charts below provide a visual breakdown of your expenses by category, month, etc");

        builder.setPositiveButton("OK", ((dialog, which) -> dialog.dismiss()));
        builder.show();

    }




    private void calculateTotalAmountSpentThisMonth() {

        new Thread(() -> {

            List<Expense> expenses = database.expenseDao().getAllExpenses();
            double totalAmount = 0.0;

            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentYear = calendar.get(Calendar.YEAR);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            for (Expense expense : expenses) {

                try {

                    String[] dateParts = expense.getDate().split("-");
                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);

                    if(year == currentYear && month == currentMonth) {
                        totalAmount += expense.getAmount();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            double finalTotalAmount = totalAmount;
            runOnUiThread(() -> displayTotalAmount(finalTotalAmount));

        }).start();

    }

    private String getMonthName(String monthNumber) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        int monthIndex = Integer.parseInt(monthNumber) - 1;
        return (monthIndex >= 0 && monthIndex < 12) ? months[monthIndex] : "Unknown";
    }

    private void displayTotalAmount(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        String formattedAmount = "$" + decimalFormat.format(amount);

        totalAmountTextView.setText("Total Amount Spent This Month: " + formattedAmount);
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
