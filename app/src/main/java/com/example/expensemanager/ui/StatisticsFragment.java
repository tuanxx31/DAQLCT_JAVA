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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensemanager.R;
import com.example.expensemanager.model.CategoryTotal;
import com.example.expensemanager.model.DailyTotal;
import com.example.expensemanager.utils.DateUtils;
import com.example.expensemanager.utils.MoneyUtils;
import com.example.expensemanager.viewmodel.StatisticViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsFragment extends Fragment {
    private double income;
    private double expense;
    private TextView summary;
    private TextView monthLabel;
    private TextView empty;
    private PieChart pieChart;
    private BarChart barChart;
    private StatisticViewModel viewModel;
    private final Calendar selectedMonth = Calendar.getInstance();
    private LiveData<Double> incomeSource;
    private LiveData<Double> expenseSource;
    private LiveData<List<CategoryTotal>> categorySource;
    private LiveData<List<DailyTotal>> dailySource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(StatisticViewModel.class);
        summary = view.findViewById(R.id.textStatisticSummary);
        monthLabel = view.findViewById(R.id.textStatisticMonth);
        empty = view.findViewById(R.id.textStatisticEmpty);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);

        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setCenterText("Chi tiêu");

        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getLegend().setWordWrapEnabled(true);

        view.findViewById(R.id.buttonPreviousMonth).setOnClickListener(v -> {
            selectedMonth.add(Calendar.MONTH, -1);
            observeSelectedMonth();
        });
        view.findViewById(R.id.buttonNextMonth).setOnClickListener(v -> {
            selectedMonth.add(Calendar.MONTH, 1);
            observeSelectedMonth();
        });
        observeSelectedMonth();
    }

    private void observeSelectedMonth() {
        removeOldObservers();
        int year = selectedMonth.get(Calendar.YEAR);
        int month = selectedMonth.get(Calendar.MONTH);
        long start = DateUtils.startOfMonth(year, month);
        long end = DateUtils.endOfMonth(year, month);
        monthLabel.setText("Tháng " + DateUtils.formatMonth(year, month));

        incomeSource = viewModel.getIncome(start, end);
        expenseSource = viewModel.getExpense(start, end);
        categorySource = viewModel.getExpenseByCategory(start, end);
        dailySource = viewModel.getDailyTotals(start, end);

        incomeSource.observe(getViewLifecycleOwner(), value -> {
            income = value == null ? 0 : value;
            updateSummary();
        });
        expenseSource.observe(getViewLifecycleOwner(), value -> {
            expense = value == null ? 0 : value;
            updateSummary();
        });
        categorySource.observe(getViewLifecycleOwner(), this::renderPieChart);
        dailySource.observe(getViewLifecycleOwner(), this::renderBarChart);
    }

    private void removeOldObservers() {
        if (incomeSource != null) {
            incomeSource.removeObservers(getViewLifecycleOwner());
        }
        if (expenseSource != null) {
            expenseSource.removeObservers(getViewLifecycleOwner());
        }
        if (categorySource != null) {
            categorySource.removeObservers(getViewLifecycleOwner());
        }
        if (dailySource != null) {
            dailySource.removeObservers(getViewLifecycleOwner());
        }
        income = 0;
        expense = 0;
        updateSummary();
    }

    private void updateSummary() {
        summary.setText("Thu: " + MoneyUtils.format(income) + "\nChi: " + MoneyUtils.format(expense) + "\nSố dư: " + MoneyUtils.format(income - expense));
    }

    private void renderPieChart(List<CategoryTotal> totals) {
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

    private void renderBarChart(List<DailyTotal> totals) {
        boolean hasData = totals != null && !totals.isEmpty();
        barChart.setVisibility(hasData ? View.VISIBLE : View.GONE);
        if (!hasData) {
            barChart.clear();
            return;
        }

        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < totals.size(); i++) {
            DailyTotal total = totals.get(i);
            incomeEntries.add(new BarEntry(i, (float) total.income));
            expenseEntries.add(new BarEntry(i, (float) total.expense));
            labels.add(total.dayLabel);
        }

        BarDataSet incomeSet = new BarDataSet(incomeEntries, "Thu");
        incomeSet.setColor(getResources().getColor(R.color.income));
        BarDataSet expenseSet = new BarDataSet(expenseEntries, "Chi");
        expenseSet.setColor(getResources().getColor(R.color.expense));

        BarData data = new BarData(incomeSet, expenseSet);
        float groupSpace = 0.24f;
        float barSpace = 0.04f;
        float barWidth = 0.34f;
        data.setBarWidth(barWidth);
        barChart.setData(data);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setAxisMinimum(0f);
        barChart.getXAxis().setAxisMaximum(0f + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * labels.size());
        barChart.groupBars(0f, groupSpace, barSpace);
        barChart.invalidate();
    }
}
