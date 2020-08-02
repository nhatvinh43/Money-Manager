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

public class AddTransactionChooseMoneySourceAdapter extends
        RecyclerView.Adapter<AddTransactionChooseMoneySourceAdapter.SmallMoneySourceViewHolder> {
    private ArrayList<MoneySource> dataSet;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public AddTransactionChooseMoneySourceAdapter(Context context, ArrayList<MoneySource> obj){
        this.context = context;
        this.dataSet = obj;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SmallMoneySourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.small_money_source_item, parent, false);
        return new SmallMoneySourceViewHolder(view, mListener);
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
        holder.moneySourceName.setText(moneySource.getMoneySourceName().toString());
        holder.moneySourceAmount.setText(moneyToString((Double)moneySource.getAmount()) + " " + moneySource.getCurrencyName().toString());
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }

    private String moneyToString(double amount) {
        if(amount == 0) return "0";
        StringBuilder mString = new StringBuilder();
        long mAmount = (long)amount;
        double remainder = amount - mAmount;
        int count = 0;
        while(mAmount > 0) {
            mString.insert(0, Long.toString(Math.floorMod(mAmount, 10)));
            mAmount /= 10;
            count++;

            if(count == 3 && mAmount != 0) {
                mString.insert(0, ",");
                count = 0;
            }
        }

        String decimal =  "";
        if (remainder > 0)
            decimal = String.valueOf(remainder).substring(String.valueOf(remainder).indexOf("."));
        return mString.toString() + decimal;
    }
}
