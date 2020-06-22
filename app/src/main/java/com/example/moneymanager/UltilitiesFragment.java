package com.example.moneymanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UltilitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UltilitiesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UltilitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UltilitiesFragment.
     */

    //use public vì khi có database, khi Activity này được mở hay tiếp tục thì sẽ cập nhật lại dữ liệu từ server
    public static ArrayList<MoneySource> dataSet = new ArrayList<>();
    private RecyclerView recyclerView;
    private UltilitiesMoneySourceManageAdapter adapter;

    // TODO: Rename and change types and number of parameters
    public static UltilitiesFragment newInstance(String param1, String param2) {
        UltilitiesFragment fragment = new UltilitiesFragment();
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
        View v = inflater.inflate(R.layout.fragment_ultilities, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        dataSet.add(new MoneySource(10000, "Cur01", "VND", 1000,
                "MS001", "TestMS01", "U001" ));
        dataSet.add(new MoneySource(10000, "Cur01", "AUD", 1000,
                "MS002", "TestMS02", "U001" ));

        view.findViewById(R.id.manageMoneySourcesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_manage_money_sources);
                final AlertDialog categoryPanel  = builder.create();
                categoryPanel.show();
                categoryPanel.getWindow().setLayout(1000,1200);
                categoryPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                categoryPanel.findViewById(R.id.addMoneySource_manageMoneySource).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AddMoneySourceActivity.class);
                        categoryPanel.dismiss();
                        startActivity(intent);

                    }
                });
                adapter = new UltilitiesMoneySourceManageAdapter(categoryPanel.getContext(), dataSet);
                recyclerView = categoryPanel.findViewById(R.id.moneySourceList_manageMoneySource);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(categoryPanel.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                adapter.setOnItemClickListener(new UltilitiesMoneySourceManageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        MoneySource moneySource = dataSet.get(position);
                        Intent intent = new Intent(categoryPanel.getContext(), MoneySourceDetailsActivity.class);
                        intent.putExtra("MSId", moneySource.getMoneySourceId());
                        startActivity(intent);
                    }
                });

            }
        });
    }
}
