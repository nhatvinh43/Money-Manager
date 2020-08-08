package com.example.moneymanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddPeriodicTransaction extends AppCompatActivity {
    PeriodicTransaction periodicTransaction = new PeriodicTransaction();
    ArrayList<MoneySource> moneySourcesList = new ArrayList<>();
    ArrayList<Expenditure> expenditures = new ArrayList<>();

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DataHelper dataHelper = new DataHelper();
    ExpenditureList expenditureList = new ExpenditureList();

    private EditText moneySourceEditText;
    private EditText dateTimeEditText;
    private EditText typeOfExpenditure;
    private EditText amount;
    private EditText description;
    private LinearLayout food, bill, travel, health, party, spendingOther, bonus, profit, salary, gifted, incomingOther;
    private ProgressBar loading;
    private ScrollView container;

    enum TYPE {
        DAY,
        MONTH,
        YEAR
    }

    TYPE type = TYPE.DAY;

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

        setContentView(R.layout.activity_add_periodic_transaction);
        loading = findViewById(R.id.loading);
        container = findViewById(R.id.container);
        loading.setVisibility(View.VISIBLE);
        container.setVisibility(View.INVISIBLE);

        dataHelper.getMoneySourceWithoutTransactionList(new MoneySourceCallBack() {
            @Override
            public void onCallBack(ArrayList<MoneySource> list) {
                moneySourcesList.addAll(list);
                initView();
                loading.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCallBackFail(String message) {

            }
        }, firebaseAuth.getCurrentUser().getUid());


    }

    private void initView() {
        moneySourceEditText = findViewById(R.id.moneySource_addPeriodicTransaction);
        dateTimeEditText = findViewById(R.id.dateTime_addPeriodicTransaction);
        typeOfExpenditure = findViewById(R.id.category_addPeriodicTransaction);
        amount = findViewById(R.id.moneyAmount_addPeriodicTransaction);
        description = findViewById(R.id.description_addPeriodicTransaction);
        expenditures.addAll(expenditureList.getList());

        //Back
        findViewById(R.id.backButton_addPeriodicTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //Period menu initiation
        Spinner periodMenu = findViewById(R.id.period_addPeriodTransaction);
        String[] periodMenuItems = new String[]{"Ngày", "Tháng", "Năm"};

        final ArrayAdapter<String> periodAdapter = new ArrayAdapter<String>(this, R.layout.spinner_menu, periodMenuItems);
        periodMenu.setAdapter(periodAdapter);
        periodMenu.setSelection(0);
        periodMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                ArrayList<PeriodicTransaction> filterList = new ArrayList<>();
                switch (selectedItem) {
                    case "Ngày":
                        type = TYPE.DAY;
                        periodicTransaction.setPeriodicType("day");
                        break;
                    case "Tháng":
                        type = TYPE.MONTH;
                        periodicTransaction.setPeriodicType("month");
                        break;
                    case "Năm":
                        type = TYPE.YEAR;
                        periodicTransaction.setPeriodicType("year");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Nút chọn nguồn tiền
        findViewById(R.id.chooseMoneySourceButton_addPeriodicTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_choose_money_source);
                final AlertDialog categoryPanel = builder.create();

                categoryPanel.show();
                categoryPanel.getWindow().setLayout(1000, 1200);
                categoryPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                AddTransactionChooseMoneySourceAdapter adapter = new AddTransactionChooseMoneySourceAdapter(categoryPanel.getContext(), moneySourcesList);
                RecyclerView recyclerView = categoryPanel.findViewById(R.id.moneySourceList_chooseMoneySource);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(categoryPanel.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adapter.setOnItemClickListener(new AddTransactionChooseMoneySourceAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        MoneySource moneySource = moneySourcesList.get(position);
                        moneySourceEditText.setText(moneySource.getMoneySourceName());
                        periodicTransaction.setMoneySourceId(moneySource.getMoneySourceId());
                        periodicTransaction.setMoneySourceName(moneySource.getMoneySourceName());
                        categoryPanel.dismiss();
                    }
                });
            }
        });

        // Nút chọn thời gian
        findViewById(R.id.chooseDateTimeButton_addPeriodicTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                final int mYear = calendar.get(Calendar.YEAR);
                final int mMonth = calendar.get(Calendar.MONTH);
                final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                final int mMin = calendar.get(Calendar.MINUTE);
                final Date date = new Date();
                final DatePickerDialog datePickerDialog;
                final TimePickerDialog timePickerDialog;

                switch (type) {
                    case DAY:
                        timePickerDialog = new TimePickerDialog(AddPeriodicTransaction.this, R.style.TimePickerDialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        date.setHours(hourOfDay);
                                        date.setMinutes(minute);
                                        date.setSeconds(00);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        dateTimeEditText.setText(sdf.format(date.getTime()) + " hằng ngày");
                                        periodicTransaction.setTransactionTime(new Timestamp(date.getTime()));
                                    }
                                }, mHour, mMin, false);
                        timePickerDialog.show();
                        break;
                    case MONTH:
                        datePickerDialog = new DatePickerDialog(AddPeriodicTransaction.this, R.style.DatePickerDialog,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        date.setYear(year - 1900);
                                        date.setMonth(month);
                                        date.setDate(dayOfMonth);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd");
                                        dateTimeEditText.setText(sdf.format(date.getTime()) + " Ngày " + sdf1.format(date.getTime()) + " hằng tháng");
                                        periodicTransaction.setTransactionTime(new Timestamp(date.getTime()));
                                    }
                                }, mYear, mMonth, mDay);

                        timePickerDialog = new TimePickerDialog(AddPeriodicTransaction.this, R.style.TimePickerDialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);
                                        date.setHours(hourOfDay);
                                        date.setMinutes(minute);
                                        date.setSeconds(00);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd");
                                        dateTimeEditText.setText(sdf.format(date.getTime()) + " Ngày " + sdf1.format(date.getTime()) + " hằng tháng");
                                        periodicTransaction.setTransactionTime(new Timestamp(date.getTime()));

                                    }
                                }, mHour, mMin, false);
                        timePickerDialog.show();
                        datePickerDialog.show();
                        break;
                    case YEAR:
                        datePickerDialog = new DatePickerDialog(AddPeriodicTransaction.this, R.style.DatePickerDialog,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        date.setYear(year - 1900);
                                        date.setMonth(month);
                                        date.setDate(dayOfMonth);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM");
                                        dateTimeEditText.setText(sdf.format(date.getTime()) + " Ngày " + sdf1.format(date.getTime()) + " hằng năm");
                                        periodicTransaction.setTransactionTime(new Timestamp(date.getTime()));
                                    }
                                }, mYear, mMonth, mDay);

                        timePickerDialog = new TimePickerDialog(AddPeriodicTransaction.this, R.style.TimePickerDialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);
                                        date.setHours(hourOfDay);
                                        date.setMinutes(minute);
                                        date.setSeconds(00);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM");
                                        dateTimeEditText.setText(sdf.format(date.getTime()) + " Ngày " + sdf1.format(date.getTime()) + " hằng năm");
                                        periodicTransaction.setTransactionTime(new Timestamp(date.getTime()));

                                    }
                                }, mHour, mMin, false);
                        timePickerDialog.show();
                        datePickerDialog.show();
                        break;
                }
            }
        });

        // Nút chọn danh mục
        findViewById(R.id.chooseCategoryButton_addPeriodicTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_choose_category);
                final AlertDialog categoryPanel = builder.create();
                categoryPanel.show();
                categoryPanel.getWindow().setLayout(1000, 1200);
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
                        periodicTransaction.setExpenditureId(expenditures.get(0).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(0).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(0).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                bill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeOfExpenditure.setText(expenditures.get(1).getExpenditureName());
                        periodicTransaction.setExpenditureId(expenditures.get(1).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(1).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(1).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                travel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeOfExpenditure.setText(expenditures.get(2).getExpenditureName());
                        periodicTransaction.setExpenditureId(expenditures.get(2).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(2).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(2).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                health.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeOfExpenditure.setText(expenditures.get(3).getExpenditureName());
                        periodicTransaction.setExpenditureId(expenditures.get(3).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(3).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(3).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                party.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeOfExpenditure.setText(expenditures.get(4).getExpenditureName());
                        periodicTransaction.setExpenditureId(expenditures.get(4).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(4).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(4).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                spendingOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeOfExpenditure.setText(expenditures.get(5).getExpenditureName());
                        periodicTransaction.setExpenditureId(expenditures.get(5).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(5).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(5).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                bonus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeOfExpenditure.setText(expenditures.get(6).getExpenditureName());
                        periodicTransaction.setExpenditureId(expenditures.get(6).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(6).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(6).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                profit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeOfExpenditure.setText(expenditures.get(7).getExpenditureName());
                        periodicTransaction.setExpenditureId(expenditures.get(7).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(7).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(7).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                salary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeOfExpenditure.setText(expenditures.get(8).getExpenditureName());
                        periodicTransaction.setExpenditureId(expenditures.get(8).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(8).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(8).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                gifted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeOfExpenditure.setText(expenditures.get(9).getExpenditureName());
                        periodicTransaction.setExpenditureId(expenditures.get(9).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(9).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(9).isIncome());
                        categoryPanel.dismiss();
                    }
                });
                incomingOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeOfExpenditure.setText(expenditures.get(10).getExpenditureName());
                        periodicTransaction.setExpenditureId(expenditures.get(10).getExpenditureId());
                        periodicTransaction.setExpenditureName(expenditures.get(10).getExpenditureName());
                        periodicTransaction.setTransactionIsIncome(expenditures.get(10).isIncome());
                        categoryPanel.dismiss();
                    }
                });
            }
        });

        // Nút lưu
        findViewById(R.id.save_editUserInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPeriodicTransaction.this);
                builder.setView(R.layout.dialog_one_button);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setLayout(850, 400);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView msg = dialog.findViewById(R.id.message_one_button_dialog);
                dialog.findViewById(R.id.confirm_one_button_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.hide();

                if (moneySourceEditText.getText().toString().length() == 0 || dateTimeEditText.getText().toString().length() == 0
                        || typeOfExpenditure.getText().toString().length() == 0 || amount.getText().toString().length() == 0) {
                    dialog.show();
                    msg.setText("Vui lòng điền dầy đủ thông tin!");
                } else {
                    //Trường hợp đầy đủ thông tin
                    container.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);

                    periodicTransaction.setTransactionAmount(Double.valueOf(amount.getText().toString()));
                    periodicTransaction.setDescription(description.getText().toString());
                    dataHelper.setPeriodicTransaction(new PeriodicTransactionCallBack() {
                                                          @Override
                                                          public void onCallBack(ArrayList<PeriodicTransaction> list) {
                                                              dialog.dismiss();

                                                              Intent data = new Intent();
                                                              data.putExtra("periodicTransaction", list.get(0));
                                                              setResult(Activity.RESULT_OK, data);
                                                              Toast.makeText(AddPeriodicTransaction.this, "Thêm giao dịch định kỳ thành công", Toast.LENGTH_LONG).show();
                                                              finish();
                                                          }

                                                          @Override
                                                          public void onCallBackFail(String message) {
                                                              container.setVisibility(View.VISIBLE);
                                                              loading.setVisibility(View.GONE);
                                                              Toast.makeText(AddPeriodicTransaction.this, "Thêm giao dịch định kỳ thất bại", Toast.LENGTH_LONG).show();
                                                          }
                                                      },
                            periodicTransaction.getDescription(), periodicTransaction.getExpenditureId(),
                            periodicTransaction.getExpenditureName(), periodicTransaction.getTransactionAmount(), periodicTransaction.getMoneySourceId(), periodicTransaction.getMoneySourceName(),
                            periodicTransaction.getTransactionIsIncome(), periodicTransaction.getTransactionTime(), periodicTransaction.getPeriodicType());
                }
            }
        });
    }
}