package com.example.expensemanager.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {
    private static final SimpleDateFormat DISPLAY_DATE = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_MONTH = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

    static {
        DISPLAY_DATE.setLenient(false);
        DISPLAY_MONTH.setLenient(false);
    }

    private DateUtils() {
    }

    public static long startOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long endOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public static String formatDate(long time) {
        return DISPLAY_DATE.format(new Date(time));
    }

    public static Long parseDisplayDate(String date) {
        try {
            Date parsed = DISPLAY_DATE.parse(date);
            return parsed == null ? null : parsed.getTime();
        } catch (Exception exception) {
            return null;
        }
    }

    public static String formatCurrentMonth() {
        return DISPLAY_MONTH.format(new Date());
    }
}
