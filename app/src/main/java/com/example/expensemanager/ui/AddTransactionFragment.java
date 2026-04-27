package com.example.expensemanager.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensemanager.R;
import com.example.expensemanager.data.TransactionType;
import com.example.expensemanager.entity.Category;
import com.example.expensemanager.entity.Transaction;
import com.example.expensemanager.viewmodel.CategoryViewModel;
import com.example.expensemanager.viewmodel.TransactionViewModel;
import com.example.expensemanager.utils.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AddTransactionFragment extends Fragment {
    private static final String ARG_TRANSACTION_ID = "transaction_id";

    private CategoryViewModel categoryViewModel;
    private TransactionViewModel transactionViewModel;
    private TextInputLayout amountLayout;
    private TextInputLayout dateLayout;
    private TextInputEditText amountInput;
    private TextInputEditText dateInput;
    private TextInputEditText noteInput;
    private Spinner categorySpinner;
    private RadioGroup typeGroup;
    private String selectedType = TransactionType.EXPENSE;
    private final List<Category> categories = new ArrayList<>();
    private LiveData<List<Category>> activeCategorySource;
    private Transaction editingTransaction;

    public static AddTransactionFragment newInstance(int transactionId) {
        AddTransactionFragment fragment = new AddTransactionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TRANSACTION_ID, transactionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        amountLayout = view.findViewById(R.id.inputAmountLayout);
        dateLayout = view.findViewById(R.id.inputDateLayout);
        amountInput = view.findViewById(R.id.inputAmount);
        dateInput = view.findViewById(R.id.inputDate);
        noteInput = view.findViewById(R.id.inputNote);
        categorySpinner = view.findViewById(R.id.spinnerCategory);
        typeGroup = view.findViewById(R.id.typeGroup);
        MaterialButton save = view.findViewById(R.id.buttonSave);

        int transactionId = getArguments() == null ? 0 : getArguments().getInt(ARG_TRANSACTION_ID, 0);
        if (transactionId > 0) {
            ((TextView) view.findViewById(R.id.textFormTitle)).setText("Sửa giao dịch");
            transactionViewModel.getById(transactionId).observe(getViewLifecycleOwner(), transaction -> {
                if (transaction != null && editingTransaction == null) {
                    editingTransaction = transaction;
                    selectedType = transaction.getType();
                    typeGroup.check(TransactionType.INCOME.equals(selectedType) ? R.id.radioIncome : R.id.radioExpense);
                    amountInput.setText(String.valueOf(transaction.getAmount()));
                    dateInput.setText(DateUtils.formatDate(transaction.getDate()));
                    noteInput.setText(transaction.getNote());
                    observeCategories(selectedType);
                }
            });
        }

        typeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedType = checkedId == R.id.radioIncome ? TransactionType.INCOME : TransactionType.EXPENSE;
            observeCategories(selectedType);
        });
        observeCategories(selectedType);
        dateInput.setText(DateUtils.formatDate(System.currentTimeMillis()));

        save.setOnClickListener(v -> saveTransaction());
    }

    private void observeCategories(String type) {
        if (activeCategorySource != null) {
            activeCategorySource.removeObservers(getViewLifecycleOwner());
        }
        activeCategorySource = categoryViewModel.getByType(type);
        activeCategorySource.observe(getViewLifecycleOwner(), values -> {
            categories.clear();
            if (values != null) {
                categories.addAll(values);
            }
            List<String> names = new ArrayList<>();
            for (Category category : categories) {
                names.add(category.getName());
            }
            categorySpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, names));
            if (editingTransaction != null) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getId() == editingTransaction.getCategoryId()) {
                        categorySpinner.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    private void saveTransaction() {
        amountLayout.setError(null);
        dateLayout.setError(null);
        String amountText = amountInput.getText() == null ? "" : amountInput.getText().toString().trim();
        if (amountText.isEmpty()) {
            amountLayout.setError("Vui lòng nhập số tiền");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException exception) {
            amountLayout.setError("Số tiền không hợp lệ");
            return;
        }
        if (amount <= 0) {
            amountLayout.setError("Số tiền phải lớn hơn 0");
            return;
        }
        if (categories.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng tạo danh mục trước", Toast.LENGTH_SHORT).show();
            return;
        }
        String dateText = dateInput.getText() == null ? "" : dateInput.getText().toString().trim();
        Long selectedDate = DateUtils.parseDisplayDate(dateText);
        if (selectedDate == null) {
            dateLayout.setError("Ngày phải đúng định dạng dd/MM/yyyy");
            return;
        }

        Category category = categories.get(categorySpinner.getSelectedItemPosition());
        String note = noteInput.getText() == null ? "" : noteInput.getText().toString().trim();
        long now = System.currentTimeMillis();
        if (editingTransaction == null) {
            transactionViewModel.insert(new Transaction(amount, selectedType, category.getId(), note, selectedDate, now, now));
            Toast.makeText(requireContext(), "Đã thêm giao dịch", Toast.LENGTH_SHORT).show();
        } else {
            editingTransaction.setAmount(amount);
            editingTransaction.setType(selectedType);
            editingTransaction.setCategoryId(category.getId());
            editingTransaction.setNote(note);
            editingTransaction.setDate(selectedDate);
            editingTransaction.setUpdatedAt(now);
            transactionViewModel.update(editingTransaction);
            Toast.makeText(requireContext(), "Đã cập nhật giao dịch", Toast.LENGTH_SHORT).show();
        }
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
