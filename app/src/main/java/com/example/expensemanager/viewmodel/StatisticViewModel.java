package com.example.expensemanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensemanager.data.TransactionType;
import com.example.expensemanager.model.CategoryTotal;
import com.example.expensemanager.model.DailyTotal;
import com.example.expensemanager.repository.TransactionRepository;

import java.util.List;

public class StatisticViewModel extends AndroidViewModel {
    private final TransactionRepository repository;

    public StatisticViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
    }

    public LiveData<Double> getIncome(long start, long end) {
        return repository.getTotalByType(TransactionType.INCOME, start, end);
    }

    public LiveData<Double> getExpense(long start, long end) {
        return repository.getTotalByType(TransactionType.EXPENSE, start, end);
    }

    public LiveData<List<CategoryTotal>> getExpenseByCategory(long start, long end) {
        return repository.getCategoryTotals(TransactionType.EXPENSE, start, end);
    }

    public LiveData<List<DailyTotal>> getDailyTotals(long start, long end) {
        return repository.getDailyTotals(start, end);
    }
}
