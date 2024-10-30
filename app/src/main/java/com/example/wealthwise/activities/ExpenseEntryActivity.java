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

public class ExpenseEntryActivity extends AppCompatActivity {

    private EditText amountEditText;
    private Spinner categorySpinner;
    private Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_entry);

        amountEditText = findViewById(R.id.amountEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveExpense());


        ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(this, R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        categorySpinner.setAdapter(adapter);


    }


    private void saveExpense() {

        String category = categorySpinner.getSelectedItem().toString();
        double amount = Double.parseDouble(amountEditText.getText().toString());
        String date = getCurrentDate();

        Expense expense = new Expense(category, amount, date);
        WealthWiseDatabase.getDatabase(getApplicationContext()).expenseDao().insert(expense);
        Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();

    }


    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }


}
