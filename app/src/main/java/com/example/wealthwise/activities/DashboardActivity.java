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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wealthwise.R;
import com.example.wealthwise.adapters.AdvicePageAdapter;
import com.example.wealthwise.adapters.ChartPagerAdapter;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.CategoryTotal;
import com.example.wealthwise.models.Expense;
import com.example.wealthwise.models.MonthlyTotal;
import com.example.wealthwise.models.WeeklyTotal;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.content.SharedPreferences;
import com.github.mikephil.charting.components.AxisBase;

public class DashboardActivity extends AppCompatActivity {


    private Button addExpenseButton, viewAllExpensesButton;
    private WealthWiseDatabase database;
    private ViewPager2 viewPager, aiAdvicePager;
    private TabLayout tabLayout;
    private TextView userNameTextView, budgetTextView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private ProgressBar budgetProgressBar;
    private TextView progressBarDetails;
    private float totalBudget;
    private float totalExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        database = WealthWiseDatabase.getDatabase(getApplicationContext());
        addExpenseButton = findViewById(R.id.addExpenseButton);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        aiAdvicePager = findViewById(R.id.aiAdvicePager);
        userNameTextView = userNameTextView = findViewById(R.id.userNameTextView);
        budgetTextView = findViewById(R.id.budgetTextView);

