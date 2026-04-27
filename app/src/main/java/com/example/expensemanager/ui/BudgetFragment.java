package com.example.expensemanager.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensemanager.R;
import com.example.expensemanager.utils.BudgetPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class BudgetFragment extends Fragment {
    private BudgetPreferences budgetPreferences;
    private TextInputLayout budgetLayout;
    private TextInputEditText budgetInput;
    private Switch alertSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        budgetPreferences = new BudgetPreferences(requireContext());
        budgetLayout = view.findViewById(R.id.inputBudgetLayout);
        budgetInput = view.findViewById(R.id.inputBudget);
        alertSwitch = view.findViewById(R.id.switchAlert);
        MaterialButton save = view.findViewById(R.id.buttonSaveBudget);

        double currentBudget = budgetPreferences.getMonthlyBudget();
        if (currentBudget > 0) {
            budgetInput.setText(String.valueOf(currentBudget));
        }
        alertSwitch.setChecked(budgetPreferences.isAlertEnabled());
        save.setOnClickListener(v -> saveBudget());
    }

    private void saveBudget() {
        budgetLayout.setError(null);
        String text = budgetInput.getText() == null ? "" : budgetInput.getText().toString().trim();
        if (text.isEmpty()) {
            budgetLayout.setError("Vui lòng nhập ngân sách");
            return;
        }
        try {
            double budget = Double.parseDouble(text);
            if (budget < 0) {
                budgetLayout.setError("Ngân sách không được âm");
                return;
            }
            budgetPreferences.save(budget, alertSwitch.isChecked());
            Toast.makeText(requireContext(), "Đã lưu ngân sách", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException exception) {
            budgetLayout.setError("Ngân sách không hợp lệ");
        }
    }
}
