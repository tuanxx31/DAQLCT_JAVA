package com.example.expensemanager.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.adapter.TransactionAdapter;
import com.example.expensemanager.data.TransactionType;
import com.example.expensemanager.model.TransactionWithCategory;
import com.example.expensemanager.viewmodel.TransactionViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TransactionsFragment extends Fragment {
    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;
    private TextView empty;
    private LiveData<List<TransactionWithCategory>> activeSource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        empty = view.findViewById(R.id.textEmpty);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter(new TransactionAdapter.Listener() {
            @Override
            public void onEdit(TransactionWithCategory transaction) {
                ((MainActivity) requireActivity()).openFragment(AddTransactionFragment.newInstance(transaction.transaction.getId()), true);
            }

            @Override
            public void onDelete(TransactionWithCategory transaction) {
                confirmDelete(transaction);
            }
        });
        recyclerView.setAdapter(adapter);

        MaterialButton add = view.findViewById(R.id.buttonAdd);
        add.setOnClickListener(v -> ((MainActivity) requireActivity()).openFragment(new AddTransactionFragment(), true));

        RadioGroup filter = view.findViewById(R.id.filterTypeGroup);
        filter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioIncome) {
                observeSource(viewModel.getCurrentMonthByType(TransactionType.INCOME));
            } else if (checkedId == R.id.radioExpense) {
                observeSource(viewModel.getCurrentMonthByType(TransactionType.EXPENSE));
            } else {
                observeSource(viewModel.getCurrentMonth());
            }
        });
        observeSource(viewModel.getCurrentMonth());
    }

    private void observeSource(LiveData<List<TransactionWithCategory>> source) {
        if (activeSource != null) {
            activeSource.removeObservers(getViewLifecycleOwner());
        }
        activeSource = source;
        activeSource.observe(getViewLifecycleOwner(), transactions -> {
            adapter.submitList(transactions);
            empty.setVisibility(transactions == null || transactions.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void confirmDelete(TransactionWithCategory item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa giao dịch")
                .setMessage("Bạn có chắc muốn xóa giao dịch này?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> viewModel.delete(item.transaction))
                .show();
    }
}
