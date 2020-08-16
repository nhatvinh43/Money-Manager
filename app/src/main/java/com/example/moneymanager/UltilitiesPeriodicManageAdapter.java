package com.example.moneymanager;

import android.app.AlarmManager;
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

public class UltilitiesPeriodicManageAdapter extends RecyclerView.Adapter<UltilitiesPeriodicManageAdapter.ViewHolder> {
    private static UltilitiesPeriodicManageAdapter.ClickListener clickListener;
    ArrayList<PeriodicTransaction> mainModel;
    ArrayList<MoneySource> moneySourceArrayList;
    Context context;
    MoneyToStringConverter converter = new MoneyToStringConverter();

    public UltilitiesPeriodicManageAdapter(ArrayList<PeriodicTransaction> mainModel, ArrayList<MoneySource> moneySources, Context context) {
        this.mainModel = mainModel;
        this.moneySourceArrayList = moneySources;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.periodic_transaction_item, parent, false);
        return new UltilitiesPeriodicManageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleDateFormat sfd_day = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sfd_month = new SimpleDateFormat("dd");
        SimpleDateFormat sfd_year = new SimpleDateFormat("dd/MM");
        ExpenditureList expList = new ExpenditureList();
        String iconString = expList.getIcon(mainModel.get(position).getExpenditureId());
        int id = context.getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);

        holder.transactionName.setText(mainModel.get(position).getExpenditureName());
        holder.transactionAmount.setText((mainModel.get(position).getTransactionIsIncome() == true ? "+" : "-") + converter.moneyToString(mainModel.get(position).getTransactionAmount().doubleValue()));
        holder.transactionAmount.setTextColor(mainModel.get(position).getTransactionIsIncome() == true ? Color.GREEN : Color.RED);
        for(MoneySource ms : moneySourceArrayList) {
            if(ms.getMoneySourceId().equals(mainModel.get(position).getMoneySourceId())) {
                holder.transactionMoneySource.setText(ms.getMoneySourceName());
                break;
            }
        }
        holder.transactionIcon.setImageResource(id);

        Date time = new Date(mainModel.get(position).getTransactionTime().getTime());
        if(mainModel.get(position).getPeriodicType().equals("day")) {
            holder.transactionTime.setText(sfd_day.format(time));
            holder.transactionPeriodicType.setText("Hằng ngày");
        } else if (mainModel.get(position).getPeriodicType().equals("month")) {
            holder.transactionTime.setText(sfd_day.format(time) + " ngày " + sfd_month.format(time));
            holder.transactionPeriodicType.setText("Hằng tháng");
        } else if (mainModel.get(position).getPeriodicType().equals("year")) {
            holder.transactionTime.setText(sfd_day.format(time) + " ngày " + sfd_year.format(time));
            holder.transactionPeriodicType.setText("Hằng năm");
        }
    }

    @Override
    public int getItemCount() {
        return mainModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView transactionName;
        TextView transactionTime;
        TextView transactionAmount;
        TextView transactionMoneySource;
        TextView transactionPeriodicType;
        ImageView transactionIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            transactionName = itemView.findViewById(R.id.name_periodTransaction);
            transactionTime = itemView.findViewById(R.id.time_periodTransaction);
            transactionAmount = itemView.findViewById(R.id.amount_periodTransaction);
            transactionMoneySource = itemView.findViewById(R.id.moneySource_periodTransaction);
            transactionPeriodicType = itemView.findViewById(R.id.periodicType_periodTransaction);
            transactionIcon = itemView.findViewById(R.id.icon_periodTransaction);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void getFilter(ArrayList<PeriodicTransaction> filterList) {
        mainModel = filterList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(UltilitiesPeriodicManageAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
