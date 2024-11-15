package com.example.wealthwise.models;

import androidx.room.ColumnInfo;

public class CategoryTotal {

    @ColumnInfo(name = "category")
    public String category;


    @ColumnInfo(name = "total")
    public double total;

}
