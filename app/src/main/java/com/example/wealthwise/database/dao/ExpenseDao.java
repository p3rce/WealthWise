package com.example.wealthwise.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.wealthwise.models.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    void insert(Expense expense);


    @Query("SELECT * FROM expenses")
    List<Expense> getAllExpenses();


    @Delete
    void delete(Expense expense);


    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    List<Expense> getExpensesByMonth(String startDate, String endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category AND date BETWEEN :startDate AND :endDate")
    double getTotalByCategory(String category, String startDate, String endDate);

}
