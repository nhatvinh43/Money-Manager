package com.example.moneymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddMoneySourceCurrencyAdapter extends
        RecyclerView.Adapter<AddMoneySourceCurrencyAdapter.CurrencyUnitViewHolder> {
    private ArrayList<Currency> dataSet, filterSet;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public AddMoneySourceCurrencyAdapter(Context context, ArrayList<Currency> obj){
        this.context = context;
        this.dataSet = obj;
        this.filterSet = obj;
        inflater = LayoutInflater.from(context);
    }

    public class CurrencyUnitViewHolder extends RecyclerView.ViewHolder{
        public TextView currencyUnit;
        public CurrencyUnitViewHolder(View view, final OnItemClickListener listener){
            super(view);
            currencyUnit = view.findViewById(R.id.unit_currencyList);
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

    @NonNull
    @Override
    public CurrencyUnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.currency_unit_list_item, parent, false);
        return new CurrencyUnitViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyUnitViewHolder holder, int position) {
        Currency currency = this.dataSet.get(position);
        holder.currencyUnit.setText(currency.getCurrencyName());
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }
}
