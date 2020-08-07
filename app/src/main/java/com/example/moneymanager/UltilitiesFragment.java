package com.example.moneymanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UltilitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UltilitiesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UltilitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UltilitiesFragment.
     */

    public ArrayList<MoneySource> dataSet = new ArrayList<>();
    private RecyclerView recyclerView;
    private UltilitiesMoneySourceManageAdapter adapter;
    DataHelper dataHelper = new DataHelper();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uId = firebaseAuth.getCurrentUser().getUid();

    // TODO: Rename and change types and number of parameters
    public static UltilitiesFragment newInstance(String param1, String param2) {
        UltilitiesFragment fragment = new UltilitiesFragment();
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
        View v = inflater.inflate(R.layout.fragment_ultilities, container, false);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        view.findViewById(R.id.manageMoneySourcesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_manage_money_sources);
                final AlertDialog categoryPanel  = builder.create();
                categoryPanel.show();
                categoryPanel.getWindow().setLayout(1000,1200);
                categoryPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                categoryPanel.findViewById(R.id.addMoneySource_manageMoneySource).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AddMoneySourceActivity.class);
                        getActivity().startActivityForResult(intent, HomeFragment.HOME_NEW_MONEYSOURCE_RQCODE);
                        categoryPanel.dismiss();
                    }
                });

                final ProgressBar loading = categoryPanel.findViewById(R.id.loading);
                recyclerView = categoryPanel.findViewById(R.id.moneySourceList_manageMoneySource);
                loading.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);

                dataHelper.getMoneySourceWithoutTransactionList(new MoneySourceCallBack() {
                    @Override
                    public void onCallBack(ArrayList<MoneySource> list) {
                        dataSet = list;
                        adapter = new UltilitiesMoneySourceManageAdapter(categoryPanel.getContext(), list);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(categoryPanel.getContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        loading.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        adapter.setOnItemClickListener(new UltilitiesMoneySourceManageAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                MoneySource moneySource = dataSet.get(position);
                                Intent intent = new Intent(categoryPanel.getContext(), MoneySourceDetailsActivity.class);
                                intent.putExtra("MoneySourceId", moneySource.getMoneySourceId());
                                categoryPanel.dismiss();
                                getActivity().startActivityForResult(intent, HomeFragment.HOME_CHANGE_MONEYSOURCE_RQCODE);
                            }
                        });
                    }

                    @Override
                    public void onCallBackFail(String message) {

                    }
                }, uId);

            }
        });

        //Change main unit of currency
        view.findViewById(R.id.changeCurrentUnit_statistics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_choose_main_currency);
                final AlertDialog exchangeMoneyPanel  = builder.create();
                exchangeMoneyPanel.show();
                exchangeMoneyPanel.getWindow().setLayout(1000,1200);
                exchangeMoneyPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            }
        });

        //Currency converter
        view.findViewById(R.id.convertCurrencyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_currency_converter);
                final AlertDialog convertPanel  = builder.create();
                convertPanel.show();
                convertPanel.getWindow().setLayout(1000,1200);
                convertPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                convertPanel.findViewById(R.id.convert_currencyConverter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        convertPanel.findViewById(R.id.resultTitle_currencyConverter).setVisibility(View.VISIBLE);
                        convertPanel.findViewById(R.id.result_currencyConverter).setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //Calculate tax
        view.findViewById(R.id.calculateTaxButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_calculate_tax);
                final AlertDialog calculateTaxPanel  = builder.create();
                calculateTaxPanel.show();
                calculateTaxPanel.getWindow().setLayout(1000,1200);
                calculateTaxPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                calculateTaxPanel.findViewById(R.id.calculate_calculateTax).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calculateTaxPanel.findViewById(R.id.resultTitle_calculateTax).setVisibility(View.VISIBLE);
                        calculateTaxPanel.findViewById(R.id.result_calculateTax).setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //Calculate profit
        view.findViewById(R.id.calculateProfitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_calculate_profit);
                final AlertDialog calculateProfitPanel  = builder.create();
                calculateProfitPanel.show();
                calculateProfitPanel.getWindow().setLayout(1000,1200);
                calculateProfitPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                calculateProfitPanel.findViewById(R.id.calculate_calculateProfit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calculateProfitPanel.findViewById(R.id.resultTitle_calculateProfit).setVisibility(View.VISIBLE);
                        calculateProfitPanel.findViewById(R.id.result_calculateProfit).setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //Exchange money
        view.findViewById(R.id.exchangeMoneyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_exchange_money);
                final AlertDialog exchangeMoneyPanel  = builder.create();
                exchangeMoneyPanel.show();
                exchangeMoneyPanel.getWindow().setLayout(1000,1200);
                exchangeMoneyPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                exchangeMoneyPanel.findViewById(R.id.confirm_exchange_money).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });

        //Manage Periodic Transaction
        view.findViewById(R.id.manageSpecialTransactions_statistics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(v.getContext(), ManagePeriodicTransactions.class);
               startActivity(intent);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
