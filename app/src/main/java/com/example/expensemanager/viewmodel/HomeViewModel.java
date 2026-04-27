package com.example.expensemanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.expensemanager.data.TransactionType;
import com.example.expensemanager.model.TransactionWithCategory;
import com.example.expensemanager.repository.TransactionRepository;
import com.example.expensemanager.utils.DateUtils;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final TransactionRepository repository;
    private final LiveData<Double> income;
    private final LiveData<Double> expense;
    private final MediatorLiveData<Double> balance = new MediatorLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        long start = DateUtils.startOfCurrentMonth();
        long end = DateUtils.endOfCurrentMonth();
        income = repository.getTotalByType(TransactionType.INCOME, start, end);
        expense = repository.getTotalByType(TransactionType.EXPENSE, start, end);
        balance.addSource(income, value -> updateBalance());
        balance.addSource(expense, value -> updateBalance());
    }

    private void updateBalance() {
        Double incomeValue = income.getValue();
        Double expenseValue = expense.getValue();
        balance.setValue((incomeValue == null ? 0 : incomeValue) - (expenseValue == null ? 0 : expenseValue));
    }

    public LiveData<Double> getIncome() {
        return income;
    }

    public LiveData<Double> getExpense() {
        return expense;
    }

    public LiveData<Double> getBalance() {
        return balance;
    }

    public LiveData<List<TransactionWithCategory>> getRecentTransactions() {
        return repository.getRecent(5);
    }
}
