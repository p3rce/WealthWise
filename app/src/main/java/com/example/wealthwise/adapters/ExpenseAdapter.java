package com.example.wealthwise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wealthwise.R;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenses;
    private WealthWiseDatabase database;


    public ExpenseAdapter(List<Expense> expenses, WealthWiseDatabase database) {
        this.expenses = expenses;
        this.database = database;
    }


    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.categoryTextView.setText(expense.getCategory());
        holder.amountTextView.setText(String.valueOf(expense.getAmount()));
        holder.dateTextView.setText(expense.getDate());

        holder.deleteButton.setOnClickListener(v -> {
            deleteExpense(expense);
            expenses.remove(position);
            notifyItemRemoved(position);
        });
    }

    private void deleteExpense(Expense expense) {
        new Thread(() -> database.expenseDao().delete(expense)).start();
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }





    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView categoryTextView, amountTextView, dateTextView;
        Button deleteButton;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

    }




}