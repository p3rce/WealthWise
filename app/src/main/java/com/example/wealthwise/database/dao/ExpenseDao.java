package com.example.wealthwise.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.wealthwise.models.CategoryTotal;
import com.example.wealthwise.models.Expense;
import com.example.wealthwise.models.MonthlyTotal;
import com.example.wealthwise.models.WeeklyTotal;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    void insert(Expense expense);


    @Query("SELECT * FROM expenses")
    List<Expense> getAllExpenses();


    @Delete
    void delete(Expense expense);


    // Get the total amount spent per category for the current month
    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now') GROUP BY category")
    List<CategoryTotal> getCategoryWiseTotal();

    // Get the total amount spent per week for the current month
    @Query("SELECT strftime('%W', date) as week, SUM(amount) as total FROM expenses WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now') GROUP BY week")
    List<WeeklyTotal> getWeeklyTotal();

    // Get the total amount spent per month for the current year
    @Query("SELECT strftime('%m', date) as month, SUM(amount) as total FROM expenses WHERE strftime('%Y', date) = strftime('%Y', 'now') GROUP BY month")
    List<MonthlyTotal> getMonthlyTotal();

}
