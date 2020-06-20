package com.example.moneymanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.zip.Inflater;

public class AddTransactionActivity extends AppCompatActivity {
    private ArrayList<Expenditure> expenditures = new ArrayList<>();
    private EditText moneySourceEditText;
    private EditText dateTimeEditText;
    private EditText typeOfExpenditure;
    private EditText amount;
    private EditText description;
    private RecyclerView recyclerView;
    private ArrayList<MoneySource> dataSet;
    private AddTransactionChooseMoneySourceAdapter adapter;
    private Date date = new Date();
    private Timestamp timestamp;
    private LinearLayout food, bill, travel, health, party, spendingOther, bonus, profit, salary, gifted, incomingOther;

    private Transaction resTransaction = new Transaction();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        //prepare data and model
        resTransaction.setTransactionId("Tran001");
        //prepare data
        expenditures.add(new Expenditure("Exp01","Ăn Uống", false));
        expenditures.add(new Expenditure("Exp02", "Sinh Hoạt", false));
        expenditures.add(new Expenditure("Exp03", "Đi Lại", false));
        expenditures.add(new Expenditure("Exp04", "Sức Khỏe", false));
        expenditures.add(new Expenditure("Exp05", "Đám Tiệc", false));
        expenditures.add(new Expenditure("Exp06", "Thu Khác", false));
        expenditures.add(new Expenditure("Exp07", "Tiền Thưởng", true));
        expenditures.add(new Expenditure("Exp08", "Tiền Lãi", true));
        expenditures.add(new Expenditure("Exp09", "Tiền Lương", true));
        expenditures.add(new Expenditure("Exp10", "Được Tặng", true));
        expenditures.add(new Expenditure("Exp11", "Chi Khác", true));

        //prepare model

        moneySourceEditText = findViewById(R.id.moneySource_addTransaction);
        dateTimeEditText = findViewById(R.id.dateTime_addTransaction);
        typeOfExpenditure = findViewById(R.id.category_addTransaction);
        amount = findViewById(R.id.moneyAmount_addTransaction);
        description = findViewById(R.id.description_addTransaction);

        dataSet = new ArrayList<>();
        MoneySource m1 = new MoneySource(10000,"CUR1", "VND", 1000,
                "MS1", "MS001", "U1");
        dataSet.add(m1);
        MoneySource m2 = new MoneySource(100000,"CUR1", "VND", 1000,
                "MS2", "MS002", "U1");
        dataSet.add(m2);

        //Nút back
        findViewById(R.id.backButton_addTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //Nút chọn nguồn tiền
        findViewById(R.id.chooseMoneySourceButton_addTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_choose_money_source);
                final AlertDialog categoryPanel  = builder.create();

                categoryPanel.show();
                categoryPanel.getWindow().setLayout(1000,1200);
                categoryPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                adapter = new AddTransactionChooseMoneySourceAdapter(categoryPanel.getContext(), dataSet);
                recyclerView = categoryPanel.findViewById(R.id.moneySourceList_chooseMoneySource);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(categoryPanel.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adapter.setOnItemClickListener(new AddTransactionChooseMoneySourceAdapter.OnItemClickListener(){
                    @Override
                    public void onItemClick(int position) {
                        MoneySource moneySource = dataSet.get(position);
                        moneySourceEditText.setText(moneySource.getMoneySourceName());
                        resTransaction.setMoneySourceId(moneySource.getMoneySourceId());
                        categoryPanel.dismiss();
                    }
                });
            }
        });

        //Nút chọn ngày
        findViewById(R.id.chooseDateTimeButton_addTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show Date picker Dialog
                final Calendar calendar = Calendar.getInstance();
                final int mYear = calendar.get(Calendar.YEAR);
                final int mMonth = calendar.get(Calendar.MONTH);
                final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                final int mMin = calendar.get(Calendar.MINUTE);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTransactionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        date.setYear(year-1900);
                        date.setMonth(month);
                        date.setDate(dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy HH:mm");
                        dateTimeEditText.setText(sdf.format(date.getTime()));
                        timestamp = new Timestamp(date.getTime());
                        resTransaction.setTransactionTime(timestamp);
                    }
                }, mYear, mMonth, mDay);
                //show Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddTransactionActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                date.setHours(hourOfDay);
                                date.setMinutes(minute);
                                date.setSeconds(00);

                            }
                        }, mHour, mMin, false);
                timePickerDialog.show();
                datePickerDialog.show();

            }
        });

        //Nút chọn danh mục
        findViewById(R.id.chooseCategoryButton_addTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_choose_category);
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
                        typeOfExpenditure.setText(expenditures.get(0).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(0).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(0).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(0).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                bill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("11111111111111", "onClick: " + "Bill");
                        typeOfExpenditure.setText(expenditures.get(1).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(1).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(1).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(1).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                travel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("11111111111111", "onClick: " + "An UOng");
                        typeOfExpenditure.setText(expenditures.get(2).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(2).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(2).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(2).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                health.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("11111111111111", "onClick: " + "An UOng");
                        typeOfExpenditure.setText(expenditures.get(3).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(3).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(3).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(3).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                party.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("11111111111111", "onClick: " + "An UOng");
                        typeOfExpenditure.setText(expenditures.get(4).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(4).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(4).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(4).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                spendingOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("11111111111111", "onClick: " + "An UOng");
                        typeOfExpenditure.setText(expenditures.get(5).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(5).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(5).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(5).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                bonus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("11111111111111", "onClick: " + "An UOng");
                        typeOfExpenditure.setText(expenditures.get(6).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(6).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(6).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(6).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                profit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("11111111111111", "onClick: " + "An UOng");
                        typeOfExpenditure.setText(expenditures.get(7).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(7).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(7).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(7).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                salary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("11111111111111", "onClick: " + "An UOng");
                        typeOfExpenditure.setText(expenditures.get(8).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(8).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(8).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(8).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                gifted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("11111111111111", "onClick: " + "An UOng");
                        typeOfExpenditure.setText(expenditures.get(9).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(9).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(9).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(9).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                incomingOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("11111111111111", "onClick: " + "An UOng");
                        typeOfExpenditure.setText(expenditures.get(10).getExpenditureName());
                        resTransaction.setExpenditureId(expenditures.get(10).getExpenditureId());
                        resTransaction.setExpenditureName(expenditures.get(10).getExpenditureName());
                        resTransaction.setTransactionIsIncome(expenditures.get(10).isIncome());
                        categoryPanel.dismiss();
                    }
                });
            }
        });
        //Nút Lưu
        findViewById(R.id.save_editUserInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tạo dialog để thông báo lỗi
                AlertDialog.Builder builder = new AlertDialog.Builder(AddTransactionActivity.this);
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
                //Trường hợp khuyết thông tin
//                private EditText moneySourceEditText;
//                private EditText dateTimeEditText;
//                private EditText typeOfExpenditure;
//                private EditText amount;

                if (moneySourceEditText.getText().toString().length() == 0 || dateTimeEditText.getText().toString().length() == 0
                || typeOfExpenditure.getText().toString().length() == 0 || amount.getText().toString().length() == 0) {
                    dialog.show();
                    msg.setText("Vui Lòng Điền Đầy Đủ Thông Tin");
                }
                else {
                    //Trường hợp đầy đủ thông tin
                    try {
                        resTransaction.setTransactionAmout(NumberFormat.getInstance().parse(amount.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    resTransaction.setDescription(description.getText().toString());
                    dialog.show();
                    msg.setText("Tạo Thành Công" + resTransaction.getTransactionId() + " " + resTransaction.getTransactionAmout());
                    finish();
                }
            }
        });
    }
}
