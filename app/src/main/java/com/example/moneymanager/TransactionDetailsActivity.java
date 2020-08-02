package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TransactionDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        // Nút back
        findViewById(R.id.backButton_transactionDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Button saveOrEditButton = findViewById(R.id.editAndSave_transactionDetails);
        final Button chooseDateTimeButton = findViewById(R.id.chooseDateTime_transactionDetails);
        final EditText dateTime = findViewById(R.id.dateTime_transactionDetails);
        final EditText description = findViewById(R.id.description_transactionDetails);
        final EditText amount = findViewById(R.id.amount_transactionDetails);
        final ImageView icon = findViewById(R.id.transactionIcon_transactionDetails);

        saveOrEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentStatus = (String) saveOrEditButton.getText();

                if(currentStatus.equals("Sửa"))
                {

                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            0,LinearLayout.LayoutParams.MATCH_PARENT,3.0f);

                    icon.setOnClickListener(null);
                    amount.setEnabled(true);
                    dateTime.setLayoutParams(param);
                    chooseDateTimeButton.setVisibility(View.VISIBLE);
                    description.setEnabled(true);

                    saveOrEditButton.setText("Lưu");

                }
                else
                {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            0,LinearLayout.LayoutParams.MATCH_PARENT,4.0f);

                    icon.setOnClickListener(null); //Thêm on click cho icon, mở dialog_choose_category.xml
                    amount.setEnabled(false);
                    dateTime.setLayoutParams(param);
                    chooseDateTimeButton.setVisibility(View.GONE);
                    description.setEnabled(false);

                    saveOrEditButton.setText("Sửa");

                }

            }
        });

    }
}