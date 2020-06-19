package com.example.moneymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeMoneySourceAdapter extends RecyclerView.Adapter<HomeMoneySourceAdapter.ViewHolder> {
    ArrayList<MoneySource> mainModel;
    Context context;

    public HomeMoneySourceAdapter(ArrayList<MoneySource> mainModel, Context context) {
        this.mainModel = mainModel;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.money_source_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.moneySourceName.setText(mainModel.get(position).getName());
        holder.moneySourceTotal.setText(Double.toString(mainModel.get(position).getCurrentMoney()));
        holder.moneySourceCurrency.setText(mainModel.get(position).getCurrencyID());
    }

    @Override
    public int getItemCount() {
        mainModel.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView moneySourceName;
        TextView moneySourceTotal;
        TextView moneySourceCurrency;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moneySourceName = itemView.findViewById(R.id.moneySourceName);
            moneySourceTotal = itemView.findViewById(R.id.moneySourceTotal);
            moneySourceTotal = itemView.findViewById(R.id.moneySourceCurrency);
        }
    }
}
