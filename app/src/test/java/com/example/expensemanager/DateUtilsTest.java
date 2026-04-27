package com.example.expensemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.expensemanager.utils.DateUtils;

import org.junit.Test;

public class DateUtilsTest {
    @Test
    public void parseDisplayDate_acceptsVietnameseDateFormat() {
        assertNotNull(DateUtils.parseDisplayDate("27/04/2026"));
    }

    @Test
    public void parseDisplayDate_rejectsInvalidInput() {
        assertNull(DateUtils.parseDisplayDate("2026-04-27"));
    }
}
