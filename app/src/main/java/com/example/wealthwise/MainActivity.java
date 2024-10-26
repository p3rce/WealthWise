package com.example.wealthwise;

/*
@author: Pierce Goulimis
@desc: MainActivity - Define basic logic for displaying expenses + adding new ones, implement click listeners and adapters for the RecyclerView
 */

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wealthwise.Expense;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //MARK: - Properties
    private WealthWiseDatabase database;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = WealthWiseDatabase.getInstance(this);
        recyclerView = findViewById(R.id.recyclerViewExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Button addExpenseButton = findViewById(R.id.buttonAddExpense);
        addExpenseButton.setOnClickListener(view -> {
            //Code to add new expense (will handle dialog later)

        });

        loadExpenses();

    }



    private void loadExpenses() {

        List<Expense> expenses = database.expenseDao().getAllExpenses();
        adapter = new ExpenseAdapter(expenses);
        recyclerView.setAdapter(adapter);

    }


}
