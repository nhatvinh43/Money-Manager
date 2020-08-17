package com.example.moneymanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.firebase.auth.FirebaseAuth;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    HomeFragment.ViewMode viewMode = HomeFragment.ViewMode.DAY;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Calendar task
    TextView dayOfWeek;
    TextView dateHome;
    TextView dateArrow;
    int mYear, mMonth, mDay;
    private Calendar fromDate = Calendar.getInstance();
    private Calendar toDate = Calendar.getInstance();
    private ArrayList<MoneySource> moneySources;
    private ArrayList<Transaction> transactions;
    private FirebaseAuth firebaseAuth;
    private DataHelper dataHelper;
    private RecyclerView recyclerView;
    private HomeMoneySourceAdapter homeMoneySourceAdapter;
    private MoneySource selectedMoneySource;
    private LayoutAnimationController layoutAnimationController;

    private String[] overallTitle = {"Thu", "Chi"};
    private String[] incomeTitle = {"Thưởng", "Lãi", "Lương", "Được tặng", "Khác"};
    private String[] spendTitle = {"Ăn uống", "Sinh hoạt", "Đi lại", "Sức khỏe", "Đám tiệc", "Khác"};
    private AnyChartView overallChart, incomeChart, spendChart;
    private TextView totalIncome, totalSpending;
    private Pie overallPieChart;
    private Cartesian incomeCartesianChart, spendingCartersianChart;
    private Column incomeColumn, spendingColumn;
    private ArrayList<DataEntry> dataEntries, dataEntries2, dataEntries3;

    ViewMode2 viewMode2 = ViewMode2.DAY;
    enum ViewMode2 {
        DAY,
        WEEK,
        MONTH,
        QUARTER,
        YEAR,
        MANUAL
    }

    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
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
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar loading = view.findViewById(R.id.loading_statistics);
        final RelativeLayout rl = view.findViewById(R.id.statisticsContainer);

        moneySources = new ArrayList<>();
        transactions = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        dataHelper = new DataHelper();
        selectedMoneySource = new MoneySource();
        overallPieChart = AnyChart.pie();
        dataEntries = new ArrayList<>();
        incomeCartesianChart = AnyChart.column();
        dataEntries2 = new ArrayList<>();
        spendingCartersianChart = AnyChart.column();
        dataEntries3 = new ArrayList<>();



        dataHelper.getMoneySource(new MoneySourceCallBack() {
            @Override
            public void onCallBack(ArrayList<MoneySource> list) {
                getMoneySourceList(list);
                loading.setVisibility(View.GONE);
                rl.setVisibility(View.VISIBLE);
                initView(view);
            }

            @Override
            public void onCallBackFail(String message) {

            }
        }, firebaseAuth.getCurrentUser().getUid());
    }

    public void getMoneySourceList(ArrayList<MoneySource> list){
        moneySources.clear();
        moneySources.addAll(list);
        selectedMoneySource = moneySources.get(0);
    }

    public void initView(View view){
        // Calendar Initiation
        dayOfWeek = view.findViewById(R.id.weekDay_statistics);
        dateHome = view.findViewById(R.id.date_statistics);
        dateArrow = view.findViewById(R.id.dateArrow_statistics);
        recyclerView = view.findViewById(R.id.moneySourceList_statistics);
        totalIncome = view.findViewById(R.id.totalIncome);
        totalSpending = view.findViewById(R.id.totalSpending);

        // prepare Overall chart
        dataEntries.add(new ValueDataEntry(overallTitle[0], 10));
        dataEntries.add(new ValueDataEntry(overallTitle[1], 10));
        overallChart = view.findViewById(R.id.overallChart);
        APIlib.getInstance().setActiveAnyChartView(overallChart);
        overallPieChart = AnyChart.pie();
        overallPieChart.data(dataEntries);
        overallPieChart.background("#000000");
        overallChart.setChart(overallPieChart);

        //prepare Income chart
        dataEntries2.clear();
        dataEntries2.add(new ValueDataEntry(incomeTitle[0],1));
        dataEntries2.add(new ValueDataEntry(incomeTitle[1],2));
        dataEntries2.add(new ValueDataEntry(incomeTitle[2],3));
        dataEntries2.add(new ValueDataEntry(incomeTitle[3],2));
        dataEntries2.add(new ValueDataEntry(incomeTitle[4],1));

        incomeChart = view.findViewById(R.id.incomeChart);
        APIlib.getInstance().setActiveAnyChartView(incomeChart);
        incomeCartesianChart = AnyChart.column();

        incomeColumn = incomeCartesianChart.column(dataEntries2);
        incomeCartesianChart.background("#000000");
        incomeColumn.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");
        incomeCartesianChart.yScale().minimum(0d);

        incomeCartesianChart.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        incomeCartesianChart.tooltip().positionMode(TooltipPositionMode.POINT);
        incomeCartesianChart.interactivity().hoverMode(HoverMode.BY_X);
        incomeCartesianChart.xAxis(0).title("Hạng mục");
        incomeCartesianChart.yAxis(0).title("Số tiền");
        incomeChart.setChart(incomeCartesianChart);

        // prepare Spend chart
        dataEntries3.add(new ValueDataEntry(spendTitle[0],1));
        dataEntries3.add(new ValueDataEntry(spendTitle[1],2));
        dataEntries3.add(new ValueDataEntry(spendTitle[2],3));
        dataEntries3.add(new ValueDataEntry(spendTitle[3],3));
        dataEntries3.add(new ValueDataEntry(spendTitle[4],2));
        dataEntries3.add(new ValueDataEntry(spendTitle[5],1));

        spendChart = view.findViewById(R.id.spendChart);
        APIlib.getInstance().setActiveAnyChartView(spendChart);
        spendingCartersianChart = AnyChart.column();
        spendingColumn = spendingCartersianChart.column(dataEntries3);
        spendingCartersianChart.background("#000000");
        spendingCartersianChart.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");
        spendingCartersianChart.yScale().minimum(0d);

        spendingCartersianChart.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        spendingCartersianChart.tooltip().positionMode(TooltipPositionMode.POINT);
        spendingCartersianChart.interactivity().hoverMode(HoverMode.BY_X);
        spendingCartersianChart.xAxis(0).title("Hạng mục");
        spendingCartersianChart.yAxis(0).title("Số tiền");
        spendChart.setChart(spendingCartersianChart);

        SharedPreferences prefs = ((MainActivity)getActivity()).getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String myFormat = prefs.getString("currentDate", "dd/MM/yyyy");

        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        final Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        dateHome.setText(sdf.format(mcurrentDate.getTime()));
        dayOfWeek.setText("Hôm nay");

        //"View by" menu initiation
        Spinner viewByMenu = view.findViewById(R.id.viewBy_statistics);
        String[] viewByMenuItems = new String[]{"Ngày", "Tuần", "Tháng", "Quý", "Năm", "Chọn"};

        ArrayAdapter<String> viewByAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_menu, viewByMenuItems);
        viewByMenu.setAdapter(viewByAdapter);

        //NOTE: file layout của dialog chọn khoảng thời gian là dialog_choose_time.xml
        viewByMenu.setSelection(0);
        viewByMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar myCalender = Calendar.getInstance();
                mYear = myCalender.get(Calendar.YEAR);
                mMonth = myCalender.get(Calendar.MONTH);
                mDay = myCalender.get(Calendar.DAY_OF_MONTH);
                String selectedItem = parent.getItemAtPosition(position).toString();
                System.out.println(transactions.size());
                switch (selectedItem)
                {
                    case "Ngày":
                    {
                        dateHome.setText(sdf.format(myCalender.getTime()));
                        dayOfWeek.setText("Hôm nay");
                        viewMode2 = viewMode2.DAY;

                        transactions.clear();
                        transactions.addAll(modifierTransactionListByViewMode());
                        System.out.println(transactions.size());
                        setupDateChart();
                        break;
                    }
                    case "Tuần":
                    {
                        viewMode2 = viewMode2.WEEK;
                        break;
                    }
                    case "Tháng":
                    {
                        SimpleDateFormat sdf_month = new SimpleDateFormat("MM/yyyy");
                        dateHome.setText(sdf_month.format(myCalender.getTime()));
                        dayOfWeek.setText("Tháng nay");
                        viewMode2 = viewMode2.MONTH;
                        transactions.clear();
                        transactions.addAll(modifierTransactionListByViewMode());
                        setupDateChart();

                        break;
                    }
                    case "Quý":
                    {
                        viewMode2 = viewMode2.QUARTER;
                        break;
                    }
                    case "Năm":
                    {
                        SimpleDateFormat sdf_month = new SimpleDateFormat("yyyy");
                        dateHome.setText(sdf_month.format(myCalender.getTime()));
                        dayOfWeek.setText("Năm nay");
                        viewMode2 = viewMode2.YEAR;

                        transactions.clear();
                        transactions.addAll(modifierTransactionListByViewMode());
                        setupDateChart();
                        break;
                    }
                    case "Chọn":
                    {
                        viewMode2 = viewMode2.MANUAL;

                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                        builder.setView(R.layout.dialog_choose_time);
                        final AlertDialog chooseTimeDialog  = builder.create();
                        chooseTimeDialog.show();
                        chooseTimeDialog.getWindow().setLayout(1000,1200);
                        chooseTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final TextView fromDateTV = chooseTimeDialog.findViewById(R.id.start_choose_time);
                        final TextView toDateTV = chooseTimeDialog.findViewById(R.id.end_choose_time);

                        //chọn ngày bắt đầu
                        chooseTimeDialog.findViewById(R.id.chooseStart_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.set(Calendar.YEAR, year);
                                        myCalendar.set(Calendar.MONTH, month);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        fromDateTV.setText(sdf.format(myCalendar.getTime()));
                                        fromDate = myCalendar;
                                    }
                                };
                                new DatePickerDialog(getContext(), R.style.DatePickerDialog, date, mYear, mMonth, mDay).show();
                            }
                        });

                        //chọn ngày kết thúc
                        chooseTimeDialog.findViewById(R.id.chooseEnd_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.set(Calendar.YEAR, year);
                                        myCalendar.set(Calendar.MONTH, month);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        toDateTV.setText(sdf.format(myCalendar.getTime()));
                                        toDate = myCalendar;
                                    }
                                };
                                new DatePickerDialog(getContext(), R.style.DatePickerDialog, date, mYear, mMonth, mDay).show();
                            }
                        });

                        //nút xác nhận
                        chooseTimeDialog.findViewById(R.id.confirm_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dayOfWeek.setText("Mốc thời gian");
                                dateHome.setText(sdf.format(fromDate.getTime()) + " - " + sdf.format(toDate.getTime()));

                                transactions.clear();
                                transactions.addAll(modifierTransactionListByViewMode());
                                setupDateChart();
                                chooseTimeDialog.dismiss();
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        View.OnClickListener dateSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(viewMode2)
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

                                transactions.clear();
                                transactions.addAll(modifierTransactionListByViewMode());
                                setupDateChart();
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

                                        transactions.clear();
                                        transactions.addAll(modifierTransactionListByViewMode());
                                        setupDateChart();
                                    }
                                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
                        builder.setActivatedMonth(Calendar.JULY)
                                .setMinYear(2010)
                                .setActivatedYear(2020)
                                .setMaxYear(2099)
                                .setMinMonth(Calendar.JANUARY)
                                .setTitle("Chọn tháng")
                                .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                                    @Override
                                    public void onMonthChanged(int selectedMonth) {} }).setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                            @Override
                            public void onYearChanged(int selectedYear) {}}).build().show();
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
                                        transactions.clear();
                                        transactions.addAll(modifierTransactionListByViewMode());
                                        setupDateChart();
                                    }
                                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
                        builder.setActivatedMonth(Calendar.JULY)
                                .setMinYear(2010)
                                .setActivatedYear(2020)
                                .setMaxYear(2099)
                                .setMinMonth(Calendar.JANUARY)
                                .setTitle("Chọn năm")
                                .showYearOnly()
                                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                                    @Override
                                    public void onMonthChanged(int selectedMonth) {} }).setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                            @Override
                            public void onYearChanged(int selectedYear) {}}).build().show();
                        break;
                    }
                    case MANUAL:
                    {
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                        builder.setView(R.layout.dialog_choose_time);
                        final AlertDialog chooseTimeDialog  = builder.create();
                        chooseTimeDialog.show();
                        chooseTimeDialog.getWindow().setLayout(1000,1200);
                        chooseTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final TextView fromDateTV = chooseTimeDialog.findViewById(R.id.start_choose_time);
                        final TextView toDateTV = chooseTimeDialog.findViewById(R.id.end_choose_time);

                        //chọn ngày bắt đầu
                        chooseTimeDialog.findViewById(R.id.chooseStart_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.set(Calendar.YEAR, year);
                                        myCalendar.set(Calendar.MONTH, month);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        fromDateTV.setText(sdf.format(myCalendar.getTime()));
                                        fromDate = myCalendar;
                                    }
                                };
                                new DatePickerDialog(getContext(), R.style.DatePickerDialog, date, mYear, mMonth, mDay).show();
                            }
                        });

                        //chọn ngày kết thúc
                        chooseTimeDialog.findViewById(R.id.chooseEnd_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.set(Calendar.YEAR, year);
                                        myCalendar.set(Calendar.MONTH, month);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        toDateTV.setText(sdf.format(myCalendar.getTime()));
                                        toDate = myCalendar;
                                    }
                                };
                                new DatePickerDialog(getContext(), R.style.DatePickerDialog, date, mYear, mMonth, mDay).show();
                            }
                        });

                        //nút xác nhận
                        chooseTimeDialog.findViewById(R.id.confirm_choose_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dayOfWeek.setText("Mốc thời gian");
                                dateHome.setText(sdf.format(fromDate.getTime()) + " - " + sdf.format(toDate.getTime()));

                                transactions.clear();
                                transactions.addAll(modifierTransactionListByViewMode());
                                setupDateChart();
                                chooseTimeDialog.dismiss();
                            }
                        });
                        break;
                    }
                }


            }
        };


        dateArrow.setOnClickListener(dateSelector);
        dayOfWeek.setOnClickListener(dateSelector);
        dateHome.setOnClickListener(dateSelector);
        //init recyclerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        homeMoneySourceAdapter = new HomeMoneySourceAdapter(moneySources, getContext());
        recyclerView.setAdapter(homeMoneySourceAdapter);
        recyclerView.setClipToPadding(false);
        int activeColor = ContextCompat.getColor(view.getContext(), R.color.white);
        int deactiveColor = ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark);
        recyclerView.addItemDecoration(new DotsIndicatorDecoration(9, 20, 100, deactiveColor, activeColor));

        //Money source Item animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(0);
                if(viewHolder != null) {
                    CardView cv = viewHolder.itemView.findViewById(R.id.cardContainer);
                    cv.animate().setDuration(100).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
                }
            }
        }, 100);

        //on Snap Helper Scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    return;
                }
                View v = snapHelper.findSnapView(linearLayoutManager);
                int pos = linearLayoutManager.getPosition(v);

                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(pos);
                CardView cv = viewHolder.itemView.findViewById(R.id.cardContainer);

                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    cv.animate().setDuration(300).scaleX(1).scaleY(1).setInterpolator(new DecelerateInterpolator()).start();
                } else {
                    cv.animate().setDuration(300).scaleX(0.9f).scaleY(0.9f).setInterpolator(new AccelerateInterpolator()).start();
                }

                if (newState != RecyclerView.SCROLL_STATE_SETTLING){
                    selectedMoneySource = moneySources.get(pos);
                    transactions.clear();
                    transactions.addAll(modifierTransactionListByViewMode());
                    setupDateChart();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


    }
    private ArrayList<Transaction> modifierTransactionListByViewMode() {
        if(viewMode2.equals(ViewMode2.DAY)) return modifierTransactionListByDate();
        if(viewMode2.equals(ViewMode2.MONTH)) return modifierTransactionListByMonth();
        if(viewMode2.equals(ViewMode2.YEAR)) return modifierTransactionListByYear();
        if(viewMode2.equals(ViewMode2.MANUAL)) return modifierTransactionListByManual();
        return modifierTransactionListByDate();
    }

    private ArrayList<Transaction> modifierTransactionListByManual() {
        ArrayList<Transaction> res = new ArrayList<>();
        for (Transaction t : selectedMoneySource.getTransactionsList()){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t.getTransactionTime().getTime());
            if (c.compareTo(fromDate) >= 0 && c.compareTo(toDate) <=0){
                res.add(t);
            }
        }
        return res;
    }

    private ArrayList<Transaction> modifierTransactionListByYear() {
        ArrayList<Transaction> res = new ArrayList<>();
        for (Transaction t : selectedMoneySource.getTransactionsList()){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t.getTransactionTime().getTime());
            int transactionYear = c.get(Calendar.YEAR);
            if (transactionYear == mYear){
                res.add(t);
            }
        }
        return res;
    }

    private ArrayList<Transaction> modifierTransactionListByMonth() {
        ArrayList<Transaction> res = new ArrayList<>();
        for (Transaction t : selectedMoneySource.getTransactionsList()){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t.getTransactionTime().getTime());
            int transactionMonth = c.get(Calendar.MONTH);
            int transactionYear = c.get(Calendar.YEAR);
            if (transactionMonth == mMonth && transactionYear == mYear) {
                res.add(t);
            }
        }
        return res;
    }

    private ArrayList<Transaction> modifierTransactionListByDate() {
        ArrayList<Transaction> res = new ArrayList<>();
        for(Transaction t : selectedMoneySource.getTransactionsList()) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t.getTransactionTime().getTime());
            int transactionDay = c.get(Calendar.DAY_OF_MONTH);
            int transactionMonth = c.get(Calendar.MONTH);
            int transactionYeah = c.get(Calendar.YEAR);
            if(transactionDay == mDay && transactionMonth == mMonth && transactionYeah == mYear)
                res.add(t);
        }
        return res;
    }

    private ArrayList<Double> overallRate(){
        System.out.println("Size of Transactions Prepare: " + transactions.size());
        ArrayList<Double> res = new ArrayList<>();
        double income, spend;
        income = 0.0;
        spend = 0.0;
        for (int i = 0; i < transactions.size(); i++){
            if (transactions.get(i).getTransactionIsIncome()){
                income += (double) transactions.get(i).getTransactionAmount();
            } else {
                spend += (double) transactions.get(i).getTransactionAmount();
            }
        }
        res.add(income);
        res.add(spend);
        return res;
    }
    private ArrayList<Double> incomeRate(){
        ArrayList<Double> res = new ArrayList<>();
        double bonus, profit, salary, gift, other;
        bonus = 0.0;
        profit = 0.0;
        salary = 0.0;
        gift = 0.0;
        other = 0.0;
        for (int i = 0; i < transactions.size(); i++){
            if (transactions.get(i).getTransactionIsIncome()){
                switch (transactions.get(i).getExpenditureId()){
                    case "Exp07":
                        bonus += (double) transactions.get(i).getTransactionAmount();
                        break;
                    case "Exp08":
                        profit += (double) transactions.get(i).getTransactionAmount();
                        break;
                    case "Exp09":
                        salary += (double) transactions.get(i).getTransactionAmount();
                        break;
                    case "Exp10":
                        gift += (double) transactions.get(i).getTransactionAmount();
                        break;
                    case "Exp11":
                        other += (double) transactions.get(i).getTransactionAmount();
                        break;
                }
            }
        }
        res.add(bonus);
        res.add(profit);
        res.add(salary);
        res.add(gift);
        res.add(other);
        return res;
    }
    private ArrayList<Double> spendRate(){
        ArrayList<Double> res = new ArrayList<>();
        double food, bill, traval, health, party, other;
        food = 0.0;
        bill = 0.0;
        traval = 0.0;
        health = 0.0;
        party = 0.0;
        other = 0.0;
        for (int i = 0; i < transactions.size(); i++){
            if (!transactions.get(i).getTransactionIsIncome()){
                switch (transactions.get(i).getExpenditureId()){
                    case "Exp01":
                        food += (double) transactions.get(i).getTransactionAmount();
                        break;
                    case "Exp02":
                        bill += (double) transactions.get(i).getTransactionAmount();
                        break;
                    case "Exp03":
                        traval += (double) transactions.get(i).getTransactionAmount();
                        break;
                    case "Exp04":
                        health += (double) transactions.get(i).getTransactionAmount();
                        break;
                    case "Exp05":
                        party += (double) transactions.get(i).getTransactionAmount();
                        break;
                    case "Exp06":
                        other += (double) transactions.get(i).getTransactionAmount();
                        break;
                }
            }
        }
        res.add(food);
        res.add(bill);
        res.add(traval);
        res.add(health);
        res.add(party);
        res.add(other);
        return res;
    }

    private void setupChart(){
        if (viewMode2 == ViewMode2.DAY) setupDateChart();
    }

    private void setupDateChart(){
        //overall Chart
        System.out.println("Size of Transactions Setup: " + transactions.size());
        ArrayList<Double> res = new ArrayList<>();
        res = overallRate();
        dataEntries.clear();
        for (int i = 0; i < 2; i++){
            dataEntries.add(new ValueDataEntry(overallTitle[i], res.get(i)));
        }
        System.out.println(dataEntries.size());
        APIlib.getInstance().setActiveAnyChartView(overallChart);
        overallPieChart.data(dataEntries);
        //setup income/spending
        MoneyToStringConverter moneyToStringConverter = new MoneyToStringConverter();
        totalIncome.setText(moneyToStringConverter.moneyToString((double)(res.get(0))));
        totalSpending.setText(moneyToStringConverter.moneyToString((double)(res.get(1))));

        //income Chart
        ArrayList<Double> resIncome = new ArrayList<>();
        resIncome = incomeRate();
        dataEntries2.clear();
        for (int i = 0; i < resIncome.size(); i++){
            dataEntries2.add(new ValueDataEntry(incomeTitle[i], resIncome.get(i)));
        }
        APIlib.getInstance().setActiveAnyChartView(incomeChart);
        incomeCartesianChart.data(dataEntries2);

        //spend Chart
        ArrayList<Double> resSpend = new ArrayList<>();
        resSpend = spendRate();
        dataEntries3.clear();
        for (int i = 0; i < resSpend.size(); i++){
            dataEntries3.add(new ValueDataEntry(spendTitle[i], resSpend.get(i)));
        }
        APIlib.getInstance().setActiveAnyChartView(spendChart);
        spendingCartersianChart.data(dataEntries3);
    }

    private void setupMonthChart(View view){

    }

    private void setupYearChart(View view){

    }

    private void setupManualChar(View view){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Resume");
        dataHelper.getMoneySource(new MoneySourceCallBack() {
            @Override
            public void onCallBack(ArrayList<MoneySource> list) {
                getMoneySourceList(list);
                setupDateChart();
            }

            @Override
            public void onCallBackFail(String message) {

            }
        }, firebaseAuth.getCurrentUser().getUid());
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
}
