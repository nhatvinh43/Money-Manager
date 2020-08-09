package com.example.moneymanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;

public class ManagePeriodicTransactions extends AppCompatActivity {
    public static final int ULTILITIES_NEW_PERIODICTRANSACION_RQCODE = 10;
    public static final int ULTILITIES_CHANGE_PERIODICTRANSACION_RQCODE = 11;
    ArrayList<PeriodicTransaction> periodicTrasactionListFull;
    ArrayList<PeriodicTransaction> periodicTrasactionList;
    RecyclerView periodicTransactionRecycleView;
    UltilitiesPeriodicManageAdapter periodicTransactionAdapter;
    LayoutAnimationController aController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String colorScheme = getSharedPreferences("MyPreferences", MODE_PRIVATE).getString("currentColor", "yellow");

        switch (colorScheme)
        {
            case "yellow":
            {
                getTheme().applyStyle(R.style.AppTheme,true);
                break;
            }

            case "blue":
            {
                getTheme().applyStyle(R.style.AppThemeBlue,true);
                break;
            }

            case "red":
            {
                getTheme().applyStyle(R.style.AppThemeRed,true);
                break;
            }

            case "green":
            {
                getTheme().applyStyle(R.style.AppThemeGreen,true);
                break;
            }

            case "purple":
            {
                getTheme().applyStyle(R.style.AppThemePurple,true);
                break;
            }
        }

        setContentView(R.layout.activity_manage_periodic_transactions);

        periodicTransactionRecycleView = findViewById(R.id.periodicTransactions);
        loadData();

        GridLayoutManager transactionLayoutManager = new GridLayoutManager(ManagePeriodicTransactions.this , 2);
        periodicTransactionRecycleView.setLayoutManager(transactionLayoutManager);

        periodicTransactionAdapter = new UltilitiesPeriodicManageAdapter(periodicTrasactionList, ManagePeriodicTransactions.this);
        periodicTransactionRecycleView.setAdapter(periodicTransactionAdapter);
        periodicTransactionRecycleView.addItemDecoration(new SpaceItemDecoration(2,30,false));

        aController = AnimationUtils.loadLayoutAnimation(periodicTransactionRecycleView.getContext(), R.anim.layout_fall_down);
        periodicTransactionRecycleView.setLayoutAnimation(aController);
        periodicTransactionRecycleView.scheduleLayoutAnimation();
        periodicTransactionAdapter.notifyDataSetChanged();

