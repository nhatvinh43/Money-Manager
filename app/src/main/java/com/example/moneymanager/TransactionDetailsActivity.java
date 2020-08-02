package com.example.moneymanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TransactionDetailsActivity extends AppCompatActivity {
    private Transaction transaction;
    private MoneySource ms;
    private Timestamp timestamp;
    private LinearLayout food, bill, travel, health, party, spendingOther, bonus, profit, salary, gifted, incomingOther;
    private boolean isChange = false;

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
        final ExpenditureList expList = new ExpenditureList();
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
                if(isChange){
                    Intent data = new Intent();
                    data.putExtra("transaction", transaction);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            }
        });

        // Nút chọn ngày
        chooseDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show Date picker Dialog
                final Calendar calendar = Calendar.getInstance();
                final int mYear = calendar.get(Calendar.YEAR);
                final int mMonth = calendar.get(Calendar.MONTH);
                final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                final int mMin = calendar.get(Calendar.MINUTE);
                final Date date = new Date();
                DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionDetailsActivity.this,R.style.DatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                date.setYear(year-1900);
                                date.setMonth(month);
                                date.setDate(dayOfMonth);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                dateTime.setText(sdf.format(date.getTime()));
                                timestamp = new Timestamp(date.getTime());
                                transaction.setTransactionTime(timestamp);
                            }
                        }, mYear, mMonth, mDay);
                //show Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(TransactionDetailsActivity.this, R.style.TimePickerDialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                date.setHours(hourOfDay);
                                date.setMinutes(minute);
                                date.setSeconds(00);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                dateTime.setText(sdf.format(date.getTime()));
                                timestamp = new Timestamp(date.getTime());
                                transaction.setTransactionTime(timestamp);

                            }
                        }, mHour, mMin, false);
                timePickerDialog.show();
                datePickerDialog.show();
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

                    icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setView(R.layout.dialog_choose_category);

                            final ExpenditureList expenditureList = new ExpenditureList();
                            final ArrayList<Expenditure> expenditures = expenditureList.getList();

                            final AlertDialog categoryPanel = builder.create();
                            categoryPanel.show();
                            categoryPanel.getWindow().setLayout(1000,1200);
                            categoryPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            food = categoryPanel.findViewById(R.id.food_chooseCategory);
                            bill = categoryPanel.findViewById(R.id.bill_chooseCategory);
                            travel = categoryPanel.findViewById(R.id.travel_chooseCategory);
                            health = categoryPanel.findViewById(R.id.health_chooseCategory);
                            party = categoryPanel.findViewById(R.id.party_chooseCategory);
                            spendingOther = categoryPanel.findViewById(R.id.moreSpend_chooseCategory);
                            bonus = categoryPanel.findViewById(R.id.bonus_chooseCategory);
                            profit = categoryPanel.findViewById(R.id.profit_chooseCategory);
                            salary = categoryPanel.findViewById(R.id.salary_chooseCategory);
                            gifted = categoryPanel.findViewById(R.id.gift_chooseCategory);
                            incomingOther = categoryPanel.findViewById(R.id.moreIncome_chooseCategory);
                            food.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(0);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                            bill.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(1);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                            travel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(2);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                            health.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(3);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                            party.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(4);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                            spendingOther.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(5);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                            bonus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(6);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                            profit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(7);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                            salary.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(8);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                            gifted.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(9);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                            incomingOther.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Expenditure expenditure = expenditures.get(10);
                                    expenditureName.setText(expenditure.getExpenditureName());
                                    transaction.setExpenditureId(expenditure.getExpenditureId());
                                    transaction.setExpenditureName(expenditure.getExpenditureName());
                                    transaction.setTransactionIsIncome(expenditure.isIncome());

                                    String iconString = expenditureList.getIcon(expenditure.getExpenditureId());
                                    int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
                                    icon.setImageResource(id);

                                    categoryPanel.dismiss();
                                }
                            });
                        }
                    }); //Thêm on click cho icon, mở dialog_choose_category.xml

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
                    //Tạo dialog để thông báo lỗi
                    AlertDialog.Builder builder = new AlertDialog.Builder(TransactionDetailsActivity.this);
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

                    if (amount.getText().toString().length() == 0) {
                        dialog.show();
                        msg.setText("Số tiền giao dịch không được bỏ trống!");
                    }
                    else {
                        //Trường hợp đầy đủ thông tin
                        isChange = true;
                        transaction.setTransactionAmount(Double.valueOf(amount.getText().toString()));
                        transaction.setDescription(description.getText().toString());

                        icon.setOnClickListener(null);

                        amount.setEnabled(false);
                        dateTime.setLayoutParams(param);
                        chooseDateTimeButton.setVisibility(View.GONE);
                        description.setEnabled(false);

                        saveOrEditButton.setText("Sửa");
                    }
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