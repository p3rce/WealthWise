package com.example.wealthwise.adapters;

import static android.app.ProgressDialog.show;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wealthwise.R;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenses;
    private WealthWiseDatabase database;
    private Context context;
    private ExecutorService executorService;


    public ExpenseAdapter(List<Expense> expenses, WealthWiseDatabase database) {
        this.expenses = expenses;
        this.database = database;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);

        context = parent.getContext();

        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.categoryTextView.setText(expense.getCategory());
        holder.amountTextView.setText(String.valueOf(expense.getAmount()));
        holder.dateTextView.setText(expense.getDate());

        holder.deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(expense, position));
    }

    private void showDeleteConfirmationDialog(Expense expense, int position) {

        new AlertDialog.Builder(context)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteExpense(expense, position);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

    }


    private void deleteExpense(Expense expense, int position) {

        executorService.execute(() -> {

            database.expenseDao().delete(expense);
            expenses.remove(position);


            ((AppCompatActivity) context).runOnUiThread(() -> {

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, expenses.size());

                Toast.makeText(context, "Expense Deleted!", Toast.LENGTH_SHORT).show();

            });


        });

    }


//    private void deleteExpense(Expense expense) {
//        new Thread(() -> database.expenseDao().delete(expense)).start();
//    }

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