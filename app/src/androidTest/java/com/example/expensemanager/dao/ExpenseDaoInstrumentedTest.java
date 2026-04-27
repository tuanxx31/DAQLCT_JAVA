package com.example.expensemanager.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.expensemanager.data.TransactionType;
import com.example.expensemanager.database.AppDatabase;
import com.example.expensemanager.entity.Category;
import com.example.expensemanager.entity.Transaction;
import com.example.expensemanager.model.TransactionWithCategory;
import com.example.expensemanager.utils.DateUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ExpenseDaoInstrumentedTest {
    private AppDatabase database;
    private CategoryDao categoryDao;
    private TransactionDao transactionDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        categoryDao = database.categoryDao();
        transactionDao = database.transactionDao();
    }

    @After
    public void tearDown() throws IOException {
        database.close();
    }

    @Test
    public void categoryCrud_insertsUpdatesAndDeletesUnusedCategory() {
        long now = System.currentTimeMillis();
        long categoryId = categoryDao.insert(new Category("Cafe", TransactionType.EXPENSE, "#DC2626", "coffee", now));

        Category inserted = categoryDao.getByIdSync((int) categoryId);
        assertEquals("Cafe", inserted.getName());
        assertEquals(TransactionType.EXPENSE, inserted.getType());

        inserted.setName("Cafe sáng");
        inserted.setType(TransactionType.INCOME);
        inserted.setColor("#16A34A");
        inserted.setIcon("salary");
        categoryDao.update(inserted);

        Category updated = categoryDao.getByIdSync((int) categoryId);
        assertEquals("Cafe sáng", updated.getName());
        assertEquals(TransactionType.INCOME, updated.getType());
        assertEquals("#16A34A", updated.getColor());
        assertEquals("salary", updated.getIcon());

        categoryDao.delete(updated);
        assertNull(categoryDao.getByIdSync((int) categoryId));
    }

    @Test
    public void transactionCrudAndFilters_workByMonthAndType() {
        long now = System.currentTimeMillis();
        int incomeCategoryId = (int) categoryDao.insert(new Category("Lương", TransactionType.INCOME, "#16A34A", "salary", now));
        int expenseCategoryId = (int) categoryDao.insert(new Category("Ăn uống", TransactionType.EXPENSE, "#DC2626", "food", now));

        int year = 2026;
        int month = Calendar.APRIL;
        long start = DateUtils.startOfMonth(year, month);
        long end = DateUtils.endOfMonth(year, month);
        long inMonthDate = DateUtils.startOfMonth(year, month) + 12 * 60 * 60 * 1000;
        long outsideMonthDate = DateUtils.startOfMonth(year, Calendar.MARCH) + 12 * 60 * 60 * 1000;

        long incomeId = transactionDao.insert(new Transaction(5000000, TransactionType.INCOME, incomeCategoryId, "Lương tháng 4", inMonthDate, now, now));
        long expenseId = transactionDao.insert(new Transaction(120000, TransactionType.EXPENSE, expenseCategoryId, "Ăn trưa", inMonthDate, now, now));
        transactionDao.insert(new Transaction(99000, TransactionType.EXPENSE, expenseCategoryId, "Tháng trước", outsideMonthDate, now, now));

        List<TransactionWithCategory> monthlyTransactions = transactionDao.getByMonthSync(start, end);
        assertEquals(2, monthlyTransactions.size());

        List<TransactionWithCategory> monthlyExpenses = transactionDao.getByTypeAndMonthSync(TransactionType.EXPENSE, start, end);
        assertEquals(1, monthlyExpenses.size());
        assertEquals((int) expenseId, monthlyExpenses.get(0).transaction.getId());
        assertEquals(120000, transactionDao.getTotalByTypeSync(TransactionType.EXPENSE, start, end), 0.001);

        Transaction expense = transactionDao.getByIdSync((int) expenseId);
        expense.setAmount(150000);
        expense.setUpdatedAt(now + 1);
        transactionDao.update(expense);

        assertEquals(150000, transactionDao.getByIdSync((int) expenseId).getAmount(), 0.001);
        assertEquals(150000, transactionDao.getTotalByTypeSync(TransactionType.EXPENSE, start, end), 0.001);

        transactionDao.delete(transactionDao.getByIdSync((int) incomeId));
        assertNull(transactionDao.getByIdSync((int) incomeId));
        assertEquals(1, transactionDao.getByMonthSync(start, end).size());
    }

    @Test
    public void dateValidation_rejectsInvalidDisplayDates() {
        assertNull(DateUtils.parseDisplayDate("2026-04-27"));
        assertNull(DateUtils.parseDisplayDate("31/02/2026"));
    }
}
