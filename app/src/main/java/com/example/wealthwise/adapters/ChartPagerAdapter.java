package com.example.wealthwise.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.wealthwise.fragments.BarChartFragment;
import com.example.wealthwise.fragments.ChartFragment;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.List;

public class ChartPagerAdapter extends FragmentStateAdapter {

    private final List<PieEntry> categoryData;
    private final List<BarEntry> monthlyData;

    public ChartPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<PieEntry> categoryData, List<BarEntry> monthlyData) {
        super(fragmentActivity);
        this.categoryData = categoryData;
        this.monthlyData = monthlyData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return ChartFragment.newInstance("", categoryData); // Pie Chart
        } else {
            return BarChartFragment.newInstance(monthlyData); // Bar Chart
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of charts
    }
}