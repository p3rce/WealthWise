package com.example.wealthwise.models;

import androidx.room.ColumnInfo;

public class WeeklyTotal {
    @ColumnInfo(name = "week")
    public String week;

    @ColumnInfo(name = "total")
    public double total;
}
