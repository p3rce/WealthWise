package com.example.wealthwise.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wealthwise.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_ENTRIES = "entries";

    private String title;
    private ArrayList<PieEntry> entries;

    public static ChartFragment newInstance(String title, List<PieEntry> entries) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putParcelableArrayList(ARG_ENTRIES, new ArrayList<>(entries));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            entries = getArguments().getParcelableArrayList(ARG_ENTRIES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        TextView chartTitle = view.findViewById(R.id.chartTitle);
        PieChart pieChart = view.findViewById(R.id.chart);

        chartTitle.setText(title);
        configurePieChart(pieChart); // Custom method to configure the chart

        loadChartData(pieChart);

        return view;
    }

    private void configurePieChart(PieChart pieChart) {
        // Disable percentage values
        pieChart.setUsePercentValues(false);

        // Disable description text on the chart
        pieChart.getDescription().setEnabled(false);

        // Remove hole in the center of the pie chart
        pieChart.setDrawHoleEnabled(false);

        // Set entry label text size and color
        pieChart.setEntryLabelTextSize(14f);
        pieChart.setEntryLabelColor(getResources().getColor(android.R.color.white));

        // Customize the legend (optional: disable it if desired)
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(false);  // Disable if you don’t want the legend

        // Add animations
        pieChart.animateY(1000);
    }


    private void loadChartData(PieChart pieChart) {

        PieDataSet dataSet = new PieDataSet(entries, title);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(getResources().getColor(android.R.color.white));


        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();

    }


}
