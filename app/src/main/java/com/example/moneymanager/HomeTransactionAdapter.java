package com.example.moneymanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeTransactionAdapter extends RecyclerView.Adapter<HomeTransactionAdapter.ViewHolder> {
    ArrayList<Transaction> mainModel;
    Context context;

    public HomeTransactionAdapter(ArrayList<Transaction> mainModel, Context context) {
        this.mainModel = mainModel;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");

        holder.transactionName.setText(mainModel.get(position).getExpenditureName());
        holder.transactionTime.setText(sfd.format(new Date(mainModel.get(position).getTransactionTime().getTime())));
        holder.transactionAmount.setText((mainModel.get(position).getTransactionIsIncome() == true ? "+" : "-") + mainModel.get(position).getTransactionAmount().toString());
        holder.transactionAmount.setTextColor(mainModel.get(position).getTransactionIsIncome() == true ? Color.GREEN : Color.RED);
        holder.transactionMoneySource.setText("Thêm sau");
    }

    @Override
    public int getItemCount() {
        return mainModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView transactionName;
        TextView transactionTime;
        TextView transactionAmount;
        TextView transactionMoneySource;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            transactionName = itemView.findViewById(R.id.transactionName_item);
            transactionTime = itemView.findViewById(R.id.transactionTime_item);
            transactionAmount = itemView.findViewById(R.id.transactionAmount_item);
            transactionMoneySource = itemView.findViewById(R.id.transactionMoneySource_item);
        }
    }

    private String moneyToString(double amount) {
        StringBuilder mString = new StringBuilder();
        int nAmount = (int)amount;
        int count = 0;
        while(nAmount > 0) {
            mString.insert(0, Double.toString(nAmount % 10));
            nAmount /= 10;
            count++;

            if(count == 3 && nAmount != 0) {
                mString.insert(0, ",");
                count = 0;
            }
        }

        return mString.toString();
    }
}
