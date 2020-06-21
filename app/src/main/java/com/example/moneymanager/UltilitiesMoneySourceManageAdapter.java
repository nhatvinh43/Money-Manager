package com.example.moneymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UltilitiesMoneySourceManageAdapter extends
        RecyclerView.Adapter<UltilitiesMoneySourceManageAdapter.SmallMoneySourceViewHolder> {
    private ArrayList<MoneySource> dataSet;
    private Context context;
    private LayoutInflater inflater;

    public UltilitiesMoneySourceManageAdapter(Context context, ArrayList<MoneySource> obj){
        this.context = context;
        this.dataSet = obj;
        this.inflater = LayoutInflater.from(context);
    }

    public class SmallMoneySourceViewHolder extends RecyclerView.ViewHolder{
        public TextView moneySourceName;
        public TextView moneySourceAmount;

        public SmallMoneySourceViewHolder(View view){
            super(view);
            moneySourceName = view.findViewById(R.id.moneySourceName_moneySourceListSmall);
            moneySourceAmount = view.findViewById(R.id.moneySourceAmount_moneySourceListSmall);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SmallMoneySourceViewHolder holder, int position) {
        MoneySource moneySource = this.dataSet.get(position);
        holder.moneySourceName.setText(moneySource.getMoneySourceName());
        holder.moneySourceAmount.setText(moneySource.getAmount().toString() + " " + moneySource.getCurrencyName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Xem Thông Tin Chi Tiết Của Ví", Toast.LENGTH_LONG).show();
            }
        });
    }

    @NonNull
    @Override
    public SmallMoneySourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.small_money_source_item, parent, false);
        return new SmallMoneySourceViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
