package com.example.moneymanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class AddMoneySourceActivity extends AppCompatActivity {
    //prepare data
    private FirebaseAuth firebaseAuth;
    private DataHelper dataHelper;
    private String uId;
    //Thiếu Limit khi tạo nguồn tiền
    public static ArrayList<Currency> currencies = new ArrayList<>();
    private RecyclerView recyclerView;
    private AddMoneySourceCurrencyAdapter adapter;
    private EditText moneySourceName, moneySourceAmount, moneySourceCurrency;
    private MoneySource resMoneySource = new MoneySource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money_source);

        firebaseAuth = FirebaseAuth.getInstance();
        dataHelper = new DataHelper();

        resMoneySource.setUserId(firebaseAuth.getCurrentUser().getUid());
        resMoneySource.setLimit(1000);

        //prepare data
        currencies.add(new Currency("Cur01", "VND"));
        currencies.add(new Currency("Cur02", "$"));
        currencies.add(new Currency("Cur03", "AUD"));


        moneySourceName = findViewById(R.id.moneySourceName_addMoneySource);
        moneySourceAmount = findViewById(R.id.moneySourceAmount_addMoneySource);
        moneySourceCurrency = findViewById(R.id.moneySourceUnit_addMoneySource);

        //Back Button
        findViewById(R.id.backButton_addMoneySource).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //nút chọn đơn vị tiền
        findViewById(R.id.chooseMoneySourceUnitButton_addMoneySource).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_choose_currency);
                final AlertDialog categoryPanel  = builder.create();
                categoryPanel.show();
                categoryPanel.getWindow().setLayout(1000,1200);
                categoryPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                adapter = new AddMoneySourceCurrencyAdapter(categoryPanel.getContext(), currencies);
                recyclerView = categoryPanel.findViewById(R.id.unitList_chooseCurrency);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(categoryPanel.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adapter.setOnItemClickListener(new AddMoneySourceCurrencyAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Currency currency = currencies.get(position);
                        moneySourceCurrency.setText(currency.getCurrencyName());
                        resMoneySource.setCurrencyId(currency.getCurrencyId());
                        resMoneySource.setCurrencyName(currency.getCurrencyName());
                        categoryPanel.dismiss();
                    }
                });
            }
        });

        //nút lưu nguồn tiền
        findViewById(R.id.save_addMoneySource).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chuẩn bị AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMoneySourceActivity.this);
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
                //Thất Bại
                if (moneySourceAmount.getText().toString().length() == 0 ||
                moneySourceName.getText().toString().length() == 0 || moneySourceCurrency.getText().toString().length() == 0){
                    dialog.show();
                    msg.setText("Vui Lòng nhập đủ thông tin");
                }else {
                    //Thành công
                    try {
                        resMoneySource.setAmount(NumberFormat.getInstance().parse(moneySourceAmount.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    resMoneySource.setMoneySourceName(moneySourceName.getText().toString());
                    //UltilitiesFragment.dataSet.add(resMoneySource);
                    Number tmpAmount, tmpLimit;
                    tmpAmount = 0;
                    try {
                        tmpAmount = NumberFormat.getInstance().parse(moneySourceAmount.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    tmpLimit = 1000;
                    dataHelper.createMoneySource(firebaseAuth.getCurrentUser().getUid(), resMoneySource.getMoneySourceName(), resMoneySource.getAmount(),
                            resMoneySource.getLimit(), resMoneySource.getCurrencyId(), resMoneySource.getCurrencyName());
                    Toast.makeText(dialog.getContext(), "Thành công", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    setResult(1, intent);
                    finish();
                }
            }
        });

    }
}