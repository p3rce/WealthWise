package com.example.wealthwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.example.wealthwise.R;

import java.util.ArrayList;
import java.util.List;

public class BarChartFragment extends Fragment {

    private static final String ARG_BAR_DATA = "bar_data";
    private List<BarEntry> barEntries;

    public static BarChartFragment newInstance(List<BarEntry> barEntries) {
        BarChartFragment fragment = new BarChartFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_BAR_DATA, new ArrayList<>(barEntries));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barEntries = getArguments().getParcelableArrayList(ARG_BAR_DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        BarChart barChart = view.findViewById(R.id.barChart);
        configureBarChart(barChart);

        return view;
    }

    private void configureBarChart(BarChart barChart) {


        BarDataSet barDataSet = new BarDataSet(barEntries, "Monthly Expenses");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(12f);


        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.8f);


        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.animateY(1000);
        barChart.setFitBars(true);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getMonths()));



        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(10f);
        leftAxis.setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        barChart.invalidate();
    }

    private List<String> getMonths() {
        return List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    }
}
