package com.example.expensemanager.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.data.TransactionType;
import com.example.expensemanager.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final List<Category> items = new ArrayList<>();
    private final Listener listener;

    public CategoryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Category> categories) {
        items.clear();
        if (categories != null) {
            items.addAll(categories);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = items.get(position);
        holder.name.setText(category.getName());
        holder.type.setText(TransactionType.INCOME.equals(category.getType()) ? "Thu nhập" : "Chi tiêu");
        try {
            holder.color.setTextColor(Color.parseColor(category.getColor()));
        } catch (IllegalArgumentException ignored) {
            holder.color.setTextColor(holder.itemView.getResources().getColor(R.color.primary));
        }
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(category);
            return true;
        });
        holder.itemView.setOnClickListener(v -> listener.onEdit(category));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final TextView color;
        final TextView name;
        final TextView type;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.textCategoryColor);
            name = itemView.findViewById(R.id.textCategoryName);
            type = itemView.findViewById(R.id.textCategoryType);
        }
    }

    public interface Listener {
        void onEdit(Category category);

        void onDelete(Category category);
    }
}