        budgetProgressBar = findViewById(R.id.budgetProgressBar);
        progressBarDetails = findViewById(R.id.progressBarDetails);


        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ExpenseEntryActivity.class);
            startActivity(intent);
        });


        SharedPreferences sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE);
        totalBudget = sharedPreferences.getFloat("budget", 0f);


        calculateTotalExpenses();


        preferenceChangeListener = (sharedPrefs, key) -> {
            if ("budget".equals(key)) {
                totalBudget = sharedPrefs.getFloat("budget", 0f);
                updateProgressBar();
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        loadChartData();
        loadUserSettings();


        aiAdvicePager.setUserInputEnabled(false);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
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
        } else if (item.getItemId() == R.id.action_view_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }



    @Override
    protected void onResume() {
        super.onResume();
        loadChartData();
        loadUserSettings();
        calculateTotalExpenses();
    }

    private void calculateTotalExpenses() {
        new Thread(() -> {
            List<Expense> expenses = database.expenseDao().getAllExpenses();
            totalExpenses = 0f;


            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentYear = calendar.get(Calendar.YEAR);

            for (Expense expense : expenses) {
                try {

                    String[] parts = expense.getDate().split("-");
                    int expenseYear = Integer.parseInt(parts[0]);
                    int expenseMonth = Integer.parseInt(parts[1]);


                    if (expenseYear == currentYear && expenseMonth == currentMonth) {
                        totalExpenses += expense.getAmount();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            runOnUiThread(() -> updateProgressBar());
        }).start();
    }


    private void updateProgressBar() {


        budgetProgressBar.setMax((int) totalBudget);
        budgetProgressBar.setProgress((int) totalExpenses);


        progressBarDetails.setText(String.format("Spent: $%.2f / $%.2f", totalExpenses, totalBudget));


        if (totalExpenses / totalBudget <= 0.5) {
            budgetProgressBar.setProgressTintList(getColorStateList(R.color.green));
        } else if (totalExpenses / totalBudget <= 0.8) {
            budgetProgressBar.setProgressTintList(getColorStateList(R.color.shoppingColor));
        } else {
            budgetProgressBar.setProgressTintList(getColorStateList(R.color.red));
        }
    }


    private void loadUserSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE);

        String name = sharedPreferences.getString("name", "User");
        float budget = sharedPreferences.getFloat("budget", 0f);

        userNameTextView.setText("Welcome, " + name);
        budgetTextView.setText("Budget: " + (budget == 0f ? "Not Set" : "$" + (int) budget));
    }


    private void loadChartData() {

        new Thread(() -> {

            List<PieEntry> categoryData = getCategoryData();
            List<BarEntry> monthlyData = getMonthlyBarData();

            runOnUiThread(() -> {
                ChartPagerAdapter adapter = new ChartPagerAdapter(this, categoryData, monthlyData);
                viewPager.setAdapter(adapter);

                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {

                    switch (position) {
                        case 0:
                            tab.setText("By-Category");
                            break;

                        case 1:
                            tab.setText("By-Month");
                            break;
                    }

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

        updateAIAdvice(entries);
        return entries;
    }

    private List<BarEntry> getMonthlyBarData() {
        List<Expense> expenses = database.expenseDao().getAllExpenses();
        Map<Integer, Float> monthTotals = new HashMap<>();

        for (Expense expense : expenses) {
            try {

                String[] parts = expense.getDate().split("-");
                int month = Integer.parseInt(parts[1]);

                float currentAmount = (float) expense.getAmount();
                monthTotals.put(month, monthTotals.getOrDefault(month, 0f) + currentAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Float> entry : monthTotals.entrySet()) {
            int month = entry.getKey() - 1;
            entries.add(new BarEntry(month, entry.getValue()));
        }

        return entries;
    }


    private void configureBarChart(BarChart barChart, List<BarEntry> entries) {


        BarDataSet barDataSet = new BarDataSet(entries, "Monthly Expenses");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(12f);


        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (Math.floor(value) == value) {
                    return "$" + (int) value;
                } else {
                    return "$" + String.format("%.2f", value);
                }
            }
        });


        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.8f);


        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.animateY(1000);
        barChart.setFitBars(true);


        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(10f);
        leftAxis.setAxisMinimum(0f);


        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return "$" + (int) value;
            }
        });

        barChart.getAxisRight().setEnabled(false);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getMonths()));

        barChart.invalidate();
    }



    private void updateAIAdvice(List<PieEntry> entries) {
        List<String> adviceList;

        if (entries.isEmpty()) {

            adviceList = generateAdviceForCategory("default");
        } else {

            entries.sort((entry1, entry2) -> Float.compare(entry2.getValue(), entry1.getValue()));
            String topCategory = entries.get(0).getLabel();


            adviceList = generateAdviceForCategory(topCategory);
        }


        runOnUiThread(() -> {
            AdvicePageAdapter adviceAdapter = new AdvicePageAdapter(this, adviceList);
            aiAdvicePager.setAdapter(adviceAdapter);


            startAdviceAutoCycle();
        });
    }





    private List<String> generateAdviceForCategory(String category) {

        List<String> adviceList = new ArrayList<>();
        switch (category) {

            case "Transportation":
                adviceList.add("Consider using public transit to save fuel.");
                adviceList.add("Try carpooling.");
                adviceList.add("Walk or bike to save money.");
                break;

            case "Food":
                adviceList.add("Prepare meals at home instead of dining out.");
                adviceList.add("Consider buying in bulk.");
                adviceList.add("Limit takeout meals.");
                break;
            case "Entertainment":
                adviceList.add("Look for free or low-cost entertainment.");
                adviceList.add("Limit subscriptions and memberships.");
                adviceList.add("Plan home movie nights.");
                break;

            case "Utilities":
                adviceList.add("Turn off lights and electronics.");
                adviceList.add("Switch to energy-efficient appliances.");
                adviceList.add("Use programmable thermostats.");
                adviceList.add("Consider bundling services for discounts.");
                break;

            case "Household Expenses":
                adviceList.add("Shop for generic brands.");
                adviceList.add("Take advantage of sales and coupons.");
                adviceList.add("Avoid impulse buys.");
                adviceList.add("Reuse and recycle items where possible.");
                break;

            case "Health":
                adviceList.add("Explore fitness apps for free workouts.");
                adviceList.add("Look for discounts on medications.");
                adviceList.add("Schedule preventive care visits.");
                break;

            case "Travel":
                adviceList.add("Book flights and accommodations in advance.");
                adviceList.add("Travel during off-peak times.");
                adviceList.add("Use loyalty programs or rewards.");
                adviceList.add("Consider local destinations to vacations.");
                break;

            case "Shopping":
                adviceList.add("Wait for sales events.");
                adviceList.add("Compare prices online.");
                adviceList.add("Limit non-essential shopping.");
                adviceList.add("Use cashback / rewards programs.");
                break;

            default:
                adviceList.add("Analyze your spending habits.");
                adviceList.add("Set a monthly budget.");
                adviceList.add("Avoid unnecessary purchases.");
                adviceList.add("Look for ways to reduce subscriptions.");
                break;

        }


        return adviceList;

    }


    private void startAdviceAutoCycle() {
        aiAdvicePager.post(new Runnable() {
            int currentPage = 0;

            @Override
            public void run() {

                if (aiAdvicePager.getAdapter() != null) {
                    int itemCount = aiAdvicePager.getAdapter().getItemCount();
                    if (itemCount > 0) {
                        currentPage = (currentPage + 1) % itemCount;
                        aiAdvicePager.setCurrentItem(currentPage, true);
                    }
                }
                aiAdvicePager.postDelayed(this, 15000);
            }
        });
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


    private String getMonthName(String monthNumber) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        int monthIndex = Integer.parseInt(monthNumber) - 1;
        return (monthIndex >= 0 && monthIndex < 12) ? months[monthIndex] : "Unknown";
    }

    private List<String> getMonths() {
        return List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
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
