package com.example.wealthwise.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wealthwise.R;
import com.example.wealthwise.adapters.ExpenseAdapter;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseListActivity extends AppCompatActivity {

    private RecyclerView expenseRecyclerView;
    private ExpenseAdapter expenseAdapter;
    private WealthWiseDatabase database;
    private ExecutorService executorService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Expenses");
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    finish();
            }
        });

        expenseRecyclerView = findViewById(R.id.expenseRecyclerView);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        database = WealthWiseDatabase.getDatabase(getApplicationContext());

        executorService = Executors.newSingleThreadExecutor();

        loadExpenses();

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void loadExpenses() {

        executorService.execute(() -> {
            List<Expense> expenses = database.expenseDao().getAllExpenses();
            runOnUiThread(() -> {
                if (expenses.isEmpty()) {
                    expenseRecyclerView.setVisibility(View.GONE);
                } else {
                    expenseRecyclerView.setVisibility(View.VISIBLE);
                    expenseAdapter = new ExpenseAdapter(expenses, database);
                    expenseRecyclerView.setAdapter(expenseAdapter);
                }
            });
        });

    }


}