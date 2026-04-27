package com.example.expensemanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensemanager.entity.Transaction;
import com.example.expensemanager.model.TransactionWithCategory;
import com.example.expensemanager.repository.TransactionRepository;
import com.example.expensemanager.utils.DateUtils;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private final TransactionRepository repository;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
    }

    public LiveData<List<TransactionWithCategory>> getAll() {
        return repository.getAllWithCategory();
    }

    public LiveData<List<TransactionWithCategory>> getCurrentMonth() {
        return repository.getByMonth(DateUtils.startOfCurrentMonth(), DateUtils.endOfCurrentMonth());
    }

    public LiveData<List<TransactionWithCategory>> getCurrentMonthByType(String type) {
        return repository.getByTypeAndMonth(type, DateUtils.startOfCurrentMonth(), DateUtils.endOfCurrentMonth());
    }

    public LiveData<List<TransactionWithCategory>> getRecent(int limit) {
        return repository.getRecent(limit);
    }

    public LiveData<Transaction> getById(int id) {
        return repository.getById(id);
    }

    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }

    public void update(Transaction transaction) {
        repository.update(transaction);
    }

    public void delete(Transaction transaction) {
        repository.delete(transaction);
    }
}
