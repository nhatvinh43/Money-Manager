package com.example.moneymanager;

import android.app.DatePickerDialog;
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

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
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
    private ArrayList<MoneySource> moneySouces = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DataHelper dataHelper = new DataHelper();
    private RecyclerView recyclerView;
    private HomeMoneySourceAdapter homeMoneySourceAdapter;
    private MoneySource selectedMoneySource = new MoneySource();
    private LayoutAnimationController layoutAnimationController;

    private String[] overallTitle = {"Thu", "Chi"};
    private AnyChartView overallChart;

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
        moneySouces.clear();
        moneySouces.addAll(list);
        selectedMoneySource = moneySouces.get(0);
        transactions = selectedMoneySource.getTransactionsList();
    }

    public void initView(View view){
        // Calendar Initiation
        dayOfWeek = view.findViewById(R.id.weekDay_statistics);
        dateHome = view.findViewById(R.id.date_statistics);
        dateArrow = view.findViewById(R.id.dateArrow_statistics);
        recyclerView = view.findViewById(R.id.moneySourceList_statistics);
        overallChart = view.findViewById(R.id.overallChart);


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
                switch (selectedItem)
                {
                    case "Ngày":
                    {
                        dateHome.setText(sdf.format(myCalender.getTime()));
                        dayOfWeek.setText("Hôm nay");
                        viewMode2 = viewMode2.DAY;

                        transactions.clear();
                        transactions.addAll(modifierTransactionListByViewMode());
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
                                System.out.println("In: " + modifierTransactionListByViewMode().size());

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

        homeMoneySourceAdapter = new HomeMoneySourceAdapter(moneySouces, getContext());
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
//                CardView cv = viewHolder.itemView.findViewById(R.id.cardContainer);
//                cv.animate().setDuration(100).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
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
                    selectedMoneySource = moneySouces.get(pos);
                    transactions.clear();
                    transactions.addAll(modifierTransactionListByViewMode());
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

        return res;
    }

    private ArrayList<Transaction> modifierTransactionListByYear() {
        ArrayList<Transaction> res = new ArrayList<>();
        return res;
    }

    private ArrayList<Transaction> modifierTransactionListByMonth() {
        ArrayList<Transaction> res = new ArrayList<>();
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
        ArrayList<Double> res = new ArrayList<>();
        double income, spend;
        income = 0;
        spend = 0;
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

    private void setupDateChart(){
        //overall Chart
        Pie overall = AnyChart.pie();
        ArrayList<Double> res = new ArrayList<>();
        res = overallRate();
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < 2; i++){
            dataEntries.add(new ValueDataEntry(overallTitle[i], res.get(i)));
        }
        overall.data(dataEntries);
        this.overallChart.setChart(overall);
    }

    private void setupMonthChart(View view){

    }

    private void setupYearChart(View view){

    }

    private void setupManualChar(View view){

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
