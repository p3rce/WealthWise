package com.example.wealthwise.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

public class ExpenseEntryActivity extends AppCompatActivity {

    private EditText amountEditText;
    private Spinner categorySpinner;
    private Button saveButton;
    private WealthWiseDatabase database;
    private ExecutorService executorService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_entry);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Expense");
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        amountEditText = findViewById(R.id.amountEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveButton = findViewById(R.id.saveButton);

        database = WealthWiseDatabase.getDatabase(getApplicationContext());

        executorService = Executors.newSingleThreadExecutor();

        saveButton.setOnClickListener(v -> saveExpense());


        ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(this, R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        categorySpinner.setAdapter(adapter);


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }




    private void saveExpense() {

        String category = categorySpinner.getSelectedItem().toString();
        double amount;
        try {
            amount = Double.parseDouble(amountEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = getCurrentDate();

        // Execute the database insertion on a background thread
        executorService.execute(() -> {
            Expense expense = new Expense(category, amount, date);
            database.expenseDao().insert(expense);

            // Notify on the main thread
            runOnUiThread(() -> {
                Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity and return to DashboardActivity
            });
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }


    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }


}
