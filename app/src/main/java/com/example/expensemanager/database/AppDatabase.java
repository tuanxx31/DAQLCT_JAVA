package com.example.expensemanager.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.expensemanager.dao.CategoryDao;
import com.example.expensemanager.dao.TransactionDao;
import com.example.expensemanager.data.TransactionType;
import com.example.expensemanager.entity.Category;
import com.example.expensemanager.entity.Transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Category.class, Transaction.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(4);

    public abstract CategoryDao categoryDao();

    public abstract TransactionDao transactionDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "expense_manager.db"
                            )
                            .addCallback(seedCallback)
                            .build();
                }
            }
        }
        return instance;
    }

    private static final RoomDatabase.Callback seedCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (instance == null) {
                        return;
                    }
                    CategoryDao dao = instance.categoryDao();
                    long now = System.currentTimeMillis();
                    dao.insert(new Category("Lương", TransactionType.INCOME, "#16A34A", "salary", now));
                    dao.insert(new Category("Thưởng", TransactionType.INCOME, "#22C55E", "bonus", now));
                    dao.insert(new Category("Ăn uống", TransactionType.EXPENSE, "#DC2626", "food", now));
                    dao.insert(new Category("Di chuyển", TransactionType.EXPENSE, "#F97316", "transport", now));
                    dao.insert(new Category("Mua sắm", TransactionType.EXPENSE, "#7C3AED", "shopping", now));
                    dao.insert(new Category("Hóa đơn", TransactionType.EXPENSE, "#2563EB", "bill", now));
                }
            });
        }
    };
}
