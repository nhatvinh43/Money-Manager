package com.example.moneymanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AnyChartView DayPieChart;
    private ArrayList<Transaction> dataSet = new ArrayList<>();
    private String[] DayPieChartTitle = {"Thu", "Chi"};
    private ExpenditureList expenditureList = new ExpenditureList();


    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Transaction t1 = new Transaction();
        t1.setTransactionAmount(10000.0);
        t1.setTransactionId("1");
        t1.setTransactionIsIncome(true);
        t1.setMoneySourceId("1");
        t1.setExpenditureId("1");
        Date date = new Date();
        Timestamp tsp = new Timestamp(date.getTime());
        t1.setTransactionTime(tsp);
        Transaction t2 = new Transaction();
        t2.setTransactionAmount(10000.0);
        t2.setTransactionId("2");
        t2.setTransactionIsIncome(false);
        t2.setMoneySourceId("1");
        t2.setExpenditureId("2");
        t2.setTransactionTime(tsp);
        dataSet.add(t1);
        dataSet.add(t2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    public void initView(View v){
        this.DayPieChart = v.findViewById(R.id.DayPieChart);
        setupDayPieChart();

    }

    public void setupDayPieChart(){
        Pie dayPieChart = AnyChart.pie();
        Date date = new Date();
        ArrayList<Transaction> todayTransaction = new ArrayList<>();
        todayTransaction = getTransactionByDay(dataSet, date);
        ArrayList<Double> incomeAndSpend = new ArrayList<>();
        incomeAndSpend = intoIncomeAndSpend(todayTransaction);
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < incomeAndSpend.size(); i++){
            dataEntries.add(new ValueDataEntry(DayPieChartTitle[i], incomeAndSpend.get(i)));
            System.out.println(incomeAndSpend.get(i) + "Thu Chi Ne");
        }
        dayPieChart.data(dataEntries);
        this.DayPieChart.setChart(dayPieChart);
    }

    public ArrayList<Transaction> getTransactionByDay(ArrayList<Transaction> transactions, Date date){
        ArrayList<Transaction> res = new ArrayList<>();
        for (Transaction tx : transactions){
            System.out.println(tx.getTransactionTime().getDate() + "Time ne");
            if (tx.getTransactionTime().getDate() == date.getDate() &&
                    tx.getTransactionTime().getMonth() == date.getMonth() && tx.getTransactionTime().getYear() == date.getYear()){
                res.add(tx);
            }
        }
        return res;
    }

    public ArrayList<Double> intoIncomeAndSpend(ArrayList<Transaction> transactions){
        ArrayList<Double> res = new ArrayList<>();
        double income, spend;
        income = 0;
        spend = 0;
        for (Transaction tx : transactions){
            if (tx.getTransactionIsIncome()){
                income += (double) tx.getTransactionAmount();
            } else {
                spend += (double) tx.getTransactionAmount();
            }
        }
        res.add(income);
        res.add(spend);
        return res;
    }

}
