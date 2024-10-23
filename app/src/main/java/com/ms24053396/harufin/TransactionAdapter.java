package com.ms24053396.harufin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final String username;
    private List<Transaction> transactionList;
    private Context context;

    public TransactionAdapter(Context context, List<Transaction> transactionList, String username) {
        this.context = context;
        this.transactionList = transactionList;
        this.username = username;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.dateTextView.setText(transaction.getDte());
        holder.sourceUserNameTextView.setText(transaction.getSourceUserName());
        holder.destUserNameTextView.setText(transaction.getDestUserName());


        if (transaction.getDestUserName().equals(username)) {
            holder.amountTextView.setText(String.valueOf("+" + transaction.getAmount()));
            holder.amountTextView.setTextColor(Color.GREEN); // Green for destination
        } else if (transaction.getSourceUserName().equals(username)) {
            holder.amountTextView.setText(String.valueOf("-" + transaction.getAmount()));
            holder.amountTextView.setTextColor(Color.RED); // Red for source
        } else {
            holder.amountTextView.setText(String.valueOf(transaction.getAmount()));
            holder.amountTextView.setTextColor(Color.BLACK); // Default color
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView sourceUserNameTextView, destUserNameTextView, amountTextView, dateTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date);
            sourceUserNameTextView = itemView.findViewById(R.id.textSourceUserName);
            destUserNameTextView = itemView.findViewById(R.id.textDestUserName);
            amountTextView = itemView.findViewById(R.id.textAmount);
        }
    }
}

