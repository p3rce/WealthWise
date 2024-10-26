package com.example.wealthwise;

/*
@author: Pierce Goulimis
@desc: WealthWiseDatabase - To store expenses, we'll set up a Room database
 */


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import com.example.wealthwise.Expense;


@Database(entities = {Expense.class}, version = 1)
public abstract class WealthWiseDatabase extends RoomDatabase {

    //MARK: - Properties
    private static WealthWiseDatabase instance;
    public abstract ExpenseDao expenseDao();


    public static synchronized WealthWiseDatabase getInstance(Context context) {

        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    WealthWiseDatabase.class, "wealthwise_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;

    }

}
