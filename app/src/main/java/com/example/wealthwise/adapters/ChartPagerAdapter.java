package com.example.wealthwise.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.wealthwise.activities.ChartFragment;
import com.github.mikephil.charting.data.PieEntry;

import java.util.List;

public class ChartPagerAdapter extends FragmentStateAdapter {

    private final List<PieEntry> categoryData;
    private final List<PieEntry> weeklyData;
    private final List<PieEntry> monthlyData;

    public ChartPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<PieEntry> categoryData, List<PieEntry> weeklyData, List<PieEntry> monthlyData) {
        super(fragmentActivity);
        this.categoryData = categoryData;
        this.weeklyData = weeklyData;
        this.monthlyData = monthlyData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ChartFragment.newInstance("Category-wise Expenses", categoryData);
            case 1:
                return ChartFragment.newInstance("Weekly Expenses", weeklyData);
            case 2:
                return ChartFragment.newInstance("Monthly Overview", monthlyData);
            default:
                return ChartFragment.newInstance("Chart", categoryData);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of charts
    }
}