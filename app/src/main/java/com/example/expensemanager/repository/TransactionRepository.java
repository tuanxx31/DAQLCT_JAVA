package com.example.expensemanager.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.expensemanager.dao.TransactionDao;
import com.example.expensemanager.database.AppDatabase;
import com.example.expensemanager.entity.Transaction;
import com.example.expensemanager.model.CategoryTotal;
import com.example.expensemanager.model.DailyTotal;
import com.example.expensemanager.model.TransactionWithCategory;

import java.util.List;

public class TransactionRepository {
    private final TransactionDao transactionDao;

    public TransactionRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        transactionDao = database.transactionDao();
    }

    public LiveData<List<TransactionWithCategory>> getAllWithCategory() {
        return transactionDao.getAllWithCategory();
    }

    public LiveData<List<TransactionWithCategory>> getByMonth(long start, long end) {
        return transactionDao.getByMonth(start, end);
    }

    public LiveData<List<TransactionWithCategory>> getByTypeAndMonth(String type, long start, long end) {
        return transactionDao.getByTypeAndMonth(type, start, end);
    }

    public LiveData<List<TransactionWithCategory>> getRecent(int limit) {
        return transactionDao.getRecent(limit);
    }

    public LiveData<Transaction> getById(int id) {
        return transactionDao.getById(id);
    }

    public LiveData<Double> getTotalByType(String type, long start, long end) {
        return transactionDao.getTotalByType(type, start, end);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotals(String type, long start, long end) {
        return transactionDao.getCategoryTotals(type, start, end);
    }

    public LiveData<List<DailyTotal>> getDailyTotals(long start, long end) {
        return transactionDao.getDailyTotals(start, end);
    }

    public void insert(Transaction transaction) {
        AppDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.insert(transaction);
            }
        });
    }

    public void update(Transaction transaction) {
        AppDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.update(transaction);
            }
        });
    }

    public void delete(Transaction transaction) {
        AppDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.delete(transaction);
            }
        });
    }
}
