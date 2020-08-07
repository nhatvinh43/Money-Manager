package com.example.moneymanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.type.Money;

import java.util.ArrayList;

public class MoneySourceDetailsActivity extends AppCompatActivity {
    private TextView name, amount, cur;
    private EditText eName, eAmount, eCur;
    private FirebaseAuth firebaseAuth;
    private DataHelper dataHelper;
    private RecyclerView recyclerView;
    private AddMoneySourceCurrencyAdapter adapter;
    private ArrayList<Currency> currencies = new ArrayList<>();
    private MoneySource MS;
    private String MSId;
    private ArrayList<MoneySource> dataSet = new ArrayList<>();
    private ScrollView container;
    private ProgressBar loading;

    @Override
    protected void onStart() {
        super.onStart();
    }

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

        setContentView(R.layout.activity_money_source_details);
        container = findViewById(R.id.container);
        loading = findViewById(R.id.loading);

        container.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        MSId = intent.getStringExtra("MoneySourceId");
        firebaseAuth = FirebaseAuth.getInstance();
        dataHelper = new DataHelper();
        dataHelper.getMoneySourceById(new SingleMoneySourceCallBack() {
            @Override
            public void onCallBack(MoneySource moneySource) {
                MS = moneySource;
                initView();

                container.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCallBackFailed(String msg) {

            }
        }, MSId);
        currencies.add(new Currency("Cur01", "VND"));
        currencies.add(new Currency("Cur02", "$"));
        currencies.add(new Currency("Cur03", "AUD"));

    }

    public void initView(){
        name = findViewById(R.id.moneySourceNameDisplay_moneySourceDetails);
        amount = findViewById(R.id.moneySourceAmountDisplay_moneySourceDetails);
        cur = findViewById(R.id.moneySourceUnitDisplay_moneySourceDetails);
        eName = findViewById(R.id.moneySourceName_moneySourceDetails);
        eAmount = findViewById(R.id.moneySourceAmount_moneySourceDetails);
        eCur = findViewById(R.id.moneySourceUnit_moneySourceDetails);

        final MoneyToStringConverter converter = new MoneyToStringConverter();

        name.setText(MS.getMoneySourceName());
        eName.setText(MS.getMoneySourceName());

        amount.setText(converter.moneyToString((double) MS.getAmount()));
        eAmount.setText(converter.moneyToString((double) MS.getAmount()));

        cur.setText(MS.getCurrencyName());
        eCur.setText(MS.getCurrencyName());

        //Nút back
        findViewById(R.id.backButton_moneySourceDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        //Nút chon đơn vị tiền
        findViewById(R.id.chooseMoneySourceUnitButton_moneySourceDetails).setOnClickListener(new View.OnClickListener() {
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
                        Toast.makeText(categoryPanel.getContext(), currency.getCurrencyName()+" 11",Toast.LENGTH_LONG).show();
                        eCur.setText(currency.getCurrencyName());
                        MS.setCurrencyId(currency.getCurrencyId());
                        MS.setCurrencyName(currency.getCurrencyName());
                        categoryPanel.dismiss();
                    }
                });
            }
        });

        //Nút Lưu
        findViewById(R.id.save_moneySourceDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chuẩn bị dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MoneySourceDetailsActivity.this);
                builder.setView(R.layout.dialog_one_button);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setLayout(850,400);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView msg = dialog.findViewById(R.id.message_one_button_dialog);
                dialog.findViewById(R.id.confirm_one_button_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.hide();
                //Thất Bại
                if (eAmount.getText().toString().length() == 0 || eName.getText().toString().length() == 0
                || eCur.getText().toString().length() == 0) {
                    msg.setText("Vui lòng điền đầy đủ thông tin");
                    dialog.show();
                } else {
                    //Thành Công
                    dialog.dismiss();
                    Toast.makeText(dialog.getContext(), "Chỉnh Sửa Thành Công", Toast.LENGTH_LONG).show();
                    MS.setMoneySourceName(eName.getText().toString());
                    MS.setAmount(converter.stringToMoney(eAmount.getText().toString()));

                    Intent data = new Intent();
                    data.putExtra("moneysource", MS);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });

        //Nút Xóa
        findViewById(R.id.delete_moneySourceDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chuẩn bị dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MoneySourceDetailsActivity.this);
                builder.setView(R.layout.dialog_two_buttons);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setLayout(850,400);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final TextView msg = dialog.findViewById(R.id.message_two_button_dialog);
                msg.setText("Bạn chắc chắn muốn xóa");
                dialog.findViewById(R.id.confirm_two_button_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        Intent data = new Intent();
                        data.putExtra("moneysource", MS);
                        setResult(Activity.RESULT_FIRST_USER, data);
                        finish();
                    }
                });
                dialog.findViewById(R.id.cancel_two_button_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        //Nút Back
        findViewById(R.id.backButton_moneySourceDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}