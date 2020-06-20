package com.example.moneymanager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Money source
    RecyclerView moneySourceRecycleView;
    ArrayList<MoneySource> moneySourceList;
    HomeMoneySourceAdapter moneySourceAdapter;

    // Transaction
    RecyclerView transactionRecycleView;
    ArrayList<Transaction> transactionList;
    HomeTransactionAdapter transactionAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Moneysource RecycleView Initiation
        moneySourceRecycleView = view.findViewById(R.id.moneySourceList);
        moneySourceList = new ArrayList<>();
        moneySourceList.add(new MoneySource(10000, "a","VND",500000, "a", "Ví chung", "1"));
        moneySourceList.add(new MoneySource(130000, "a","VND",600000, "b", "Ví ăn vặt", "1"));
        moneySourceList.add(new MoneySource(140000, "a","VND",1000000, "c", "Ví tiết kiệm", "1"));

        LinearLayoutManager moneySourceLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        moneySourceRecycleView.setLayoutManager(moneySourceLayoutManager);
        moneySourceRecycleView.setItemAnimator(new DefaultItemAnimator());

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(moneySourceRecycleView);

        moneySourceAdapter = new HomeMoneySourceAdapter(moneySourceList, getContext());
        moneySourceRecycleView.setAdapter(moneySourceAdapter);
        moneySourceRecycleView.setClipToPadding(false);
        final int activeColor = ContextCompat.getColor(view.getContext(), R.color.white);
        final int inactiveColor = ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark);
        moneySourceRecycleView.addItemDecoration(new DotsIndicatorDecoration(9,20,100,inactiveColor,activeColor));


        // Transaction RecycleView Initiation
        transactionRecycleView = view.findViewById(R.id.transactionList);
        transactionList = new ArrayList<>();
        transactionList.add(new Transaction("vui", "1", "Tiền lương", 1000000, "a", "MS01",true, new Timestamp(100000)));
        transactionList.add(new Transaction("vui quá", "2", "Tiền điện", 120000, "b", "MS01",false, new Timestamp(200000)));
        transactionList.add(new Transaction("vui à", "3", "Tiền uống", 100000, "c","MS01", false, new Timestamp(300000)));
        transactionList.add(new Transaction("vui ghê", "1", "Tiền lương", 3000000, "d","MS01", true, new Timestamp(450000)));
        transactionList.add(new Transaction("vui bla", "4", "Tiền ăn", 650000, "e","MS01", false, new Timestamp(678000)));

        GridLayoutManager transactionLayoutManager = new GridLayoutManager(getContext(), 2);
        transactionRecycleView.setLayoutManager(transactionLayoutManager);
//        transactionRecycleView.setItemAnimator(new DefaultItemAnimator());

        transactionAdapter = new HomeTransactionAdapter(transactionList, getContext());
        transactionRecycleView.setAdapter(transactionAdapter);
        transactionRecycleView.addItemDecoration(new SpaceItemDecoration(2,30,false));
    }
}
