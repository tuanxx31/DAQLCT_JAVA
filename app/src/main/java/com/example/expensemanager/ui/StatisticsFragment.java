package com.example.expensemanager.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensemanager.R;
import com.example.expensemanager.model.CategoryTotal;
import com.example.expensemanager.utils.DateUtils;
import com.example.expensemanager.utils.MoneyUtils;
import com.example.expensemanager.viewmodel.StatisticViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {
    private double income;
    private double expense;
    private TextView summary;
    private TextView empty;
    private PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        StatisticViewModel viewModel = new ViewModelProvider(this).get(StatisticViewModel.class);
        summary = view.findViewById(R.id.textStatisticSummary);
        empty = view.findViewById(R.id.textStatisticEmpty);
        pieChart = view.findViewById(R.id.pieChart);
        ((TextView) view.findViewById(R.id.textStatisticMonth)).setText("Tháng " + DateUtils.formatCurrentMonth());

        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setCenterText("Chi tiêu");

        viewModel.getCurrentMonthIncome().observe(getViewLifecycleOwner(), value -> {
            income = value == null ? 0 : value;
            updateSummary();
        });
        viewModel.getCurrentMonthExpense().observe(getViewLifecycleOwner(), value -> {
            expense = value == null ? 0 : value;
            updateSummary();
        });
        viewModel.getExpenseByCategory().observe(getViewLifecycleOwner(), this::renderChart);
    }

    private void updateSummary() {
        summary.setText("Thu: " + MoneyUtils.format(income) + "\nChi: " + MoneyUtils.format(expense) + "\nSố dư: " + MoneyUtils.format(income - expense));
    }

    private void renderChart(List<CategoryTotal> totals) {
        boolean hasData = totals != null && !totals.isEmpty();
        empty.setVisibility(hasData ? View.GONE : View.VISIBLE);
        pieChart.setVisibility(hasData ? View.VISIBLE : View.GONE);
        if (!hasData) {
            return;
        }
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        for (CategoryTotal total : totals) {
            entries.add(new PieEntry((float) total.total, total.categoryName));
            try {
                colors.add(Color.parseColor(total.color));
            } catch (IllegalArgumentException exception) {
                colors.add(getResources().getColor(R.color.primary));
            }
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        pieChart.setData(new PieData(dataSet));
        pieChart.invalidate();
    }
}
