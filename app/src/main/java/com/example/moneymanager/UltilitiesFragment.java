package com.example.moneymanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.type.Money;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UltilitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UltilitiesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final int ULTILITIES_MANAGE_PERIODICTRANSACION_RQCODE = 20;
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
    int msCount = 0;

    private TextView periodicTransactionCount;
    private TextView moneySourceCount;

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
        periodicTransactionCount = view.findViewById(R.id.specialTransactionsCount_statistics);
        periodicTransactionCount.setText(getPeriodicTrasactionNumber());
        moneySourceCount = view.findViewById(R.id.moneySourcesCount_statistics);
        getMoneySourceNumber();

        //Quản lý nguồn tiền
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
                final MoneySource[] sourceMS = {new MoneySource()};
                final MoneySource[] desMS = {new MoneySource()};
                exchangeMoneyPanel.findViewById(R.id.chooseSource_exchange_money).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                        builder.setView(R.layout.dialog_choose_money_source);
                        final AlertDialog chooseSourceMS  = builder.create();
                        chooseSourceMS.show();
                        chooseSourceMS.getWindow().setLayout(1000,1200);
                        chooseSourceMS.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final ArrayList<MoneySource> sourcesMS = new ArrayList<>();
//                        UltilitiesMoneySourceManageAdapter adapter = new UltilitiesMoneySourceManageAdapter(v.getContext(), sourcesMS);
                        final RecyclerView recyclerView = chooseSourceMS.findViewById(R.id.moneySourceList_chooseMoneySource);
                        final ProgressBar loading = chooseSourceMS.findViewById(R.id.loading);
                        loading.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        dataHelper.getMoneySourceWithoutTransactionList(new MoneySourceCallBack() {
                            @Override
                            public void onCallBack(ArrayList<MoneySource> list) {
                                sourcesMS.clear();
                                sourcesMS.addAll(list);
                                loading.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                UltilitiesMoneySourceManageAdapter adapter = new UltilitiesMoneySourceManageAdapter(chooseSourceMS.getContext(), list);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(chooseSourceMS.getContext());
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                adapter.setOnItemClickListener(new UltilitiesMoneySourceManageAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        sourceMS[0] = sourcesMS.get(position);
                                        chooseSourceMS.dismiss();
                                        EditText SMS = exchangeMoneyPanel.findViewById(R.id.source_exchange_money);
                                        SMS.setText(sourceMS[0].getMoneySourceName());
                                    }
                                });
                            }
                            @Override
                            public void onCallBackFail(String message) {

                            }
                        }, uId);
                    }
                });

                exchangeMoneyPanel.findViewById(R.id.chooseDestination_exchange_money).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                        builder.setView(R.layout.dialog_choose_money_source);
                        final AlertDialog chooseSourceMS  = builder.create();
                        chooseSourceMS.show();
                        chooseSourceMS.getWindow().setLayout(1000,1200);
                        chooseSourceMS.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final ArrayList<MoneySource> sourcesMS = new ArrayList<>();
