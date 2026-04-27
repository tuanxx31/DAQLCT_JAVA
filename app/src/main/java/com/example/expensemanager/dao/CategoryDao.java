package com.example.expensemanager.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expensemanager.entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories ORDER BY type, name")
    LiveData<List<Category>> getAll();

    @Query("SELECT * FROM categories ORDER BY type, name")
    List<Category> getAllSync();

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name")
    LiveData<List<Category>> getByType(String type);

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name")
    List<Category> getByTypeSync(String type);

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    LiveData<Category> getById(int id);

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    Category getByIdSync(int id);

    @Query("SELECT COUNT(*) FROM transactions WHERE categoryId = :categoryId")
    int countTransactions(int categoryId);
}
