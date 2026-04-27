package com.example.expensemanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensemanager.entity.Category;
import com.example.expensemanager.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository repository;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
    }

    public LiveData<List<Category>> getAll() {
        return repository.getAll();
    }

    public LiveData<List<Category>> getByType(String type) {
        return repository.getByType(type);
    }

    public void insert(Category category) {
        repository.insert(category);
    }

    public void update(Category category) {
        repository.update(category);
    }

    public void deleteIfUnused(Category category, CategoryRepository.DeleteCallback callback) {
        repository.deleteIfUnused(category, callback);
    }
}