        periodicTransactionAdapter.setOnItemClickListener(new UltilitiesPeriodicManageAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                PeriodicTransaction peTrans = periodicTrasactionList.get(position);
                Log.d("----------------------- Id transation ",position + "    " + peTrans.getTransactionId());
                Intent transactionDetailIntent = new Intent(ManagePeriodicTransactions.this , PeriodicTransactionDetails.class);
                transactionDetailIntent.putExtra("periodicTransaction", peTrans);
                startActivityForResult(transactionDetailIntent, ULTILITIES_CHANGE_PERIODICTRANSACION_RQCODE);
            }
        });

        //Nút back
        findViewById(R.id.backButton_manage_periodic_transactions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        //"View by" menu initiation
        Spinner viewByMenu =findViewById(R.id.viewBy_manage_periodic_transactions);
        String[] viewByMenuItems = new String[]{"Tất cả","Ngày","Tháng","Năm"};

        ArrayAdapter<String> viewByAdapter = new ArrayAdapter<String>(this, R.layout.spinner_menu, viewByMenuItems);
        viewByMenu.setAdapter(viewByAdapter);
        viewByMenu.setSelection(0);
        viewByMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                ArrayList<PeriodicTransaction> filterList = new ArrayList<>();
                switch (selectedItem) {
                    case "Tất cả":
                        periodicTrasactionList.clear();
                        periodicTrasactionList.addAll(periodicTrasactionListFull);
                        periodicTransactionAdapter.notifyDataSetChanged();
                        periodicTransactionRecycleView.scheduleLayoutAnimation();
                        break;
                    case "Ngày":
                        periodicTrasactionList.clear();
                        for(PeriodicTransaction peTrans : periodicTrasactionListFull) {
                            if(peTrans.getPeriodicType().equals("day")) {
                                periodicTrasactionList.add(peTrans);
                            }
                        }
                        periodicTransactionAdapter.notifyDataSetChanged();
                        periodicTransactionRecycleView.scheduleLayoutAnimation();
                        break;
                    case "Tháng":
                        periodicTrasactionList.clear();
                        for(PeriodicTransaction peTrans : periodicTrasactionListFull) {
                            if(peTrans.getPeriodicType().equals("month")) {
                                periodicTrasactionList.add(peTrans);
                            }
                        }
                        periodicTransactionAdapter.notifyDataSetChanged();
                        periodicTransactionRecycleView.scheduleLayoutAnimation();
                        break;
                    case "Năm":
                        periodicTrasactionList.clear();
                        for(PeriodicTransaction peTrans : periodicTrasactionListFull) {
                            if(peTrans.getPeriodicType().equals("year")) {
                                periodicTrasactionList.add(peTrans);
                            }
                        }
                        periodicTransactionAdapter.notifyDataSetChanged();
                        periodicTransactionRecycleView.scheduleLayoutAnimation();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Nút thêm giao dịch
        findViewById(R.id.addPeriodicTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddPeriodicTransaction.class);
                startActivityForResult(intent, ULTILITIES_NEW_PERIODICTRANSACION_RQCODE);
            }
        });

        // Nút tìm kiếm
        EditText search = findViewById(R.id.search_manage_periodic_transactions);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("periodicTransactionList", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(periodicTrasactionListFull);
        editor.putString("list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("periodicTransactionList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<PeriodicTransaction>>() {}.getType();

        periodicTrasactionListFull = gson.fromJson(json, type);
        if(periodicTrasactionListFull == null) {
            periodicTrasactionListFull = new ArrayList<>();
            periodicTrasactionList = new ArrayList<>();
        } else {
            periodicTrasactionList = new ArrayList<>();
            periodicTrasactionList.addAll(periodicTrasactionListFull);
        }
    }

    private void filter(String text) {
        ArrayList<PeriodicTransaction> filterList = new ArrayList<>();

        if(text.isEmpty()) {
            filterList = periodicTrasactionList;
        } else {
            MoneyToStringConverter converter = new MoneyToStringConverter();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            for(PeriodicTransaction trans : periodicTrasactionList) {
                String money = converter.moneyToString(trans.getTransactionAmount().doubleValue());
                String expenditureName = trans.getExpenditureName().toLowerCase();
                String moneySourceName = trans.getMoneySourceName().toLowerCase();

                if(money.contains(text.toLowerCase())
                        || expenditureName.contains((text.toLowerCase()))
                        || moneySourceName.contains(text.toLowerCase())) {
                    filterList.add(trans);
                }
            }
        }

        periodicTransactionAdapter.getFilter(filterList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ULTILITIES_NEW_PERIODICTRANSACION_RQCODE) {
            if(resultCode == Activity.RESULT_OK) { // Thêm giao dịch định kỳ
                Log.d("-------------Test result from add periodictrans ", "OKE");
                PeriodicTransaction resPeriodicTransaction = (PeriodicTransaction) data.getParcelableExtra("periodicTransaction");
                periodicTrasactionListFull.add(resPeriodicTransaction);
                periodicTrasactionList.add(resPeriodicTransaction);
                periodicTransactionAdapter.notifyDataSetChanged();
                saveData();
            }
        }
        else if(requestCode == ULTILITIES_CHANGE_PERIODICTRANSACION_RQCODE) { // Thay đổi giao dịch định kỳ
            if(resultCode == Activity.RESULT_OK) {
                Log.d("-------------Test result from periodictrans detail ", "OKE");
                DataHelper dataHelper = new DataHelper();
                PeriodicTransaction resPeriodicTransaction = (PeriodicTransaction) data.getParcelableExtra("periodicTransaction");
                for(int i = 0; i < periodicTrasactionListFull.size(); i++) {
                    PeriodicTransaction peTrans = periodicTrasactionListFull.get(i);
                    if(peTrans.getTransactionId().equals(resPeriodicTransaction.getTransactionId())) {
                        periodicTrasactionListFull.remove(i);
                        periodicTrasactionListFull.add(i, resPeriodicTransaction);
                        break;
                    }
                }

                for(int i = 0; i < periodicTrasactionList.size(); i++) {
                    PeriodicTransaction peTrans = periodicTrasactionList.get(i);
                    if(peTrans.getTransactionId().equals(resPeriodicTransaction.getTransactionId())) {
                        periodicTrasactionList.remove(i);
                        periodicTrasactionList.add(i, resPeriodicTransaction);
                        break;
                    }
                }

                dataHelper.updatePeriodicTransaction(resPeriodicTransaction);
                periodicTransactionAdapter.notifyDataSetChanged();
                saveData();
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                Log.d("-------------Test result from periodictrans detail ", "DELETE");
                DataHelper dataHelper = new DataHelper();
                PeriodicTransaction resPeriodicTransaction = (PeriodicTransaction) data.getParcelableExtra("periodicTransaction");
                for(int i = 0; i < periodicTrasactionListFull.size(); i++) {
                    PeriodicTransaction peTrans = periodicTrasactionListFull.get(i);
                    if(peTrans.getTransactionId().equals(resPeriodicTransaction.getTransactionId())) {
                        periodicTrasactionListFull.remove(i);
                        break;
                    }
                }

                for(int i = 0; i < periodicTrasactionList.size(); i++) {
                    PeriodicTransaction peTrans = periodicTrasactionList.get(i);
                    if(peTrans.getTransactionId().equals(resPeriodicTransaction.getTransactionId())) {
                        periodicTrasactionList.remove(i);
                        break;
                    }
                }

                dataHelper.deletePeriodicTransaction(resPeriodicTransaction);
                periodicTransactionAdapter.notifyDataSetChanged();
                saveData();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("-------------Test result from periodictrans detail ", "CANCEL");
            }
        }
    }
}