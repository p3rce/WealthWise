package com.example.wealthwise;

/*
@author: Pierce Goulimis
@desc: ExpenseDao - This Data Access Object (DAO) interface will handle database operations for Expense
 */

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.wealthwise.Expense;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insert(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    List<Expense> getAllExpenses();
}
