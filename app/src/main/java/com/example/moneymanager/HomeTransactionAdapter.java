package com.example.moneymanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    MoneyToStringConverter converter = new MoneyToStringConverter();

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
        SimpleDateFormat sfd1 = new SimpleDateFormat("dd/MM/yyyy");
        ExpenditureList expList = new ExpenditureList();
        String iconString = expList.getIcon(mainModel.get(position).getExpenditureId());
        int id = context.getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);

        holder.transactionName.setText(mainModel.get(position).getExpenditureName());
        holder.transactionTime.setText(sfd.format(new Date(mainModel.get(position).getTransactionTime().getTime())));
        holder.transactionAmount.setText((mainModel.get(position).getTransactionIsIncome() == true ? "+" : "-") + converter.moneyToString((double)mainModel.get(position).getTransactionAmount()));
        holder.transactionAmount.setTextColor(mainModel.get(position).getTransactionIsIncome() == true ? Color.GREEN : Color.RED);
        holder.transactionMoneySource.setText(sfd1.format(new Date(mainModel.get(position).getTransactionTime().getTime())));
        holder.transactionIcon.setImageResource(id);
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
        ImageView transactionIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            transactionName = itemView.findViewById(R.id.transactionName_item);
            transactionTime = itemView.findViewById(R.id.transactionTime_item);
            transactionAmount = itemView.findViewById(R.id.transactionAmount_item);
            transactionMoneySource = itemView.findViewById(R.id.transactionMoneySource_item);
            transactionIcon = itemView.findViewById(R.id.transactionIcon_item);
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
}
