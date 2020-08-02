package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionDetailsActivity extends AppCompatActivity {
    private Transaction transaction;
    private MoneySource ms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        final ProgressBar loading = findViewById(R.id.loading);
        final ScrollView container = findViewById(R.id.container);

        loading.setVisibility(View.VISIBLE);
        container.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        final String trans = intent.getStringExtra("TransactionId");

        final DataHelper dataHelper = new DataHelper();
        dataHelper.getTransactionById(new SingleTransactionCallBack() {
            @Override
            public void onCallBack(Transaction ts) {
                transaction = ts;

                dataHelper.getMoneySourceById(new SingleMoneySourceCallBack() {
                    @Override
                    public void onCallBack(MoneySource moneySource) {
                        ms = moneySource;
                        initView();
                        loading.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCallBackFailed(String msg) {
                        Log.d("-------- From TransactionDetailActivity --------", msg);
                    }
                }, transaction.getMoneySourceId());
            }

            @Override
            public void onCallBackFailed(String msg) {
                Log.d("-------- From TransactionDetailActivity --------", msg);
            }
        }, trans);
    }

    private void initView() {
        final Button saveOrEditButton = findViewById(R.id.editAndSave_transactionDetails);
        final Button chooseDateTimeButton = findViewById(R.id.chooseDateTime_transactionDetails);
        final EditText dateTime = findViewById(R.id.dateTime_transactionDetails);
        final EditText description = findViewById(R.id.description_transactionDetails);
        final EditText amount = findViewById(R.id.amount_transactionDetails);
        final EditText moneySourceName = findViewById(R.id.moneySource_transactionDetails);
        final TextView expenditureName = findViewById(R.id.expenditureName);
        final ImageView icon = findViewById(R.id.transactionIcon_transactionDetails);

        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        ExpenditureList expList = new ExpenditureList();
        String iconString = expList.getIcon(transaction.getExpenditureId());
        int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);

        dateTime.setText(sfd.format(new Date(transaction.getTransactionTime().getTime())));
        description.setText(transaction.getDescription());
        amount.setText(moneyToString((double)transaction.getTransactionAmount()));
        moneySourceName.setText(ms.getMoneySourceName());
        expenditureName.setText(transaction.getExpenditureName());
        icon.setImageResource(id);

        // Nút back
        findViewById(R.id.backButton_transactionDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Nút lưu và chỉnh sửa
        saveOrEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentStatus = (String) saveOrEditButton.getText();

                if(currentStatus.equals("Sửa"))
                {

                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            0,LinearLayout.LayoutParams.MATCH_PARENT,3.0f);

                    icon.setOnClickListener(null);
                    amount.setEnabled(true);
                    dateTime.setLayoutParams(param);
                    chooseDateTimeButton.setVisibility(View.VISIBLE);
                    description.setEnabled(true);

                    saveOrEditButton.setText("Lưu");

                }
                else
                {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            0,LinearLayout.LayoutParams.MATCH_PARENT,4.0f);

                    icon.setOnClickListener(null); //Thêm on click cho icon, mở dialog_choose_category.xml
                    amount.setEnabled(false);
                    dateTime.setLayoutParams(param);
                    chooseDateTimeButton.setVisibility(View.GONE);
                    description.setEnabled(false);

                    saveOrEditButton.setText("Sửa");

                }

            }
        });
    }

    private String moneyToString(double amount) {
        if(amount == 0) return "0";
        StringBuilder mString = new StringBuilder();
        long mAmount = (long) amount;
        double remainder = amount - mAmount;
        int count = 0;
        while (mAmount > 0) {
            mString.insert(0, Long.toString(Math.floorMod(mAmount, 10)));
            mAmount /= 10;
            count++;

            if (count == 3 && mAmount != 0) {
                mString.insert(0, ",");
                count = 0;
            }
        }

        String decimal = "";
        if (remainder > 0)
            decimal = String.valueOf(remainder).substring(String.valueOf(remainder).indexOf("."));

        return mString.toString() + decimal;
    }
}