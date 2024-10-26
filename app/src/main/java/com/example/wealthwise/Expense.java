package com.example.wealthwise;

/*
@author: Pierce Goulimis
@desc: Expense Model Class - This class will represent individual expense entries
 */


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;




@Entity(tableName = "expenses")
public class Expense {

    //MARK: - Properties
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String category;
    private double amount;
    private Date date;


    //MARK: - Class Setup
    public Expense(String category, double amount, Date date) {
        this.category = category;
        this.amount = amount;
        this.date = date;
    }


    //MARK: - Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
