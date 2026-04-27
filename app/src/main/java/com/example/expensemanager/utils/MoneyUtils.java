package com.example.expensemanager.utils;

import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyUtils {
    private MoneyUtils() {
    }

    public static String format(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(amount);
    }
}
