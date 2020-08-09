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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PeriodicTransactionDetails extends AppCompatActivity {
    private PeriodicTransaction periodicTransaction;
    private Timestamp timestamp;
    private LinearLayout food, bill, travel, health, party, spendingOther, bonus, profit, salary, gifted, incomingOther;
    Spinner periodMenu;
    private boolean isChange = false;

    enum TYPE {
        DAY,
        MONTH,
        YEAR
    }

    TYPE type;

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

        setContentView(R.layout.activity_periodic_transaction_details);
        final Button saveOrEditButton = findViewById(R.id.editAndSave_periodicTransactionDetails);
        final Button chooseDateTimeButton = findViewById(R.id.chooseDateTime_periodicTransactionDetails);
        final EditText dateTime = findViewById(R.id.dateTime_periodicTransactionDetails);
        final EditText description = findViewById(R.id.description_periodicTransactionDetails);
        final EditText amount = findViewById(R.id.amount_periodicTransactionDetails);
        final EditText moneySourceName = findViewById(R.id.moneySource_periodicTransactionDetails);
        final TextView expenditureName = findViewById(R.id.expenditureName);
        final ImageView icon = findViewById(R.id.transactionIcon_periodicTransactionDetails);

        Intent data = getIntent();
        periodicTransaction = (PeriodicTransaction) data.getParcelableExtra("periodicTransaction");

        SimpleDateFormat sdf_day = new SimpleDateFormat("hh:mm");
        SimpleDateFormat sdf_month = new SimpleDateFormat("dd");
        SimpleDateFormat sdf_year = new SimpleDateFormat("dd/MM");
        final MoneyToStringConverter converter = new MoneyToStringConverter();
        final ExpenditureList expList = new ExpenditureList();
        String iconString = expList.getIcon(periodicTransaction.getExpenditureId());
        int id = getResources().getIdentifier("com.example.moneymanager:drawable/" + iconString, null, null);
        Date date = new Date(periodicTransaction.getTransactionTime().getTime());

        description.setText(periodicTransaction.getDescription());
        amount.setText(converter.moneyToString(periodicTransaction.getTransactionAmount().doubleValue()));
        moneySourceName.setText(periodicTransaction.getMoneySourceName());
        expenditureName.setText(periodicTransaction.getExpenditureName());
        icon.setImageResource(id);

        // Nút back
        findViewById(R.id.backButton_periodicTransactionDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isChange){
                    Intent data = new Intent();
                    data.putExtra("periodicTransaction", periodicTransaction);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            }
        });

        // Nút xóa
        findViewById(R.id.delete_periodicTransactionDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo dialog
                LayoutInflater factory = LayoutInflater.from(PeriodicTransactionDetails.this);
                View DialogView = factory.inflate(R.layout.dialog_two_buttons, null);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
                dialogBuilder.setView(DialogView);
                final AlertDialog alertDialog = dialogBuilder.create();

                ((TextView) DialogView.findViewById(R.id.message_two_button_dialog)).setText("Bạn có chắc muốn xóa giao dịch này ?");
                DialogView.findViewById(R.id.confirm_two_button_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        Intent data = new Intent();
                        data.putExtra("periodicTransaction", periodicTransaction);
                        setResult(Activity.RESULT_FIRST_USER, data);
                        finish();
                    }
                });

                DialogView.findViewById(R.id.cancel_two_button_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
                alertDialog.getWindow().setLayout(850,450);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });

        // Nút chọn ngày
        chooseDateTimeButton.setOnClickListener(new View.OnClickListener() {
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
                        timePickerDialog = new TimePickerDialog(PeriodicTransactionDetails.this, R.style.TimePickerDialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        date.setHours(hourOfDay);
                                        date.setMinutes(minute);
                                        date.setSeconds(00);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        dateTime.setText(sdf.format(date.getTime()) + " hằng ngày");
                                        periodicTransaction.setTransactionTime(new Timestamp(date.getTime()));
                                    }
                                }, mHour, mMin, false);
                        timePickerDialog.show();
                        break;
                    case MONTH:
                        datePickerDialog = new DatePickerDialog(PeriodicTransactionDetails.this, R.style.DatePickerDialog,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        date.setYear(year - 1900);
                                        date.setMonth(month);
                                        date.setDate(dayOfMonth);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd");
                                        dateTime.setText(sdf.format(date.getTime()) + " Ngày " + sdf1.format(date.getTime()) + " hằng tháng");
                                        periodicTransaction.setTransactionTime(new Timestamp(date.getTime()));
                                    }
                                }, mYear, mMonth, mDay);

                        timePickerDialog = new TimePickerDialog(PeriodicTransactionDetails.this, R.style.TimePickerDialog,
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
                                        dateTime.setText(sdf.format(date.getTime()) + " Ngày " + sdf1.format(date.getTime()) + " hằng tháng");
                                        periodicTransaction.setTransactionTime(new Timestamp(date.getTime()));

                                    }
                                }, mHour, mMin, false);
                        timePickerDialog.show();
                        datePickerDialog.show();
                        break;
                    case YEAR:
                        datePickerDialog = new DatePickerDialog(PeriodicTransactionDetails.this, R.style.DatePickerDialog,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        date.setYear(year - 1900);
                                        date.setMonth(month);
                                        date.setDate(dayOfMonth);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM");
                                        dateTime.setText(sdf.format(date.getTime()) + " Ngày " + sdf1.format(date.getTime()) + " hằng năm");
                                        periodicTransaction.setTransactionTime(new Timestamp(date.getTime()));
                                    }
                                }, mYear, mMonth, mDay);

                        timePickerDialog = new TimePickerDialog(PeriodicTransactionDetails.this, R.style.TimePickerDialog,
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
                                        dateTime.setText(sdf.format(date.getTime()) + " Ngày " + sdf1.format(date.getTime()) + " hằng năm");
                                        periodicTransaction.setTransactionTime(new Timestamp(date.getTime()));

                                    }
                                }, mHour, mMin, false);
                        timePickerDialog.show();
                        datePickerDialog.show();
                        break;
                }
            }
        });

        //Period menu initiation
        periodMenu = findViewById(R.id.period_periodicTransactionDetails);
        String[] periodMenuItems = new String[]{"Ngày","Tháng", "Năm"};

        ArrayAdapter<String> periodAdapter = new ArrayAdapter<String>(this, R.layout.spinner_menu, periodMenuItems);
        periodMenu.setAdapter(periodAdapter);

        if(periodicTransaction.getPeriodicType().equals("day")) {
            periodMenu.setSelection(0);
            type = TYPE.DAY;
            dateTime.setText(sdf_day.format(date.getTime()) + " hằng ngày");
        } else if(periodicTransaction.getPeriodicType().equals("month")) {
            periodMenu.setSelection(1);
            type = TYPE.MONTH;
            dateTime.setText(sdf_day.format(date.getTime()) + " Ngày " + sdf_month.format(date) + " hằng tháng");
        } else if(periodicTransaction.getPeriodicType().equals("year")) {
            periodMenu.setSelection(2);
            type = TYPE.MONTH;
            dateTime.setText(sdf_day.format(date.getTime()) + " Ngày " + sdf_year.format(date) + " hằng năm");
        }

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                                    periodicTransaction.setExpenditureId(expenditure.getExpenditureId());
                                    periodicTransaction.setExpenditureName(expenditure.getExpenditureName());
                                    periodicTransaction.setTransactionIsIncome(expenditure.isIncome());

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
                    periodMenu.setEnabled(true);

                    saveOrEditButton.setText("Lưu");

                }
                else
                {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            0,LinearLayout.LayoutParams.MATCH_PARENT,4.0f);
                    //Tạo dialog để thông báo lỗi
                    AlertDialog.Builder builder = new AlertDialog.Builder(PeriodicTransactionDetails.this);
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
                        dialog.dismiss();
                        isChange = true;
                        periodicTransaction.setTransactionAmount(converter.stringToMoney(amount.getText().toString()));
                        amount.setText(converter.moneyToString(periodicTransaction.getTransactionAmount().doubleValue()));
                        periodicTransaction.setDescription(description.getText().toString());

                        icon.setOnClickListener(null);

                        amount.setEnabled(false);
                        dateTime.setLayoutParams(param);
                        chooseDateTimeButton.setVisibility(View.GONE);
                        description.setEnabled(false);
                        periodMenu.setEnabled(false);

                        saveOrEditButton.setText("Sửa");
                    }
                }

            }
        });
    }
}