//                        UltilitiesMoneySourceManageAdapter adapter = new UltilitiesMoneySourceManageAdapter(v.getContext(), sourcesMS);
                        final RecyclerView recyclerView = chooseSourceMS.findViewById(R.id.moneySourceList_chooseMoneySource);
                        final ProgressBar loading = chooseSourceMS.findViewById(R.id.loading);
                        loading.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        dataHelper.getMoneySourceWithoutTransactionList(new MoneySourceCallBack() {
                            @Override
                            public void onCallBack(ArrayList<MoneySource> list) {
                                sourcesMS.clear();
                                sourcesMS.addAll(list);
                                loading.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                UltilitiesMoneySourceManageAdapter adapter = new UltilitiesMoneySourceManageAdapter(chooseSourceMS.getContext(), list);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(chooseSourceMS.getContext());
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                adapter.setOnItemClickListener(new UltilitiesMoneySourceManageAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        desMS[0] = sourcesMS.get(position);
                                        chooseSourceMS.dismiss();
                                        EditText SMS = exchangeMoneyPanel.findViewById(R.id.destination_exchange_money);
                                        SMS.setText(desMS[0].getMoneySourceName());
                                    }
                                });
                            }
                            @Override
                            public void onCallBackFail(String message) {

                            }
                        }, uId);
                    }
                });

                exchangeMoneyPanel.findViewById(R.id.confirm_exchange_money).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //chuẩn bị AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setView(R.layout.dialog_one_button);
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getWindow().setLayout(850,400);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final TextView msg = dialog.findViewById(R.id.message_one_button_dialog);
                        dialog.findViewById(R.id.confirm_one_button_dialog).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.hide();

                        EditText money, sourceMS1, desMS1;
                        money = exchangeMoneyPanel.findViewById(R.id.amount_exchange_money);
                        sourceMS1 = exchangeMoneyPanel.findViewById(R.id.source_exchange_money);
                        desMS1 = exchangeMoneyPanel.findViewById(R.id.destination_exchange_money);
                        if (money.getText().toString().isEmpty() || sourceMS1.getText().toString().isEmpty() ||
                        desMS1.getText().toString().isEmpty()){
                            //chưa đủ thông tin
                            dialog.show();
                            msg.setText("Vui lòng nhập đầy đủ thông tin!");
                        } else if (!sourceMS[0].getCurrencyName().equals(desMS[0].getCurrencyName())) {
                            //2 nguồn tiền khác đơn vị
                            dialog.show();
                            msg.setText("2 nguồn tiền không chung đơn vị!");
                        } else if (sourceMS[0].getMoneySourceId().equals(desMS[0].getMoneySourceId())){
                            //chuyển cùng 1 nguồn tiền
                            dialog.show();
                            msg.setText("Không thể chuyển cùng 1 nguồn tiền!");
                        } else {
                            double exchangeAmount = Double.valueOf(money.getText().toString());
                            sourceMS[0].setAmount((double)sourceMS[0].getAmount() - exchangeAmount);
                            desMS[0].setAmount((double)desMS[0].getAmount() + exchangeAmount);
                            Toast.makeText(v.getContext(),"chuyển thành công", Toast.LENGTH_LONG).show();
                            dataHelper.updateMoneySource(sourceMS[0]);
                            dataHelper.updateMoneySource(desMS[0]);
                            exchangeMoneyPanel.dismiss();
                        }
                    }
                });
            }
        });

        //Manage Periodic Transaction
        view.findViewById(R.id.manageSpecialTransactions_statistics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(v.getContext(), ManagePeriodicTransactions.class);
               startActivityForResult(intent, ULTILITIES_MANAGE_PERIODICTRANSACION_RQCODE);
            }
        });
    }

    private String getPeriodicTrasactionNumber() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("periodicTransactionList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<PeriodicTransaction>>() {}.getType();

        ArrayList<PeriodicTransaction> periodicTrasactionListFull = gson.fromJson(json, type);
        if(periodicTrasactionListFull == null) {
            return "0";
        }
        return String.valueOf(periodicTrasactionListFull.size());
    }

    private void getMoneySourceNumber() {
        dataHelper.getMoneySourceWithoutTransactionList(new MoneySourceCallBack() {
            @Override
            public void onCallBack(ArrayList<MoneySource> list) {
                msCount = list.size();
                moneySourceCount.setText(String.valueOf(msCount));
            }

            @Override
            public void onCallBackFail(String message) {

            }
        }, firebaseAuth.getCurrentUser().getUid());
    }

    private void deletePeriodicTransaction(String msId) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("periodicTransactionList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<PeriodicTransaction>>() {}.getType();

        DataHelper dataHelper = new DataHelper();
        ArrayList<PeriodicTransaction> periodicTrasactionListFull = gson.fromJson(json, type);
        ArrayList<PeriodicTransaction> newPeriodicTrasactionListFull = new ArrayList<>();

        if(periodicTrasactionListFull != null) {
            for(int i=0; i<periodicTrasactionListFull.size(); i++) {
                PeriodicTransaction peTrans = periodicTrasactionListFull.get(i);
                if(peTrans.getMoneySourceId().equals(msId)) {
                    dataHelper.deletePeriodicTransaction(peTrans);
                } else {
                    newPeriodicTrasactionListFull.add(peTrans);
                }
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(newPeriodicTrasactionListFull);
        editor.putString("list", json1);
        editor.apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ULTILITIES_MANAGE_PERIODICTRANSACION_RQCODE) {
            if(resultCode == Activity.RESULT_CANCELED) {
                periodicTransactionCount.setText(getPeriodicTrasactionNumber());
            }
        } else if (requestCode == HomeFragment.HOME_CHANGE_MONEYSOURCE_RQCODE) {
            if(resultCode == Activity.RESULT_FIRST_USER) {
                msCount -= 1;
                moneySourceCount.setText(String.valueOf(msCount));

                // Xóa giao dịch định kỳ
                MoneySource resMoneySource = (MoneySource) data.getParcelableExtra("moneysource");
                String msId = resMoneySource.getMoneySourceId();
                deletePeriodicTransaction(msId);

                periodicTransactionCount.setText(getPeriodicTrasactionNumber());
            }
        } else if (requestCode == HomeFragment.HOME_NEW_MONEYSOURCE_RQCODE) { // Thêm mới nguồn tiền
            if(resultCode == Activity.RESULT_OK) {
                msCount += 1;
                moneySourceCount.setText(String.valueOf(msCount));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    public double calculateTax(double totalIncome, double salaryWithIns, double dependencePerson){
        double totalTax = 0;
        return totalTax;
    }
    public double calculateProfit(double amountOfMoney, double period, double interesRate){
        double totalProfit = 0;
        return totalProfit;
    }


}
