package com.example.expensemanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensemanager.data.TransactionType;
import com.example.expensemanager.model.CategoryTotal;
import com.example.expensemanager.repository.TransactionRepository;
import com.example.expensemanager.utils.DateUtils;

import java.util.List;

public class StatisticViewModel extends AndroidViewModel {
    private final TransactionRepository repository;

    public StatisticViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
    }

    public LiveData<Double> getCurrentMonthIncome() {
        return repository.getTotalByType(TransactionType.INCOME, DateUtils.startOfCurrentMonth(), DateUtils.endOfCurrentMonth());
    }

    public LiveData<Double> getCurrentMonthExpense() {
        return repository.getTotalByType(TransactionType.EXPENSE, DateUtils.startOfCurrentMonth(), DateUtils.endOfCurrentMonth());
    }

    public LiveData<List<CategoryTotal>> getExpenseByCategory() {
        return repository.getCategoryTotals(TransactionType.EXPENSE, DateUtils.startOfCurrentMonth(), DateUtils.endOfCurrentMonth());
    }
}
