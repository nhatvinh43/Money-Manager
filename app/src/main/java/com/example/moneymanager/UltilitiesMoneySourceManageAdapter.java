package com.example.moneymanager;

import android.content.Context;
import android.content.Intent;
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
    private OnItemClickListener mListener;
    private MoneyToStringConverter converter = new MoneyToStringConverter();

    public interface OnItemClickListener{
        void onItemClick (int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    public UltilitiesMoneySourceManageAdapter(Context context, ArrayList<MoneySource> obj){
        this.context = context;
        this.dataSet = obj;
        this.inflater = LayoutInflater.from(context);
    }

    public class SmallMoneySourceViewHolder extends RecyclerView.ViewHolder{
        public TextView moneySourceName;
        public TextView moneySourceAmount;

        public SmallMoneySourceViewHolder(View view, final OnItemClickListener listener){
            super(view);
            moneySourceName = view.findViewById(R.id.moneySourceName_moneySourceListSmall);
            moneySourceAmount = view.findViewById(R.id.moneySourceAmount_moneySourceListSmall);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SmallMoneySourceViewHolder holder, int position) {
        final MoneySource moneySource = this.dataSet.get(position);
        holder.moneySourceName.setText(moneySource.getMoneySourceName());
        holder.moneySourceAmount.setText(converter.moneyToString((double)moneySource.getAmount()) + " " + moneySource.getCurrencyName());
    }

    @NonNull
    @Override
    public SmallMoneySourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.small_money_source_item, parent, false);
        return new SmallMoneySourceViewHolder(v, mListener);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
