package com.example.wealthwise.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.wealthwise.R;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Calendar;

public class ExpenseEntryActivity extends AppCompatActivity {

    private String selectedCategory = null; // Holds the selected category
    private CardView currentlySelectedCard = null; // Tracks the currently selected card
    private Button selectDateButton;
    private String selectedDate;
    private TextInputEditText expenseAmountEditText;
    private WealthWiseDatabase database;
    private HashMap<String, CardView> categoryCards = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_entry);

        database = WealthWiseDatabase.getDatabase(this); // Initialize the database
        expenseAmountEditText = findViewById(R.id.expenseAmount);
        selectDateButton = findViewById(R.id.selectDateButton);

        // Initialize category selection
        setupCategorySelection();

        // Set default date to today
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        selectDateButton.setText(selectedDate);

        // Open Date Picker when the button is clicked
        selectDateButton.setOnClickListener(v -> showDatePickerDialog());

        // Submit Button Logic
        findViewById(R.id.submitExpense).setOnClickListener(v -> {
            if (validateInput()) {
                addExpenseToDatabase();
            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Expense");
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Update selectedDate with the chosen date
                    calendar.set(year, month, dayOfMonth);
                    selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                    selectDateButton.setText(selectedDate); // Update the button text
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void setupCategorySelection() {
        categoryCards.put("Transportation", findViewById(R.id.transportationCard));
        categoryCards.put("Food", findViewById(R.id.foodCard));
        categoryCards.put("Entertainment", findViewById(R.id.entertainmentCard));
        categoryCards.put("Utilities", findViewById(R.id.utilitiesCard));
        categoryCards.put("Household Expenses", findViewById(R.id.householdExpensesCard));
        categoryCards.put("Health", findViewById(R.id.healthCard));
        categoryCards.put("Travel", findViewById(R.id.travelCard));
        categoryCards.put("Shopping", findViewById(R.id.shoppingCard));
        // Add more categories as needed

        for (String category : categoryCards.keySet()) {
            CardView cardView = categoryCards.get(category);
            cardView.setOnClickListener(v -> toggleCategorySelection(category, cardView));
        }
    }


    private void toggleCategorySelection(String category, CardView selectedCard) {
        if (currentlySelectedCard == selectedCard) {
            // Deselect the card if it's already selected
            deselectCurrentCategory();
        } else {
            // Select a new category
            selectCategory(category, selectedCard);
        }
    }


    private void selectCategory(String category, CardView selectedCard) {
        deselectCurrentCategory(); // Reset any previously selected card

        selectedCard.setCardBackgroundColor(getResources().getColor(R.color.light_grey)); // Highlight selected card
        selectedCategory = category;
        currentlySelectedCard = selectedCard;
    }


    private void deselectCurrentCategory() {
        if (currentlySelectedCard != null) {
            currentlySelectedCard.setCardBackgroundColor(getResources().getColor(R.color.white));
            currentlySelectedCard = null;
        }
        selectedCategory = null;
    }


    private boolean validateInput() {
        String amountText = expenseAmountEditText.getText().toString().trim();

        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter an expense amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Please select an expense category", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void addExpenseToDatabase() {
        String amountText = expenseAmountEditText.getText().toString().trim();
        float amount = Float.parseFloat(amountText);

        // Get the current date

        // Create a new expense object using the default constructor
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setCategory(selectedCategory);
        expense.setDate(selectedDate);

        // Insert into the database
        new Thread(() -> {
            database.expenseDao().insert(expense);

            runOnUiThread(() -> {
                Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after successful submission
            });
        }).start();
    }
}

