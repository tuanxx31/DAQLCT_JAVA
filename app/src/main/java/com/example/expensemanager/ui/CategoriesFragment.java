package com.example.expensemanager.ui;

import android.app.AlertDialog;
import android.graphics.Color;
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
    private TextInputLayout colorLayout;
    private TextInputEditText nameInput;
    private TextInputEditText colorInput;
    private TextInputEditText iconInput;
    private RadioGroup typeGroup;
    private MaterialButton saveButton;
    private MaterialButton cancelEditButton;
    private Category editingCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        nameLayout = view.findViewById(R.id.inputCategoryNameLayout);
        colorLayout = view.findViewById(R.id.inputCategoryColorLayout);
        nameInput = view.findViewById(R.id.inputCategoryName);
        colorInput = view.findViewById(R.id.inputCategoryColor);
        iconInput = view.findViewById(R.id.inputCategoryIcon);
        typeGroup = view.findViewById(R.id.categoryTypeGroup);
        saveButton = view.findViewById(R.id.buttonAddCategory);
        cancelEditButton = view.findViewById(R.id.buttonCancelCategoryEdit);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        CategoryAdapter adapter = new CategoryAdapter(new CategoryAdapter.Listener() {
            @Override
            public void onEdit(Category category) {
                startEdit(category);
            }

            @Override
            public void onDelete(Category category) {
                confirmDelete(category);
            }
        });
        recyclerView.setAdapter(adapter);
        viewModel.getAll().observe(getViewLifecycleOwner(), adapter::submitList);

        saveButton.setOnClickListener(v -> saveCategory());
        cancelEditButton.setOnClickListener(v -> clearForm());
    }

    private void saveCategory() {
        nameLayout.setError(null);
        colorLayout.setError(null);
        String name = nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
        if (name.isEmpty()) {
            nameLayout.setError("Vui lòng nhập tên danh mục");
            return;
        }
        String color = colorInput.getText() == null ? "" : colorInput.getText().toString().trim();
        if (!isValidColor(color)) {
            colorLayout.setError("Màu phải có dạng #RRGGBB");
            return;
        }
        String icon = iconInput.getText() == null ? "" : iconInput.getText().toString().trim();
        if (icon.isEmpty()) {
            icon = "custom";
        }
        String type = typeGroup.getCheckedRadioButtonId() == R.id.radioIncomeCategory
                ? TransactionType.INCOME
                : TransactionType.EXPENSE;
        if (editingCategory == null) {
            viewModel.insert(new Category(name, type, color, icon, System.currentTimeMillis()));
            Toast.makeText(requireContext(), "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
        } else {
            editingCategory.setName(name);
            editingCategory.setType(type);
            editingCategory.setColor(color);
            editingCategory.setIcon(icon);
            viewModel.update(editingCategory);
            Toast.makeText(requireContext(), "Đã cập nhật danh mục", Toast.LENGTH_SHORT).show();
        }
        clearForm();
    }

    private boolean isValidColor(String color) {
        try {
            Color.parseColor(color);
            return color.matches("#[0-9a-fA-F]{6}");
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private void startEdit(Category category) {
        editingCategory = category;
        nameInput.setText(category.getName());
        colorInput.setText(category.getColor());
        iconInput.setText(category.getIcon());
        typeGroup.check(TransactionType.INCOME.equals(category.getType()) ? R.id.radioIncomeCategory : R.id.radioExpenseCategory);
        saveButton.setText("Cập nhật danh mục");
        cancelEditButton.setVisibility(View.VISIBLE);
    }

    private void clearForm() {
        editingCategory = null;
        nameInput.setText("");
        colorInput.setText(typeGroup.getCheckedRadioButtonId() == R.id.radioIncomeCategory ? "#16A34A" : "#DC2626");
        iconInput.setText("custom");
        saveButton.setText("Thêm danh mục");
        cancelEditButton.setVisibility(View.GONE);
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
