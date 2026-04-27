package com.example.expensemanager.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.expensemanager.model.CategoryTotal;
import com.example.expensemanager.model.DailyTotal;
import com.example.expensemanager.model.TransactionWithCategory;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    long insert(com.example.expensemanager.entity.Transaction transaction);

    @Update
    void update(com.example.expensemanager.entity.Transaction transaction);

    @Delete
    void delete(com.example.expensemanager.entity.Transaction transaction);

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    LiveData<List<TransactionWithCategory>> getAllWithCategory();

    @Transaction
    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC, id DESC")
    LiveData<List<TransactionWithCategory>> getByMonth(long start, long end);

    @Transaction
    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC, id DESC")
    List<TransactionWithCategory> getByMonthSync(long start, long end);

    @Transaction
    @Query("SELECT * FROM transactions WHERE type = :type AND date BETWEEN :start AND :end ORDER BY date DESC, id DESC")
    LiveData<List<TransactionWithCategory>> getByTypeAndMonth(String type, long start, long end);

    @Transaction
    @Query("SELECT * FROM transactions WHERE type = :type AND date BETWEEN :start AND :end ORDER BY date DESC, id DESC")
    List<TransactionWithCategory> getByTypeAndMonthSync(String type, long start, long end);

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC LIMIT :limit")
    LiveData<List<TransactionWithCategory>> getRecent(int limit);

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    LiveData<com.example.expensemanager.entity.Transaction> getById(int id);

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    com.example.expensemanager.entity.Transaction getByIdSync(int id);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = :type AND date BETWEEN :start AND :end")
    LiveData<Double> getTotalByType(String type, long start, long end);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = :type AND date BETWEEN :start AND :end")
    double getTotalByTypeSync(String type, long start, long end);

    @Query("SELECT c.name AS categoryName, c.color AS color, COALESCE(SUM(t.amount), 0) AS total FROM transactions t INNER JOIN categories c ON c.id = t.categoryId WHERE t.type = :type AND t.date BETWEEN :start AND :end GROUP BY c.id, c.name, c.color ORDER BY total DESC")
    LiveData<List<CategoryTotal>> getCategoryTotals(String type, long start, long end);

    @Query("SELECT strftime('%d/%m', t.date / 1000, 'unixepoch', 'localtime') AS dayLabel, " +
            "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS income, " +
            "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS expense " +
            "FROM transactions t WHERE t.date BETWEEN :start AND :end " +
            "GROUP BY strftime('%Y-%m-%d', t.date / 1000, 'unixepoch', 'localtime') " +
            "ORDER BY t.date ASC")
    LiveData<List<DailyTotal>> getDailyTotals(long start, long end);
}
