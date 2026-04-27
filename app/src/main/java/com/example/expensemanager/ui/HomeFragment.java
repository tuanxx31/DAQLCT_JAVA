package com.example.expensemanager.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.adapter.TransactionAdapter;
import com.example.expensemanager.model.TransactionWithCategory;
import com.example.expensemanager.utils.BudgetPreferences;
import com.example.expensemanager.utils.DateUtils;
import com.example.expensemanager.utils.MoneyUtils;
import com.example.expensemanager.viewmodel.HomeViewModel;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {
    private TextView balance;
    private TextView income;
    private TextView expense;
    private TextView warning;
    private TextView empty;
    private TransactionAdapter adapter;
    private BudgetPreferences budgetPreferences;
    private double currentExpense;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        HomeViewModel viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        budgetPreferences = new BudgetPreferences(requireContext());

        balance = view.findViewById(R.id.textBalance);
        income = view.findViewById(R.id.textIncome);
        expense = view.findViewById(R.id.textExpense);
        warning = view.findViewById(R.id.textBudgetWarning);
        empty = view.findViewById(R.id.textHomeEmpty);
        ((TextView) view.findViewById(R.id.textMonth)).setText("Tháng " + DateUtils.formatCurrentMonth());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerRecentTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter(new TransactionAdapter.Listener() {
            @Override
            public void onEdit(TransactionWithCategory transaction) {
                ((MainActivity) requireActivity()).openFragment(AddTransactionFragment.newInstance(transaction.transaction.getId()), true);
            }

            @Override
            public void onDelete(TransactionWithCategory transaction) {
            }
        });
        recyclerView.setAdapter(adapter);

        MaterialButton addButton = view.findViewById(R.id.buttonAddTransaction);
        addButton.setOnClickListener(v -> ((MainActivity) requireActivity()).openFragment(new AddTransactionFragment(), true));

        viewModel.getBalance().observe(getViewLifecycleOwner(), value -> balance.setText(MoneyUtils.format(value == null ? 0 : value)));
        viewModel.getIncome().observe(getViewLifecycleOwner(), value -> income.setText(MoneyUtils.format(value == null ? 0 : value)));
        viewModel.getExpense().observe(getViewLifecycleOwner(), value -> {
            currentExpense = value == null ? 0 : value;
            expense.setText(MoneyUtils.format(currentExpense));
            updateBudgetWarning();
        });
        viewModel.getRecentTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.submitList(transactions);
            empty.setVisibility(transactions == null || transactions.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void updateBudgetWarning() {
        double budget = budgetPreferences.getMonthlyBudget();
        boolean show = budgetPreferences.isAlertEnabled() && budget > 0 && currentExpense > budget;
        warning.setVisibility(show ? View.VISIBLE : View.GONE);
        warning.setText("Chi tiêu đã vượt ngân sách tháng: " + MoneyUtils.format(currentExpense) + " / " + MoneyUtils.format(budget));
    }
}
