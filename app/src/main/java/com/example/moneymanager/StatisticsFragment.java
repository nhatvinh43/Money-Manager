package com.example.moneymanager;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    enum ViewMode {
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Calendar Initiation
        dayOfWeek = view.findViewById(R.id.weekDay_statistics);
        dateHome = view.findViewById(R.id.date_statistics);
        dateArrow = view.findViewById(R.id.dateArrow_statistics);

        String myFormat = "dd/MM/yyyy";
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

                String selectedItem = parent.getItemAtPosition(position).toString();
                switch (selectedItem)
                {
                    case "Ngày":
                    {
                        viewMode = HomeFragment.ViewMode.DAY;
                        break;
                    }
                    case "Tuần":
                    {
                        viewMode = HomeFragment.ViewMode.WEEK;
                        break;
                    }
                    case "Tháng":
                    {
                        viewMode = HomeFragment.ViewMode.MONTH;
                        break;
                    }
                    case "Quý":
                    {
                        viewMode = HomeFragment.ViewMode.QUARTER;
                        break;
                    }
                    case "Năm":
                    {
                        viewMode = HomeFragment.ViewMode.YEAR;
                        break;
                    }
                    case "Chọn":
                    {
                        viewMode = HomeFragment.ViewMode.MANUAL;

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
                                        //Code code code
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
                                        //Bỏ qua month
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
