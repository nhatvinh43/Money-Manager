package com.example.moneymanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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

import android.os.Handler;
import android.text.Editable;
import android.text.PrecomputedText;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Result;

import me.itangqi.waveloadingview.WaveLoadingView;

import static android.content.ContentValues.TAG;
import static android.content.Context.ACCESSIBILITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

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
    public static final int HOME_NEW_TRANSACTION_RQCODE = 2;
    public static final int HOME_CHANGE_TRANSACTION_RQCODE = 3;
    public static final int HOME_NEW_MONEYSOURCE_RQCODE = 4;
    public static final int HOME_CHANGE_MONEYSOURCE_RQCODE = 5;
    ViewMode viewMode = ViewMode.DAY;
    Calendar fromCal = Calendar.getInstance();
    Calendar toCal = Calendar.getInstance();

    enum ViewMode {
        DAY,
        WEEK,
        MONTH,
        QUARTER,
        YEAR,
        MANUAL
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Firebase
    FirebaseAuth fAuth;
    DataHelper fDataHelper;

    // Calendar task
    TextView dayOfWeek;
    TextView dateHome;
    TextView dateArrow;
    int mYear, mMonth, mDay;

    // Money source infomation
    TextView todayIncome;
    TextView todaySpending;
    WaveLoadingView waveLoadingView; // For money source limit

    // Money source
    RecyclerView moneySourceRecycleView;
    ArrayList<MoneySource> moneySourceList;
    MoneySource selectedMoneySource;
    HomeMoneySourceAdapter moneySourceAdapter;

    // Transaction
    RecyclerView transactionRecycleView;
    ArrayList<Transaction> transactionList;
    HomeTransactionAdapter transactionAdapter;
    LayoutAnimationController aController;
    LinearLayout transactionsSection;
    LinearLayout noTransactionsSection;


    // Filter and Sort
    Spinner sortMenu;
    Spinner filterMenu;
    EditText search;

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
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar loading = view.findViewById(R.id.loading);
        final RelativeLayout rl = view.findViewById(R.id.homeContainer);

        fAuth = FirebaseAuth.getInstance();
        fDataHelper = new DataHelper();
        moneySourceList = new ArrayList<>();
        transactionList = new ArrayList<>();
        selectedMoneySource = new MoneySource();

        fDataHelper.getMoneySource(new MoneySourceCallBack() {
            @Override
            public void onCallBack(ArrayList<MoneySource> list) {
                getMoneySourceData(list);
                loading.setVisibility(View.GONE);
                rl.setVisibility(View.VISIBLE);
                initView(view);
            }

            @Override
            public void onCallBackFail(String message) {
                Log.d(TAG, "Fail: " + message);
            }
        }, fAuth.getCurrentUser().getUid());
    }

    private void getMoneySourceData(ArrayList<MoneySource> list) {
        moneySourceList.clear();
        moneySourceList.addAll(list);
        selectedMoneySource = moneySourceList.get(0);
    }

    private void initView(View view) {
        final MoneyToStringConverter converter = new MoneyToStringConverter();

        transactionsSection = view.findViewById(R.id.transactions_home);
        noTransactionsSection = view.findViewById(R.id.noTodayTransaction_home);


        // Moneysource Info Initiation
        waveLoadingView = view.findViewById(R.id.waveLoadingView);
        todayIncome = view.findViewById(R.id.todayIncome_home);
        todaySpending = view.findViewById(R.id.todaySpending_home);

        // Calendar Initiation
        dayOfWeek = view.findViewById(R.id.weekDay_home);
        dateHome = view.findViewById(R.id.date_home);
        dateArrow = view.findViewById(R.id.dateArrow_home);

        SharedPreferences prefs = ((MainActivity)getActivity()).getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String myFormat = prefs.getString("currentDate", "dd/MM/yyyy");

        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CHINA);

        final Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        dateHome.setText(sdf.format(mcurrentDate.getTime()));
        dayOfWeek.setText("Hôm nay");


        //"View by" menu initiation
        Spinner viewByMenu = view.findViewById(R.id.viewBy_home);
        String[] viewByMenuItems = new String[]{"Ngày", "Tuần", "Tháng", "Quý", "Năm", "Chọn"};

        ArrayAdapter<String> viewByAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_menu, viewByMenuItems);
        viewByMenu.setAdapter(viewByAdapter);
        //NOTE: file layout của dialog chọn khoảng thời gian là dialog_choose_time.xml
        viewByMenu.setSelection(0);
        viewByMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortMenu.setSelection(0);
                filterMenu.setSelection(0);

                Calendar myCalendar = Calendar.getInstance();
                mYear = myCalendar.get(Calendar.YEAR);
                mMonth = myCalendar.get(Calendar.MONTH);
                mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

                String selectedItem = parent.getItemAtPosition(position).toString();
                switch (selectedItem)
                {
                    case "Ngày":
                    {
                        dateHome.setText(sdf.format(myCalendar.getTime()));
                        dayOfWeek.setText("Hôm nay");

                        viewMode = ViewMode.DAY;
                        transactionList.clear();
                        transactionList.addAll(modifierTransactionListByViewMode());
                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();

                        search.setText("");
                        break;
                    }
                    case "Tuần":
                    {
                        viewMode = ViewMode.WEEK;

                        search.setText("");
                        break;
                    }
                    case "Tháng":
                    {
                        SimpleDateFormat sdf_month = new SimpleDateFormat("MM/yyyy");
                        dateHome.setText(sdf_month.format(myCalendar.getTime()));
                        dayOfWeek.setText("Tháng này");

                        viewMode = ViewMode.MONTH;
                        transactionList.clear();
                        transactionList.addAll(modifierTransactionListByViewMode());
                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();

                        search.setText("");
                        break;
                    }
                    case "Quý":
                    {
                        viewMode = ViewMode.QUARTER;

                        search.setText("");
                        break;
                    }
                    case "Năm":
                    {
                        SimpleDateFormat sdf_month = new SimpleDateFormat("yyyy");
                        dateHome.setText(sdf_month.format(myCalendar.getTime()));
                        dayOfWeek.setText("Năm nay");

                        viewMode = ViewMode.YEAR;
                        transactionList.clear();
                        transactionList.addAll(modifierTransactionListByViewMode());
                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();

                        search.setText("");
                        break;
                    }
                    case "Chọn":
                    {
                        viewMode = ViewMode.MANUAL;
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(R.layout.dialog_choose_time);
                        final AlertDialog chooseTimeDialog  = builder.create();
                        chooseTimeDialog.show();
                        chooseTimeDialog.getWindow().setLayout(1000,1200);
                        chooseTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        final TextView fromTV = chooseTimeDialog.findViewById(R.id.start_choose_time);
                        final TextView toTV = chooseTimeDialog.findViewById(R.id.end_choose_time);

                        // Chọn ngày bắt đầu
                        chooseTimeDialog.findViewById(R.id.chooseStart_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.set(Calendar.YEAR, i);
                                        myCalendar.set(Calendar.MONTH, i1);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                                        fromTV.setText(sdf.format(myCalendar.getTime()));
                                        fromCal = myCalendar;
                                    }
                                };
                                new DatePickerDialog(getContext(), R.style.DatePickerDialog, date, mYear, mMonth, mDay).show();
                            }
                        });

                        // Chọn ngày kết thúc
                        chooseTimeDialog.findViewById(R.id.chooseEnd_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.set(Calendar.YEAR, i);
                                        myCalendar.set(Calendar.MONTH, i1);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                                        toTV.setText(sdf.format(myCalendar.getTime()));
                                        toCal = myCalendar;
                                    }
                                };

                                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DatePickerDialog, date, mYear, mMonth, mDay);
                                datePickerDialog.getDatePicker().setMinDate(fromCal.getTimeInMillis() - 1000);
                                datePickerDialog.show();
                            }
                        });

                        chooseTimeDialog.findViewById(R.id.confirm_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dayOfWeek.setText("Mốc thời gian");
                                dateHome.setText(sdf.format(fromCal.getTime()) + " - " + sdf.format(toCal.getTime()));

                                transactionList.clear();
                                transactionList.addAll(modifierTransactionListByViewMode());
                                transactionAdapter.notifyDataSetChanged();
                                transactionRecycleView.scheduleLayoutAnimation();
                                chooseTimeDialog.dismiss();
                            }
                        });

                        search.setText("");
                        break;
                    }
                }

                todayIncome.setText(getTodayIncome());
                todaySpending.setText(getTodaySpending());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        View.OnClickListener dateSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortMenu.setSelection(0);
                filterMenu.setSelection(0);
                switch(viewMode)
                {
                    case DAY:
                    {
                        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                Calendar myCalendar = Calendar.getInstance();
                                myCalendar.set(Calendar.YEAR, i);
                                myCalendar.set(Calendar.MONTH, i1);
                                myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                                dateHome.setText(sdf.format(myCalendar.getTime()));

                                if (DateUtils.isToday(myCalendar.getTimeInMillis())) {
                                    dayOfWeek.setText("Hôm nay");
                                } else {
                                    dayOfWeek.setText(getDayOfWeek(myCalendar.get(Calendar.DAY_OF_WEEK)));
                                }

                                mYear = myCalendar.get(Calendar.YEAR);
                                mMonth = myCalendar.get(Calendar.MONTH);
                                mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

                                transactionList.clear();
                                transactionList.addAll(modifierTransactionListByViewMode());
                                renderError(transactionList, transactionsSection, noTransactionsSection);
                                transactionAdapter.notifyDataSetChanged();
                                transactionRecycleView.scheduleLayoutAnimation();

                                todayIncome.setText(getTodayIncome());
                                todaySpending.setText(getTodaySpending());
                            }
                        };
                        new DatePickerDialog(getContext(), R.style.DatePickerDialog, date, mYear, mMonth, mDay).show();
                        break;
                    }
                    case WEEK:
                    {

                        break;
                    }
                    case MONTH:
                    {
                        Calendar today = Calendar.getInstance();
                        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                                new MonthPickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(int selectedMonth, int selectedYear)
                                    {
                                        SimpleDateFormat sdf_month = new SimpleDateFormat("MM/yyyy");
                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.set(Calendar.YEAR, selectedYear);
                                        myCalendar.set(Calendar.MONTH, selectedMonth);
                                        dateHome.setText(sdf_month.format(myCalendar.getTime()));

                                        if(selectedMonth == Calendar.getInstance().get(Calendar.MONTH) && selectedYear == Calendar.getInstance().get(Calendar.YEAR)) {
                                            dayOfWeek.setText("Tháng này");
                                        } else {
                                            dayOfWeek.setText("Tháng " + (selectedMonth + 1));
                                        }

                                        mYear = myCalendar.get(Calendar.YEAR);
                                        mMonth = myCalendar.get(Calendar.MONTH);

                                        transactionList.clear();
                                        transactionList.addAll(modifierTransactionListByViewMode());
                                        renderError(transactionList, transactionsSection, noTransactionsSection);
                                        transactionAdapter.notifyDataSetChanged();
                                        transactionRecycleView.scheduleLayoutAnimation();

                                        todayIncome.setText(getTodayIncome());
                                        todaySpending.setText(getTodaySpending());
                                    }
                                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                        builder.setActivatedMonth(mMonth)
                                .setMinYear(2010)
                                .setActivatedYear(mYear)
                                .setMaxYear(2099)
                                .setMinMonth(Calendar.JANUARY)
                                .setTitle("Chọn tháng")
                                .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                                    @Override
                                    public void onMonthChanged(int selectedMonth) {

                                    }
                                })
                                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                                    @Override
                                    public void onYearChanged(int selectedYear) {

                                    }
                                }).build().show();
                        break;
                    }
                    case QUARTER:
                    {
                        break;
                    }
                    case YEAR:
                    {
                        Calendar today = Calendar.getInstance();
                        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                                new MonthPickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(int selectedMonth, int selectedYear)
                                    {
                                        SimpleDateFormat sdf_month = new SimpleDateFormat("yyyy");
                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.set(Calendar.YEAR, selectedYear);
                                        dateHome.setText(sdf_month.format(myCalendar.getTime()));

                                        if(selectedYear == Calendar.getInstance().get(Calendar.YEAR)) {
                                            dayOfWeek.setText("Năm nay");
                                        } else {
                                            dayOfWeek.setText("Năm " + selectedYear);
                                        }

                                        mYear = myCalendar.get(Calendar.YEAR);

                                        transactionList.clear();
                                        transactionList.addAll(modifierTransactionListByViewMode());
                                        renderError(transactionList, transactionsSection, noTransactionsSection);
                                        transactionAdapter.notifyDataSetChanged();
                                        transactionRecycleView.scheduleLayoutAnimation();

                                        todayIncome.setText(getTodayIncome());
                                        todaySpending.setText(getTodaySpending());
                                    }
                                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                        builder.setActivatedMonth(mMonth)
                                .setMinYear(2010)
                                .setActivatedYear(mYear)
                                .setMaxYear(2099)
                                .setMinMonth(Calendar.JANUARY)
                                .setTitle("Chọn năm")
                                .showYearOnly()
                                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                                    @Override
                                    public void onMonthChanged(int selectedMonth) {

                                    }
                                })
                                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                                    @Override
                                    public void onYearChanged(int selectedYear) {

                                    }
                                }).build().show();
                        break;
                    }
                    case MANUAL:
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(R.layout.dialog_choose_time);
                        final AlertDialog chooseTimeDialog  = builder.create();
                        chooseTimeDialog.show();
                        chooseTimeDialog.getWindow().setLayout(1000,1200);
                        chooseTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        final TextView fromTV = chooseTimeDialog.findViewById(R.id.start_choose_time);
                        final TextView toTV = chooseTimeDialog.findViewById(R.id.end_choose_time);
                        fromTV.setText(sdf.format(fromCal.getTime()));
                        toTV.setText(sdf.format(toCal.getTime()));

                        // Chọn ngày bắt đầu
                        chooseTimeDialog.findViewById(R.id.chooseStart_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.set(Calendar.YEAR, i);
                                        myCalendar.set(Calendar.MONTH, i1);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                                        fromTV.setText(sdf.format(myCalendar.getTime()));
                                        fromCal = myCalendar;
                                    }
                                };
                                new DatePickerDialog(getContext(), R.style.DatePickerDialog, date, fromCal.get(Calendar.YEAR), fromCal.get(Calendar.MONTH), fromCal.get(Calendar.DAY_OF_MONTH)).show();
                            }
                        });

                        // Chọn ngày kết thúc
                        chooseTimeDialog.findViewById(R.id.chooseEnd_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.set(Calendar.YEAR, i);
                                        myCalendar.set(Calendar.MONTH, i1);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                                        toTV.setText(sdf.format(myCalendar.getTime()));
                                        toCal = myCalendar;
                                    }
                                };

                                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DatePickerDialog, date, toCal.get(Calendar.YEAR), toCal.get(Calendar.MONTH), toCal.get(Calendar.DAY_OF_MONTH));
                                datePickerDialog.getDatePicker().setMinDate(fromCal.getTimeInMillis() - 1000);
                                datePickerDialog.show();
                            }
                        });

                        chooseTimeDialog.findViewById(R.id.confirm_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dateHome.setText(sdf.format(fromCal.getTime()) + " - " + sdf.format(toCal.getTime()));

                                transactionList.clear();
                                transactionList.addAll(modifierTransactionListByViewMode());
                                renderError(transactionList, transactionsSection, noTransactionsSection);
                                transactionAdapter.notifyDataSetChanged();
                                transactionRecycleView.scheduleLayoutAnimation();

                                todayIncome.setText(getTodayIncome());
                                todaySpending.setText(getTodaySpending());
                                chooseTimeDialog.dismiss();
                            }
                        });
                        break;
                    }
                }

                search.setText("");
            }
        };


        dateArrow.setOnClickListener(dateSelector);
        dayOfWeek.setOnClickListener(dateSelector);
        dateHome.setOnClickListener(dateSelector);

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

        moneySourceAdapter.setOnItemClickListener(new HomeMoneySourceAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                MoneySource ms = moneySourceList.get(position);
                Log.d("----------------------- Id moneysource",position + "    " + ms.getMoneySourceId());
                Intent transactionDetailIntent = new Intent(getContext(), MoneySourceDetailsActivity.class);
                transactionDetailIntent.putExtra("MoneySourceId", ms.getMoneySourceId());
                getActivity().startActivityForResult(transactionDetailIntent, HOME_CHANGE_MONEYSOURCE_RQCODE);
            }
        });

        // Money source item animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder viewHolder = moneySourceRecycleView.findViewHolderForAdapterPosition(0);
                CardView cv = viewHolder.itemView.findViewById(R.id.cardContainer);
                cv.animate().setDuration(100).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
            }
        }, 100);

        moneySourceRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    return;
                }

                View v = snapHelper.findSnapView(moneySourceLayoutManager);
                int pos = moneySourceLayoutManager.getPosition(v);

                RecyclerView.ViewHolder viewHolder = moneySourceRecycleView.findViewHolderForAdapterPosition(pos);
                CardView cv = viewHolder.itemView.findViewById(R.id.cardContainer);

                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    cv.animate().setDuration(300).scaleX(1).scaleY(1).setInterpolator(new DecelerateInterpolator()).start();
                } else {
                    cv.animate().setDuration(300).scaleX(0.9f).scaleY(0.9f).setInterpolator(new AccelerateInterpolator()).start();
                }

                if(newState!=RecyclerView.SCROLL_STATE_SETTLING)
                {
                    sortMenu.setSelection(0);
                    filterMenu.setSelection(0);

                    selectedMoneySource = moneySourceList.get(pos);

                    waveLoadingView.setCenterTitle(converter.moneyToString((double)selectedMoneySource.getLimit()));
                    if(selectedMoneySource.getLimit().doubleValue() != 0) {
                        int percent = (int) ((selectedMoneySource.getAmount().doubleValue() / selectedMoneySource.getLimit().doubleValue()) * 100);
                        if (percent > 100) percent = 100;
                        waveLoadingView.setProgressValue(percent);
                    } else {
                        waveLoadingView.setProgressValue(0);
                    }

                    transactionList.clear();
                    transactionList.addAll(modifierTransactionListByViewMode());
                    renderError(transactionList, transactionsSection, noTransactionsSection);
                    transactionAdapter.notifyDataSetChanged();
                    transactionRecycleView.scheduleLayoutAnimation();

                    todayIncome.setText(getTodayIncome());
                    todaySpending.setText(getTodaySpending());
                    search.setText("");
                }


            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        // Transaction RecycleView Initiation
        transactionRecycleView = view.findViewById(R.id.transactionList);
        transactionList.addAll(modifierTransactionListByViewMode());

        waveLoadingView.setCenterTitle(converter.moneyToString((double)selectedMoneySource.getLimit()));
        if(selectedMoneySource.getLimit().doubleValue() != 0) {
            int percent = (int) ((selectedMoneySource.getAmount().doubleValue() / selectedMoneySource.getLimit().doubleValue()) * 100);
            if (percent > 100) percent = 100;
            waveLoadingView.setProgressValue(percent);
        } else {
            waveLoadingView.setProgressValue(1);
        }
        todayIncome.setText(getTodayIncome());
        todaySpending.setText(getTodaySpending());

        GridLayoutManager transactionLayoutManager = new GridLayoutManager(getContext(), 2);
        transactionRecycleView.setLayoutManager(transactionLayoutManager);

        transactionAdapter = new HomeTransactionAdapter(transactionList, getContext());
        transactionRecycleView.setAdapter(transactionAdapter);
        transactionRecycleView.addItemDecoration(new SpaceItemDecoration(2,30,false));

        aController = AnimationUtils.loadLayoutAnimation(transactionRecycleView.getContext(), R.anim.layout_fall_down);
        transactionRecycleView.setLayoutAnimation(aController);
        transactionRecycleView.scheduleLayoutAnimation();
        transactionAdapter.notifyDataSetChanged();

        transactionAdapter.setOnItemClickListener(new HomeTransactionAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Transaction trans = transactionList.get(position);
                Log.d("----------------------- Id transation ",position + "    " + trans.getTransactionId());
                Intent transactionDetailIntent = new Intent(getContext(), TransactionDetailsActivity.class);
                transactionDetailIntent.putExtra("TransactionId", trans.getTransactionId());
                startActivityForResult(transactionDetailIntent, HOME_CHANGE_TRANSACTION_RQCODE);
            }
        });


        renderError(transactionList, transactionsSection, noTransactionsSection);

        // Search initiation
        search = view.findViewById(R.id.search_home);
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


        //Filter menu initiation
        sortMenu = view.findViewById(R.id.sort_home);
        filterMenu = view.findViewById(R.id.filter_home);
        String[] filterMenuItems = new String[]{"Tất cả", "Thu nhập", "Chi tiêu", "Định kỳ"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_menu, filterMenuItems);
        filterMenu.setAdapter(filterAdapter);
        filterMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortMenu.setSelection(0);
                String selectedItem = parent.getItemAtPosition(position).toString();
                ArrayList<Transaction> filterList = new ArrayList<>();
                switch (selectedItem) {
                    case "Tất cả":
                        transactionList.clear();
                        transactionList.addAll(modifierTransactionListByViewMode());
                        renderError(transactionList, transactionsSection, noTransactionsSection);
                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();
                        break;
                    case "Thu nhập":
                        transactionList.clear();
                        transactionList.addAll(modifierTransactionListByViewMode());
                        for(Transaction trans : transactionList) {
                            if(trans.getTransactionIsIncome()) filterList.add(trans);
                        }

                        transactionList.clear();
                        transactionList.addAll(filterList);
                        renderError(transactionList, transactionsSection, noTransactionsSection);
                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();
                        break;
                    case "Chi tiêu":
                        transactionList.clear();
                        transactionList.addAll(modifierTransactionListByViewMode());
                        for(Transaction trans : transactionList) {
                            if(!trans.getTransactionIsIncome()) filterList.add(trans);
                        }

                        transactionList.clear();
                        transactionList.addAll(filterList);
                        renderError(transactionList, transactionsSection, noTransactionsSection);
                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();
                        break;
                    case "Định kỳ":
                        transactionList.clear();
                        transactionList.addAll(modifierTransactionListByViewMode());
                        for(Transaction trans : transactionList) {
                            if(trans.getIsPeriodic()) filterList.add(trans);
                        }

                        transactionList.clear();
                        transactionList.addAll(filterList);
                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Sort menu initiation
        String[] sortMenuItems = new String[]{"Mới nhất", "Cũ nhất", "Lớn nhất", "Nhỏ nhất"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(view.getContext(),  R.layout.spinner_menu, sortMenuItems);
        sortMenu.setAdapter(sortAdapter);
        sortMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                switch (selectedItem) {
                    case "Mới nhất":
                        for(int i = 0; i<transactionList.size() - 1; i++) {
                            for(int j = i + 1; j<transactionList.size(); j++) {
                                if(transactionList.get(j).getTransactionTime().compareTo(transactionList.get(i).getTransactionTime()) > 0)
                                    Collections.swap(transactionList, i, j);
                            }
                        }

                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();
                        break;
                    case "Cũ nhất":
                        for(int i = 0; i<transactionList.size() - 1; i++) {
                            for(int j = i + 1; j<transactionList.size(); j++) {
                                if(transactionList.get(j).getTransactionTime().compareTo(transactionList.get(i).getTransactionTime()) < 0)
                                    Collections.swap(transactionList, i, j);
                            }
                        }

                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();
                        break;
                    case "Lớn nhất":
                        for(int i = 0; i<transactionList.size() - 1; i++) {
                            for(int j = i + 1; j<transactionList.size(); j++) {
                                if((double)transactionList.get(j).getTransactionAmount() > (double)transactionList.get(i).getTransactionAmount())
                                    Collections.swap(transactionList, i, j);
                            }
                        }

                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();
                        break;
                    case "Nhỏ nhất":
                        for(int i = 0; i<transactionList.size() - 1; i++) {
                            for(int j = i + 1; j<transactionList.size(); j++) {
                                if((double)transactionList.get(j).getTransactionAmount() < (double)transactionList.get(i).getTransactionAmount())
                                    Collections.swap(transactionList, i, j);
                            }
                        }

                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "Chủ nhật";
                break;
            case 2:
                day = "Thứ hai";
                break;
            case 3:
                day = "Thứ ba";
                break;
            case 4:
                day = "Thứ tư";
                break;
            case 5:
                day = "Thứ năm";
                break;
            case 6:
                day = "Thứ 6";
                break;
            case 7:
                day = "Thứ 7";
                break;
        }
        return day;
    }

    private void filter(String text) {
        ArrayList<Transaction> filterList = new ArrayList<>();

        if(text.isEmpty()) {
            filterList = transactionList;
        } else {
            MoneyToStringConverter converter = new MoneyToStringConverter();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            for(Transaction trans : transactionList) {
                String money = converter.moneyToString((double)trans.getTransactionAmount());
                String expenditureName = trans.getExpenditureName().toLowerCase();
                String date = sdf1.format(new Date(trans.getTransactionTime().getTime()));
                String time = sdf.format(new Date(trans.getTransactionTime().getTime()));

                if(money.contains(text.toLowerCase())
                        || expenditureName.contains((text.toLowerCase()))
                        || date.contains(text.toLowerCase())
                        || time.contains(text.toLowerCase())) {
                    filterList.add(trans);
                }
            }
        }

        transactionAdapter.getFilter(filterList);
    }

    private ArrayList<Transaction> modifierTransactionListByViewMode() {
        if(viewMode.equals(ViewMode.DAY)) return modifierTransactionListByDate();
        if(viewMode.equals(ViewMode.MONTH)) return modifierTransactionListByMonth();
        if(viewMode.equals(ViewMode.YEAR)) return modifierTransactionListByYear();
        if(viewMode.equals(ViewMode.MANUAL)) return modifierTransactionListByManual();
        return modifierTransactionListByDate();
    }

    private ArrayList<Transaction> modifierTransactionListByDate() {
        ArrayList<Transaction> modifierTransactionList = new ArrayList<>();
        Log.d("Test filter by date", selectedMoneySource.getMoneySourceName() + "-------------------------------------" + selectedMoneySource.getTransactionsList().size());
        for(Transaction t : selectedMoneySource.getTransactionsList()) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t.getTransactionTime().getTime());
            int transactionDay = c.get(Calendar.DAY_OF_MONTH);
            int transactionMonth = c.get(Calendar.MONTH);
            int transactionYeah = c.get(Calendar.YEAR);
//            Log.d("Test", Integer.toString(mDay) + " " + Integer.toString(c.get(Calendar.DAY_OF_MONTH)));
//            Log.d("Test", Integer.toString(mMonth) + " " + Integer.toString(c.get(Calendar.MONTH)));
//            Log.d("Test", Integer.toString(mYear) + " " + Integer.toString(c.get(Calendar.YEAR)));

            if(transactionDay == mDay && transactionMonth == mMonth && transactionYeah == mYear) modifierTransactionList.add(t);
        }
        return modifierTransactionList;
    }

    private ArrayList<Transaction> modifierTransactionListByMonth() {
        ArrayList<Transaction> modifierTransactionList = new ArrayList<>();
        Log.d("Test filter by month", selectedMoneySource.getMoneySourceName() + "-------------------------------------" + selectedMoneySource.getTransactionsList().size());
        for(Transaction t : selectedMoneySource.getTransactionsList()) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t.getTransactionTime().getTime());
            int transactionMonth = c.get(Calendar.MONTH);
            int transactionYeah = c.get(Calendar.YEAR);

            if(transactionMonth == mMonth && transactionYeah == mYear) modifierTransactionList.add(t);
        }
        return modifierTransactionList;
    }

    private ArrayList<Transaction> modifierTransactionListByYear() {
        ArrayList<Transaction> modifierTransactionList = new ArrayList<>();
        Log.d("Test filter by year", selectedMoneySource.getMoneySourceName() + "-------------------------------------" + selectedMoneySource.getTransactionsList().size());
        for(Transaction t : selectedMoneySource.getTransactionsList()) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t.getTransactionTime().getTime());
            int transactionMonth = c.get(Calendar.MONTH);
            int transactionYeah = c.get(Calendar.YEAR);

            if(transactionYeah == mYear) modifierTransactionList.add(t);
        }
        return modifierTransactionList;
    }

    private ArrayList<Transaction> modifierTransactionListByManual() {
        ArrayList<Transaction> modifierTransactionList = new ArrayList<>();
        Log.d("Test filter by manual", selectedMoneySource.getMoneySourceName() + "-------------------------------------" + selectedMoneySource.getTransactionsList().size());
        for(Transaction t : selectedMoneySource.getTransactionsList()) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t.getTransactionTime().getTime());

            if(c.compareTo(fromCal) >= 0 && c.compareTo(toCal) <= 0) modifierTransactionList.add(t);
        }
        return modifierTransactionList;
    }

    private String getTodayIncome() {
        MoneyToStringConverter converter = new MoneyToStringConverter();
        double total = 0;
        for(Transaction trans : transactionList) {
            if(trans.getTransactionIsIncome()) {
                total += trans.getTransactionAmount().doubleValue();
            }
        }

        return converter.moneyToString(total);
    }

    private String getTodaySpending() {
        MoneyToStringConverter converter = new MoneyToStringConverter();
        double total = 0;
        for(Transaction trans : transactionList) {
            if(!trans.getTransactionIsIncome()) {
                total += trans.getTransactionAmount().doubleValue();
            }
        }

        return converter.moneyToString(total);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == HOME_NEW_TRANSACTION_RQCODE) { // Xử lý khi thêm transaction mới
            if(resultCode == Activity.RESULT_OK) {
                Log.d("-------------Test result from add trans ", "OKE");
                DataHelper dataHelper = new DataHelper();
                Transaction resTransaction = (Transaction) data.getParcelableExtra("transaction");
                String msId = resTransaction.getMoneySourceId();

                for(MoneySource ms : moneySourceList) {
                    if(ms.getMoneySourceId().compareTo(msId) == 0) {
                        int index = 0;
                        for(index = 0; index < ms.getTransactionsList().size(); index++) {
                            Transaction trans = ms.getTransactionsList().get(index);
                            if(resTransaction.getTransactionTime().compareTo(trans.getTransactionTime()) >= 0) break;
                        }

                        ms.getTransactionsList().add(index, resTransaction);

                        // Cập nhập lại số tiền khi thêm transaction mới
                        if(resTransaction.getTransactionIsIncome()) {
                            ms.setAmount((double) ms.getAmount() + (double) resTransaction.getTransactionAmount());
                        } else {
                            ms.setAmount((double) ms.getAmount() - (double) resTransaction.getTransactionAmount());
                        }
                        dataHelper.updateMoneySource(ms);
                        moneySourceAdapter.notifyDataSetChanged();

                        // Cập nhập lại view của list transaction
                        Log.d("-------------Test result from add trans ", selectedMoneySource.getMoneySourceId() + " compared with " + msId);
                        if(selectedMoneySource.getMoneySourceId().equals(msId)) {
                            Log.d("-------------Test result from add trans ", "Equals");
                            selectedMoneySource = ms;

                            if(selectedMoneySource.getLimit().doubleValue() != 0) {
                                int percent = (int) ((selectedMoneySource.getAmount().doubleValue() / selectedMoneySource.getLimit().doubleValue()) * 100);
                                if (percent > 100) percent = 100;
                                Log.d("----------------------percent------------", String.valueOf(percent));
                                waveLoadingView.setProgressValue(percent);
                            } else {
                                waveLoadingView.setProgressValue(0);
                            }

                            transactionList.clear();
                            transactionList.addAll(modifierTransactionListByViewMode());

                            todayIncome.setText("+" + getTodayIncome());
                            todaySpending.setText("-" + getTodaySpending());

                            renderError(transactionList, transactionsSection, noTransactionsSection);
                            transactionAdapter.notifyDataSetChanged();
                            transactionRecycleView.scheduleLayoutAnimation();
                        }

                        break;
                    }
                }
            }
        } else if (requestCode == HOME_CHANGE_TRANSACTION_RQCODE) { // Xử lý khi cập nhập transaction
            if(resultCode == Activity.RESULT_OK) {
                Log.d("-------------Test result from trans detail ", "OKE");
                DataHelper dataHelper = new DataHelper();
                Transaction resTransaction = (Transaction) data.getParcelableExtra("transaction");
                String msId = resTransaction.getMoneySourceId();

                for(MoneySource ms : moneySourceList) {
                    if(ms.getMoneySourceId().compareTo(msId) == 0) {
                        // Kiểm tra xem thông tin giao dịch có thay đổi hay không
                        boolean isChange = false;
                        for(Transaction trans : ms.getTransactionsList()) {
                            if(trans.getTransactionId().equals(resTransaction.getTransactionId())) {
                                if(!trans.getTransactionTime().equals(resTransaction.getTransactionTime())
                                    || !trans.getTransactionAmount().equals(resTransaction.getTransactionAmount())
                                    || !trans.getExpenditureId().equals(resTransaction.getExpenditureId())) {
                                    isChange = true;
                                }

                                // Kiểm tra tính mô tả giao dịch
                                if(!isChange) {
                                    String transDes = trans.getDescription();
                                    String resTransDes = resTransaction.getDescription();

                                    if (transDes != null) {
                                        if (resTransDes != null) {
                                            if (!transDes.equals(resTransDes)) isChange = true;
                                        } else {
                                            isChange = true;
                                        }
                                    } else {
                                        if (resTransDes != null) {
                                            isChange = true;
                                        }
                                    }
                                }

                                if(isChange) {
                                    // Cộng hoặc trừ lại số tiền trước đó
                                    if(trans.getTransactionIsIncome()) {
                                        ms.setAmount((double)ms.getAmount() - (double) trans.getTransactionAmount());
                                    } else {
                                        ms.setAmount((double)ms.getAmount() + (double) trans.getTransactionAmount());
                                    }

                                    ms.getTransactionsList().remove(trans);
                                }

                                break;
                            }
                        }

                        if(isChange) {
                            dataHelper.updateTransaction(resTransaction);

                            int index = 0;
                            for (index = 0; index < ms.getTransactionsList().size(); index++) {
                                Transaction trans = ms.getTransactionsList().get(index);
                                if (resTransaction.getTransactionTime().compareTo(trans.getTransactionTime()) >= 0)
                                    break;
                            }

                            ms.getTransactionsList().add(index, resTransaction);

                            // Cập nhập lại số tiền khi thêm transaction mới
                            if (resTransaction.getTransactionIsIncome()) {
                                ms.setAmount((double) ms.getAmount() + (double) resTransaction.getTransactionAmount());
                            } else {
                                ms.setAmount((double) ms.getAmount() - (double) resTransaction.getTransactionAmount());
                            }
                            dataHelper.updateMoneySource(ms);
                            moneySourceAdapter.notifyDataSetChanged();

                            // Cập nhập lại view của list transaction
                            if (selectedMoneySource.getMoneySourceId().compareTo(msId) == 0) {
                                selectedMoneySource = ms;

                                if(selectedMoneySource.getLimit().doubleValue() != 0) {
                                    int percent = (int) ((selectedMoneySource.getAmount().doubleValue() / selectedMoneySource.getLimit().doubleValue()) * 100);
                                    if (percent > 100) percent = 100;
                                    waveLoadingView.setProgressValue(percent);
                                } else {
                                    waveLoadingView.setProgressValue(0);
                                }

                                transactionList.clear();
                                transactionList.addAll(modifierTransactionListByViewMode());

                                todayIncome.setText("+" + getTodayIncome());
                                todaySpending.setText("-" + getTodaySpending());

                                renderError(transactionList, transactionsSection, noTransactionsSection);
                                transactionAdapter.notifyDataSetChanged();
                                transactionRecycleView.scheduleLayoutAnimation();
                            }
                        }

                        break;
                    }
                }
            } else if(resultCode == Activity.RESULT_FIRST_USER) {
                Log.d("-------------Test result from trans detail ", "DELETE");
                DataHelper dataHelper = new DataHelper();
                Transaction resTransaction = (Transaction) data.getParcelableExtra("transaction");
                String msId = resTransaction.getMoneySourceId();

                for(MoneySource ms : moneySourceList) {
                    if (ms.getMoneySourceId().compareTo(msId) == 0) {
                        for(Transaction trans : ms.getTransactionsList()) {
                            if(trans.getTransactionId().equals(resTransaction.getTransactionId())) {
                                // Cộng hoặc trừ lại số tiền trước đó
                                if(trans.getTransactionIsIncome()) {
                                    ms.setAmount((double)ms.getAmount() - (double) trans.getTransactionAmount());
                                } else {
                                    ms.setAmount((double)ms.getAmount() + (double) trans.getTransactionAmount());
                                }
                                dataHelper.updateMoneySource(ms);
                                moneySourceAdapter.notifyDataSetChanged();

                                dataHelper.deleteTransaction(trans);
                                ms.getTransactionsList().remove(trans);

                                // Cập nhập lại view của list transaction
                                if (selectedMoneySource.getMoneySourceId().compareTo(msId) == 0) {
                                    selectedMoneySource = ms;

                                    if(selectedMoneySource.getLimit().doubleValue() != 0) {
                                        int percent = (int) ((selectedMoneySource.getAmount().doubleValue() / selectedMoneySource.getLimit().doubleValue()) * 100);
                                        if (percent > 100) percent = 100;
                                        waveLoadingView.setProgressValue(percent);
                                    } else {
                                        waveLoadingView.setProgressValue(0);
                                    }

                                    transactionList.clear();
                                    transactionList.addAll(modifierTransactionListByViewMode());

                                    todayIncome.setText("+" + getTodayIncome());
                                    todaySpending.setText("-" + getTodaySpending());

                                    renderError(transactionList, transactionsSection, noTransactionsSection);
                                    transactionAdapter.notifyDataSetChanged();
                                    transactionRecycleView.scheduleLayoutAnimation();
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("-------------Test result from trans detail ", "CANCEL");
            }
        } else if (requestCode == HOME_CHANGE_MONEYSOURCE_RQCODE) { // Thay đổi thông tin nguồn tiền
            if(resultCode == Activity.RESULT_OK) {
                Log.d("-------------Test result from moneysource detail ", "OKE");
                DataHelper dataHelper = new DataHelper();
                MoneySource resMoneySource = (MoneySource) data.getParcelableExtra("moneysource");
                String msId = resMoneySource.getMoneySourceId();
                MoneyToStringConverter converter = new MoneyToStringConverter();

                for(MoneySource ms : moneySourceList) {
                    if(ms.getMoneySourceId().equals(msId)) {
                        ms.setAmount(resMoneySource.getAmount());
                        ms.setMoneySourceName(resMoneySource.getMoneySourceName());
                        ms.setLimit(resMoneySource.getLimit());
                        ms.setCurrencyId(resMoneySource.getCurrencyId());
                        ms.setCurrencyName(resMoneySource.getCurrencyName());

                        waveLoadingView.setCenterTitle(converter.moneyToString((double)selectedMoneySource.getLimit()));
                        if(selectedMoneySource.getLimit().doubleValue() != 0) {
                            int percent = (int) ((selectedMoneySource.getAmount().doubleValue() / selectedMoneySource.getLimit().doubleValue()) * 100);
                            if (percent > 100) percent = 100;
                            waveLoadingView.setProgressValue(percent);
                        } else {
                            waveLoadingView.setProgressValue(0);
                        }

                        dataHelper.updateMoneySource(ms);
                        moneySourceAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            } else if(resultCode == Activity.RESULT_FIRST_USER) {
                Log.d("-------------Test result from moneysource detail ", "DELETE");
                DataHelper dataHelper = new DataHelper();
                MoneySource resMoneySource = (MoneySource) data.getParcelableExtra("moneysource");
                String msId = resMoneySource.getMoneySourceId();

                for(int i=0; i<moneySourceList.size(); i++) {
                    if(moneySourceList.get(i).getMoneySourceId().equals(msId)) {
                        dataHelper.deleteMoneySource(moneySourceList.get(i));
                        moneySourceList.remove(i);
                        moneySourceAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            } else if(resultCode == Activity.RESULT_CANCELED) {
                Log.d("-------------Test result from moneysource detail ", "CANCEL");
            }
        } else if (requestCode == HOME_NEW_MONEYSOURCE_RQCODE) { // Thêm mới nguồn tiền
            if(resultCode == Activity.RESULT_OK) {
                Log.d("-------------Test result from add moneysource ", "OKE");
                DataHelper dataHelper = new DataHelper();
                MoneySource resMoneySource = (MoneySource) data.getParcelableExtra("moneysource");
                resMoneySource.setTransactionsList(new ArrayList<Transaction>());

                moneySourceList.add(resMoneySource);
                moneySourceAdapter.notifyDataSetChanged();
            }
        }
    }

    void renderError(ArrayList<Transaction> transactionList, LinearLayout transactionsSection, LinearLayout noTransactionsSection)
    {
        if(transactionList.isEmpty())
        {
            transactionsSection.setVisibility(View.GONE);
            noTransactionsSection.setVisibility(View.VISIBLE);
        }
        else
        {
            transactionsSection.setVisibility(View.VISIBLE);
            noTransactionsSection.setVisibility(View.GONE);
        }
    }
}
