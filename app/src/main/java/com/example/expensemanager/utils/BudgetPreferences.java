package com.example.expensemanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class BudgetPreferences {
    private static final String PREF_NAME = "budget_preferences";
    private static final String KEY_BUDGET = "monthly_budget";
    private static final String KEY_ALERT_ENABLED = "alert_enabled";

    private final SharedPreferences preferences;

    public BudgetPreferences(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public double getMonthlyBudget() {
        return Double.longBitsToDouble(preferences.getLong(KEY_BUDGET, Double.doubleToLongBits(0)));
    }

    public boolean isAlertEnabled() {
        return preferences.getBoolean(KEY_ALERT_ENABLED, true);
    }

    public void save(double budget, boolean alertEnabled) {
        preferences.edit()
                .putLong(KEY_BUDGET, Double.doubleToLongBits(budget))
                .putBoolean(KEY_ALERT_ENABLED, alertEnabled)
                .apply();
    }
}
