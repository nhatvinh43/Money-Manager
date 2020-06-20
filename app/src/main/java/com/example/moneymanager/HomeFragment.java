package com.example.moneymanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Calendar task
    TextView dayOfWeek;
    TextView dateHome;
    TextView dateArrow;
    int mYear, mMonth, mDay;

    // Money source infomation
    TextView moneySourceLimit;
    TextView todayIncome;
    TextView todaySpending;

    // Money source
    RecyclerView moneySourceRecycleView;
    ArrayList<MoneySource> moneySourceList;
    MoneySource selectedMoneySource;
    HomeMoneySourceAdapter moneySourceAdapter;

    // Transaction
    RecyclerView transactionRecycleView;
    ArrayList<Transaction> transactionList;
    HomeTransactionAdapter transactionAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Sample Test Data
        moneySourceList = new ArrayList<>();
        moneySourceList.add(new MoneySource(10000.0, "a","VND",500000.0, "a", "Ví chung", "1"));
        moneySourceList.add(new MoneySource(130000.0, "a","VND",600000.0, "b", "Ví ăn vặt", "1"));
        moneySourceList.add(new MoneySource(140000.0, "a","VND",1000000.0, "c", "Ví tiết kiệm", "1"));

//        transactionList.add(new Transaction("vui", "1", "Tiền lương", 1000000, "a", "MS01",true, new Timestamp(100000)));
//        transactionList.add(new Transaction("vui quá", "2", "Tiền điện", 120000, "b", "MS01",false, new Timestamp(200000)));
//        transactionList.add(new Transaction("vui à", "3", "Tiền uống", 100000, "c","MS01", false, new Timestamp(300000)));
//        transactionList.add(new Transaction("vui ghê", "1", "Tiền lương", 3000000, "d","MS01", true, new Timestamp(450000)));
//        transactionList.add(new Transaction("vui bla", "4", "Tiền ăn", 650000, "e","MS01", false, new Timestamp(678000)));

        ArrayList<Transaction> transactionList1 = new ArrayList<>();
        transactionList1.add(new Transaction("vui à", "3", "Tiền uống", 100000.0, "c","MS01", false, new Timestamp(Calendar.getInstance().get(Calendar.MILLISECOND))));
        transactionList1.add(new Transaction("vui ghê", "1", "Tiền lương", 3000000.0, "d","MS01", true, new Timestamp(Calendar.getInstance().get(Calendar.MILLISECOND) - 60*60*24*1000)));
        transactionList1.add(new Transaction("vui bla", "4", "Tiền ăn", 650000.0, "e","MS01", false, new Timestamp(Calendar.getInstance().get(Calendar.MILLISECOND))));
        ArrayList<Transaction> transactionList2 = new ArrayList<>();
        transactionList2.add(new Transaction("vui à", "3", "Tiền uống", 100000.0, "c","MS01", false, new Timestamp(Calendar.getInstance().get(Calendar.MILLISECOND))));
        transactionList2.add(new Transaction("vui ghê", "1", "Tiền lương", 3000000.0, "d","MS01", true, new Timestamp(Calendar.getInstance().get(Calendar.MILLISECOND))));
        ArrayList<Transaction> transactionList3 = new ArrayList<>();
        transactionList3.add(new Transaction("vui à", "3", "Tiền uống", 100000.0, "c","MS01", false, new Timestamp(Calendar.getInstance().get(Calendar.MILLISECOND))));

        moneySourceList.get(0).setTransactionsList(transactionList1);
        moneySourceList.get(1).setTransactionsList(transactionList2);
        moneySourceList.get(2).setTransactionsList(transactionList3);

        selectedMoneySource = moneySourceList.get(0);

        // Moneysource Info Initiation
        moneySourceLimit = view.findViewById(R.id.moneyLimit_home);
        todayIncome = view.findViewById(R.id.todayIncome_home);
        todaySpending = view.findViewById(R.id.todaySpending_home);

        moneySourceLimit.setText(moneyToString((double)selectedMoneySource.getLimit()));
        todayIncome.setText("Thêm sau");
        todaySpending.setText("Thêm sau");

        // Calendar Initiation
        dayOfWeek = view.findViewById(R.id.weekDay_home);
        dateHome = view.findViewById(R.id.date_home);
        dateArrow = view.findViewById(R.id.dateArrow_home);

        String myFormat = "dd/MM/yyyy";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        final Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        dateHome.setText(sdf.format(mcurrentDate.getTime()));
        dayOfWeek.setText("Today");

        dateArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                       Calendar myCalendar = Calendar.getInstance();
                       myCalendar.set(Calendar.YEAR, i);
                       myCalendar.set(Calendar.MONTH, i1);
                       myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                       dateHome.setText(sdf.format(myCalendar.getTime()));

                       if (DateUtils.isToday(myCalendar.getTimeInMillis())) {
                           dayOfWeek.setText("Today");
                       } else {
                           dayOfWeek.setText(getDayOfWeek(myCalendar.get(Calendar.DAY_OF_WEEK)));
                       }

                       mYear = myCalendar.get(Calendar.YEAR);
                       mMonth = myCalendar.get(Calendar.MONTH);
                       mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

                       transactionList.clear();
                       transactionList.addAll(modifierTransactionListByDate());
                       transactionAdapter.notifyDataSetChanged();
                   }
               };

               new DatePickerDialog(getContext(), R.style.MyDatePickerDialogTheme, date, mYear, mMonth, mDay).show();
            }
        });

        // Moneysource RecycleView Initiation
        moneySourceRecycleView = view.findViewById(R.id.moneySourceList);

        final LinearLayoutManager moneySourceLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        moneySourceRecycleView.setLayoutManager(moneySourceLayoutManager);
        moneySourceRecycleView.setItemAnimator(new DefaultItemAnimator());

        final SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(moneySourceRecycleView);

        moneySourceAdapter = new HomeMoneySourceAdapter(moneySourceList, getContext());
        moneySourceRecycleView.setAdapter(moneySourceAdapter);
        moneySourceRecycleView.setClipToPadding(false);
        final int activeColor = ContextCompat.getColor(view.getContext(), R.color.white);
        final int inactiveColor = ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark);
        moneySourceRecycleView.addItemDecoration(new DotsIndicatorDecoration(9,20,100,inactiveColor,activeColor));

        moneySourceRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View v = snapHelper.findSnapView(moneySourceLayoutManager);
                int pos = moneySourceLayoutManager.getPosition(v);

                selectedMoneySource = moneySourceList.get(pos);
                moneySourceLimit.setText(moneyToString((double)selectedMoneySource.getLimit()));
                todayIncome.setText("Thêm sau");
                todaySpending.setText("Thêm sau");

                transactionList.clear();
                transactionList.addAll(modifierTransactionListByDate());
                transactionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        // Transaction RecycleView Initiation
        transactionRecycleView = view.findViewById(R.id.transactionList);
        transactionList = new ArrayList<>();
        transactionList.addAll(modifierTransactionListByDate());

        GridLayoutManager transactionLayoutManager = new GridLayoutManager(getContext(), 2);
        transactionRecycleView.setLayoutManager(transactionLayoutManager);

        transactionAdapter = new HomeTransactionAdapter(transactionList, getContext());
        transactionRecycleView.setAdapter(transactionAdapter);
        transactionRecycleView.addItemDecoration(new SpaceItemDecoration(2,30,false));
    }

    private String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        return day;
    }

    private ArrayList<Transaction> modifierTransactionListByDate() {
        ArrayList<Transaction> modifierTransactionList = new ArrayList<>();
        for(Transaction t : selectedMoneySource.getTransactionsList()) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MILLISECOND, (int)t.getTransactionTime().getTime());
            int transactionDay = c.get(Calendar.DAY_OF_MONTH);
            int transactionMonth = c.get(Calendar.MONTH);
            int transactionYeah = c.get(Calendar.YEAR);

            if(transactionDay == mDay && transactionMonth == mMonth && transactionYeah == mYear) modifierTransactionList.add(t);
        }
        return modifierTransactionList;
    }

    private String moneyToString(double amount) {
        StringBuilder mString = new StringBuilder();
        long mAmount = (long)amount;
        double remainder = amount - mAmount;
        int count = 0;
        while(mAmount > 0) {
            mString.insert(0, Long.toString(Math.floorMod(mAmount, 10)));
            mAmount /= 10;
            count++;

            if(count == 3 && mAmount != 0) {
                mString.insert(0, ",");
                count = 0;
            }
        }

        String decimal =  "";
        if (remainder > 0)
            decimal = String.valueOf(remainder).substring(String.valueOf(remainder).indexOf("."));

        return mString.toString() + decimal;
    }
}
