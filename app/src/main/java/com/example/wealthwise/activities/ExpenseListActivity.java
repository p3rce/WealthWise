package com.example.wealthwise.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wealthwise.R;
import com.example.wealthwise.adapters.ExpenseAdapter;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;

import java.util.List;

public class ExpenseListActivity extends AppCompatActivity {

    private RecyclerView expenseRecyclerView;
    private ExpenseAdapter expenseAdapter;
    private WealthWiseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        expenseRecyclerView = findViewById(R.id.expenseRecyclerView);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = WealthWiseDatabase.getDatabase(getApplicationContext());


        loadExpenses();

    }


    private void loadExpenses() {

        new Thread(() -> {
            List<Expense> expenses = database.expenseDao().getAllExpenses();

            runOnUiThread(() -> {
                expenseAdapter = new ExpenseAdapter(expenses, database);
                expenseRecyclerView.setAdapter(expenseAdapter);
            });

        }).start();

    }


}

