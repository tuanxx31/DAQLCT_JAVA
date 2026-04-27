package com.example.expensemanager.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.expensemanager.dao.CategoryDao;
import com.example.expensemanager.database.AppDatabase;
import com.example.expensemanager.entity.Category;

import java.util.List;

public class CategoryRepository {
    private final CategoryDao categoryDao;

    public CategoryRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        categoryDao = database.categoryDao();
    }

    public LiveData<List<Category>> getAll() {
        return categoryDao.getAll();
    }

    public LiveData<List<Category>> getByType(String type) {
        return categoryDao.getByType(type);
    }

    public void insert(Category category) {
        AppDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.insert(category);
            }
        });
    }

    public void update(Category category) {
        AppDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.update(category);
            }
        });
    }

    public void deleteIfUnused(Category category, DeleteCallback callback) {
        AppDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int count = categoryDao.countTransactions(category.getId());
                if (count == 0) {
                    categoryDao.delete(category);
                    callback.onResult(true);
                } else {
                    callback.onResult(false);
                }
            }
        });
    }

    public interface DeleteCallback {
        void onResult(boolean deleted);
    }
}
