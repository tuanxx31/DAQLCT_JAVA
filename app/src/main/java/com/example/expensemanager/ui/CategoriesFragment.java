package com.example.expensemanager.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.adapter.CategoryAdapter;
import com.example.expensemanager.data.TransactionType;
import com.example.expensemanager.entity.Category;
import com.example.expensemanager.viewmodel.CategoryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CategoriesFragment extends Fragment {
    private CategoryViewModel viewModel;
    private TextInputLayout nameLayout;
    private TextInputEditText nameInput;
    private RadioGroup typeGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        nameLayout = view.findViewById(R.id.inputCategoryNameLayout);
        nameInput = view.findViewById(R.id.inputCategoryName);
        typeGroup = view.findViewById(R.id.categoryTypeGroup);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        CategoryAdapter adapter = new CategoryAdapter(this::confirmDelete);
        recyclerView.setAdapter(adapter);
        viewModel.getAll().observe(getViewLifecycleOwner(), adapter::submitList);

        MaterialButton add = view.findViewById(R.id.buttonAddCategory);
        add.setOnClickListener(v -> addCategory());
    }

    private void addCategory() {
        nameLayout.setError(null);
        String name = nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
        if (name.isEmpty()) {
            nameLayout.setError("Vui lòng nhập tên danh mục");
            return;
        }
        String type = typeGroup.getCheckedRadioButtonId() == R.id.radioIncomeCategory
                ? TransactionType.INCOME
                : TransactionType.EXPENSE;
        String color = TransactionType.INCOME.equals(type) ? "#16A34A" : "#DC2626";
        viewModel.insert(new Category(name, type, color, "custom", System.currentTimeMillis()));
        nameInput.setText("");
        Toast.makeText(requireContext(), "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
    }

    private void confirmDelete(Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa danh mục")
                .setMessage("Chỉ xóa được danh mục chưa có giao dịch liên kết.")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> viewModel.deleteIfUnused(category, deleted ->
                        requireActivity().runOnUiThread(() -> Toast.makeText(
                                requireContext(),
                                deleted ? "Đã xóa danh mục" : "Danh mục đang được dùng",
                                Toast.LENGTH_SHORT
                        ).show())))
                .show();
    }
}
