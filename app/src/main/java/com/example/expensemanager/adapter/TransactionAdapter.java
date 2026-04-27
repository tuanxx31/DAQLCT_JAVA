package com.example.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.data.TransactionType;
import com.example.expensemanager.model.TransactionWithCategory;
import com.example.expensemanager.utils.DateUtils;
import com.example.expensemanager.utils.MoneyUtils;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final List<TransactionWithCategory> items = new ArrayList<>();
    private final Listener listener;

    public TransactionAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<TransactionWithCategory> transactions) {
        items.clear();
        if (transactions != null) {
            items.addAll(transactions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionWithCategory item = items.get(position);
        String categoryName = item.category == null ? "Chưa phân loại" : item.category.getName();
        String note = item.transaction.getNote() == null || item.transaction.getNote().trim().isEmpty()
                ? "Không có ghi chú"
                : item.transaction.getNote();
        boolean income = TransactionType.INCOME.equals(item.transaction.getType());
        String sign = income ? "+ " : "- ";

        holder.categoryName.setText(categoryName);
        holder.meta.setText(DateUtils.formatDate(item.transaction.getDate()) + " • " + note);
        holder.amount.setText(sign + MoneyUtils.format(item.transaction.getAmount()));
        holder.amount.setTextColor(holder.itemView.getResources().getColor(income ? R.color.income : R.color.expense));
        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        final TextView categoryName;
        final TextView meta;
        final TextView amount;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.textCategoryName);
            meta = itemView.findViewById(R.id.textTransactionMeta);
            amount = itemView.findViewById(R.id.textAmount);
        }
    }

    public interface Listener {
        void onEdit(TransactionWithCategory transaction);

        void onDelete(TransactionWithCategory transaction);
    }
}
