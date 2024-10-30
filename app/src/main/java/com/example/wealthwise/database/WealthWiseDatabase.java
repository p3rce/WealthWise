package com.example.wealthwise.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.wealthwise.database.dao.ExpenseDao;
import com.example.wealthwise.models.Expense;

@Database(entities = {Expense.class}, version = 1)
public abstract class WealthWiseDatabase extends RoomDatabase {

    public abstract ExpenseDao expenseDao();

    private static volatile WealthWiseDatabase INSTANCE;

    public static WealthWiseDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (WealthWiseDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WealthWiseDatabase.class, "wealthwise_database")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

}
