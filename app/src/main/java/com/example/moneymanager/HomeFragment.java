package com.example.moneymanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Result;

import me.itangqi.waveloadingview.WaveLoadingView;

import static android.content.ContentValues.TAG;

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
    public static final int HOME_RQCODE = 2;

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
        Log.d("Test", "---------------" + moneySourceList.get(0).getTransactionsList().size());
        selectedMoneySource = moneySourceList.get(0);
        Log.d("Test", "---------------" + selectedMoneySource.getTransactionsList().size());
    }

    private void initView(View view) {


        // Moneysource Info Initiation
        final WaveLoadingView waveLoadingView = view.findViewById(R.id.waveLoadingView);
        todayIncome = view.findViewById(R.id.todayIncome_home);
        todaySpending = view.findViewById(R.id.todaySpending_home);

        waveLoadingView.setCenterTitle(moneyToString((double)selectedMoneySource.getLimit()));
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
        dayOfWeek.setText("Hôm nay");

        View.OnClickListener dateSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        transactionList.addAll(modifierTransactionListByDate());
                        transactionAdapter.notifyDataSetChanged();
                        transactionRecycleView.scheduleLayoutAnimation();
                    }
                };
                new DatePickerDialog(getContext(), R.style.DatePickerDialog, date, mYear, mMonth, mDay).show();
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
                    selectedMoneySource = moneySourceList.get(pos);
                    waveLoadingView.setCenterTitle(moneyToString((double)selectedMoneySource.getLimit()));
                    todayIncome.setText("Thêm sau");
                    todaySpending.setText("Thêm sau");

                    transactionList.clear();
                    transactionList.addAll(modifierTransactionListByDate());
                    transactionAdapter.notifyDataSetChanged();
                    transactionRecycleView.scheduleLayoutAnimation();
                }


            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        // Transaction RecycleView Initiation
        transactionRecycleView = view.findViewById(R.id.transactionList);
        transactionList.addAll(modifierTransactionListByDate());

        GridLayoutManager transactionLayoutManager = new GridLayoutManager(getContext(), 2);
        transactionRecycleView.setLayoutManager(transactionLayoutManager);

        transactionAdapter = new HomeTransactionAdapter(transactionList, getContext());
        transactionRecycleView.setAdapter(transactionAdapter);
        transactionRecycleView.addItemDecoration(new SpaceItemDecoration(2,30,false));

        aController = AnimationUtils.loadLayoutAnimation(transactionRecycleView.getContext(), R.anim.layout_fall_down);
        transactionRecycleView.setLayoutAnimation(aController);
        transactionRecycleView.scheduleLayoutAnimation();
        transactionAdapter.notifyDataSetChanged();

        //"View by" menu initiation
        Spinner viewByMenu = view.findViewById(R.id.viewBy_home);
        String[] viewByMenuItems = new String[]{"Ngày", "Tuần", "Tháng", "Quý", "Năm", "Chọn"};

        ArrayAdapter<String> viewByAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_menu, viewByMenuItems);
        viewByMenu.setAdapter(viewByAdapter);

        //NOTE: file layout của dialog chọn khoảng thời gian là dialog_choose_time.xml
        viewByMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Filter menu initiation
        Spinner filterMenu = view.findViewById(R.id.filter_home);
        String[] filterMenuItems = new String[]{"Tất cả", "Thu nhập", "Chi tiêu", "Định kỳ"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_menu, filterMenuItems);
        filterMenu.setAdapter(filterAdapter);
        filterMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Sort menu initiation
        Spinner sortMenu = view.findViewById(R.id.sort_home);
        String[] sortMenuItems = new String[]{"Mới nhất", "Cũ nhất", "Lớn nhất", "Nhỏ nhất"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(view.getContext(),  R.layout.spinner_menu, sortMenuItems);
        sortMenu.setAdapter(sortAdapter);
        sortMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



//
//        transactionList.clear();
//        transactionList.addAll(modifierTransactionListByDate());
//        transactionAdapter.notifyDataSetChanged();
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

    private ArrayList<Transaction> modifierTransactionListByDate() {
        ArrayList<Transaction> modifierTransactionList = new ArrayList<>();
        Log.d("Test in", selectedMoneySource.getMoneySourceName() + "-------------------------------------" + selectedMoneySource.getTransactionsList().size());
        for(Transaction t : selectedMoneySource.getTransactionsList()) {
            Calendar c = Calendar.getInstance();
            Log.d("Test", "---------------");
            c.setTimeInMillis(t.getTransactionTime().getTime());
            int transactionDay = c.get(Calendar.DAY_OF_MONTH);
            int transactionMonth = c.get(Calendar.MONTH);
            int transactionYeah = c.get(Calendar.YEAR);
            Log.d("Test", Integer.toString(mDay) + " " + Integer.toString(c.get(Calendar.DAY_OF_MONTH)));
            Log.d("Test", Integer.toString(mMonth) + " " + Integer.toString(c.get(Calendar.MONTH)));
            Log.d("Test", Integer.toString(mYear) + " " + Integer.toString(c.get(Calendar.YEAR)));

            if(transactionDay == mDay && transactionMonth == mMonth && transactionYeah == mYear) modifierTransactionList.add(t);
        }
        return modifierTransactionList;
    }

    private String moneyToString(double amount) {
        if(amount == 0) return "0";
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == HOME_RQCODE) {
            if(resultCode == Activity.RESULT_OK) {
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
                            ms.setAmount((Double) ms.getAmount() + (Double) resTransaction.getTransactionAmount());
                        } else {
                            ms.setAmount((Double) ms.getAmount() - (Double) resTransaction.getTransactionAmount());
                        }
                        dataHelper.updateMoneySource(ms);
                        moneySourceAdapter.notifyDataSetChanged();

                        // Cập nhập lại view của list transaction
                        if(selectedMoneySource.getMoneySourceId().compareTo(msId) == 0) {
                            selectedMoneySource = ms;

                            transactionList.clear();
                            transactionList.addAll(modifierTransactionListByDate());
                            transactionAdapter.notifyDataSetChanged();
                            transactionRecycleView.scheduleLayoutAnimation();
                        }

                        break;
                    }
                }
            }
        }
    }
}
