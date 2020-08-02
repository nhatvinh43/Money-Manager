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
    private static ClickListener clickListener;
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
        holder.transactionAmount.setText((mainModel.get(position).getTransactionIsIncome() == true ? "+" : "-") + moneyToString((double)mainModel.get(position).getTransactionAmount()));
        holder.transactionAmount.setTextColor(mainModel.get(position).getTransactionIsIncome() == true ? Color.GREEN : Color.RED);
        holder.transactionMoneySource.setText("Thêm sau");
    }

    @Override
    public int getItemCount() {
        return mainModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView transactionName;
        TextView transactionTime;
        TextView transactionAmount;
        TextView transactionMoneySource;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            transactionName = itemView.findViewById(R.id.transactionName_item);
            transactionTime = itemView.findViewById(R.id.transactionTime_item);
            transactionAmount = itemView.findViewById(R.id.transactionAmount_item);
            transactionMoneySource = itemView.findViewById(R.id.transactionMoneySource_item);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    private String moneyToString(double amount) {
        if(amount == 0) return "0";
        StringBuilder mString = new StringBuilder();
        long mAmount = (long) amount;
        double remainder = amount - mAmount;
        int count = 0;
        while (mAmount > 0) {
            mString.insert(0, Long.toString(Math.floorMod(mAmount, 10)));
            mAmount /= 10;
            count++;

            if (count == 3 && mAmount != 0) {
                mString.insert(0, ",");
                count = 0;
            }
        }

        String decimal = "";
        if (remainder > 0)
            decimal = String.valueOf(remainder).substring(String.valueOf(remainder).indexOf("."));

        return mString.toString() + decimal;
    }
}
