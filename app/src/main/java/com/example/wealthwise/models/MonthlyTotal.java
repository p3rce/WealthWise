package com.example.wealthwise.models;

import androidx.room.ColumnInfo;

public class MonthlyTotal {
    @ColumnInfo(name = "month")
    public String month;

    @ColumnInfo(name = "total")
    public double total;
}

