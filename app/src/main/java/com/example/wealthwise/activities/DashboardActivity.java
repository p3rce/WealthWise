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
import com.example.wealthwise.adapters.AdvicePageAdapter;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    private Button addExpenseButton, viewAllExpensesButton;
    private WealthWiseDatabase database;
    private TextView totalAmountTextView;
    private ViewPager2 viewPager, aiAdvicePager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);  // Ensure this is called first

        // Initialize all views after setting the content view
        database = WealthWiseDatabase.getDatabase(getApplicationContext());
//        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        aiAdvicePager = findViewById(R.id.aiAdvicePager);

        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ExpenseEntryActivity.class);
            startActivity(intent);
        });


        loadChartData();


        aiAdvicePager.setUserInputEnabled(false);
//        startAdviceAutoCycle();


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
//        calculateTotalAmountSpentThisMonth();
        loadChartData();
    }


    private void loadChartData() {

        new Thread(() -> {

            List<PieEntry> categoryData = getCategoryData();
            List<PieEntry> monthlyData = getMonthlyData();

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




    private void updateAIAdvice(List<PieEntry> entries) {
        List<String> adviceList;

        if (entries.isEmpty()) {
            // No expenses are loaded, use default advice
            adviceList = generateAdviceForCategory("default");
        } else {
            // Sort entries by expense amount to find the top category
            entries.sort((entry1, entry2) -> Float.compare(entry2.getValue(), entry1.getValue()));
            String topCategory = entries.get(0).getLabel();

            // Generate advice based on the highest expense category
            adviceList = generateAdviceForCategory(topCategory);
        }

        // Set adapter on the main thread
        runOnUiThread(() -> {
            AdvicePageAdapter adviceAdapter = new AdvicePageAdapter(this, adviceList);
            aiAdvicePager.setAdapter(adviceAdapter);

            // Start auto-cycle only after adapter is set
            startAdviceAutoCycle();
        });
    }





    private List<String> generateAdviceForCategory(String category) {

        List<String> adviceList = new ArrayList<>();
        switch (category) {

            case "Transportation":
                adviceList.add("Consider using public transit to save fuel.");
                adviceList.add("Try carpooling to reduce expenses.");
                adviceList.add("Walk or bike to save money.");
                break;

            case "Food":
                adviceList.add("Prepare meals at home instead of dining out.");
                adviceList.add("Consider buying in bulk to save on groceries.");
                adviceList.add("Limit takeout meals to cut down on food expenses.");
                break;
            case "Entertainment":
                adviceList.add("Look for free or low-cost entertainment options.");
                adviceList.add("Limit subscriptions and memberships to save money.");
                adviceList.add("Plan home movie nights instead of going to the cinema.");
                break;

            case "Utilities":
                adviceList.add("Turn off lights and electronics when not in use.");
                adviceList.add("Switch to energy-efficient appliances to lower utility bills.");
                adviceList.add("Use programmable thermostats to optimize heating and cooling.");
                adviceList.add("Consider bundling services (e.g., internet and phone) for discounts.");
                break;

            case "Household Expenses":
                adviceList.add("Shop for generic brands to save on household supplies.");
                adviceList.add("Take advantage of sales and coupons for big savings.");
                adviceList.add("Avoid impulse buys by creating a shopping list and sticking to it.");
                adviceList.add("Reuse and recycle items where possible to reduce spending.");
                break;

            case "Health":
                adviceList.add("Explore fitness apps or community resources for free workouts.");
                adviceList.add("Look for discounts on medications or use generics when available.");
                adviceList.add("Schedule preventive care visits to avoid costly treatments later.");
                adviceList.add("Consider switching to an insurance plan that better fits your needs.");
                break;

            case "Travel":
                adviceList.add("Book flights and accommodations in advance for better deals.");
                adviceList.add("Travel during off-peak times to save on expenses.");
                adviceList.add("Use loyalty programs or credit card rewards for discounts.");
                adviceList.add("Consider local or budget-friendly destinations for vacations.");
                break;

            case "Shopping":
                adviceList.add("Wait for sales events like Black Friday or end-of-season sales.");
                adviceList.add("Compare prices online before making purchases.");
                adviceList.add("Limit non-essential shopping to reduce impulsive expenses.");
                adviceList.add("Use cashback or rewards programs for extra savings.");
                break;

            default:
                adviceList.add("Analyze your spending habits to identify savings opportunities.");
                adviceList.add("Set a monthly budget and track your expenses regularly.");
                adviceList.add("Avoid unnecessary purchases by prioritizing needs over wants.");
                adviceList.add("Look for ways to reduce subscriptions or recurring expenses.");
                break;

        }


        return adviceList;

    }


    private void startAdviceAutoCycle() {
        aiAdvicePager.post(new Runnable() {
            int currentPage = 0;

            @Override
            public void run() {
                // Ensure adapter is set before accessing it
                if (aiAdvicePager.getAdapter() != null) {
                    int itemCount = aiAdvicePager.getAdapter().getItemCount();
                    if (itemCount > 0) {
                        currentPage = (currentPage + 1) % itemCount;
                        aiAdvicePager.setCurrentItem(currentPage, true); // Smooth scroll to next item
                    }
                }
                aiAdvicePager.postDelayed(this, 5000); // Change advice every 5 seconds
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
