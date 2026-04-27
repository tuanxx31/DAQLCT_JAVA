package com.example.expensemanager.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.expensemanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                openFragment(new HomeFragment(), false);
                return true;
            } else if (id == R.id.nav_transactions) {
                openFragment(new TransactionsFragment(), false);
                return true;
            } else if (id == R.id.nav_statistics) {
                openFragment(new StatisticsFragment(), false);
                return true;
            } else if (id == R.id.nav_categories) {
                openFragment(new CategoriesFragment(), false);
                return true;
            } else if (id == R.id.nav_budget) {
                openFragment(new BudgetFragment(), false);
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    public void openFragment(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
