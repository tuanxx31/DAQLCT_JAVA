package com.example.expensemanager.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.expensemanager.entity.Category;
import com.example.expensemanager.entity.Transaction;

public class TransactionWithCategory {
    @Embedded
    public Transaction transaction;

    @Relation(parentColumn = "categoryId", entityColumn = "id")
    public Category category;
}
