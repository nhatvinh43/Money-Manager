package com.example.moneymanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MoneySourceDetailsActivity extends AppCompatActivity {
    private TextView name, amount, cur;
    private EditText eName, eAmount, eCur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_source_details);
        Intent intent = getIntent();
        String MSId = intent.getStringExtra("MSId");
        MoneySource MS = findByMSId(MSId);

        name = findViewById(R.id.moneySourceNameDisplay_moneySourceDetails);
        amount = findViewById(R.id.moneySourceAmountDisplay_moneySourceDetails);
        cur = findViewById(R.id.moneySourceUnitDisplay_moneySourceDetails);
        eName = findViewById(R.id.moneySourceName_moneySourceDetails);
        eAmount = findViewById(R.id.moneySourceAmount_moneySourceDetails);
        eCur = findViewById(R.id.moneySourceUnit_moneySourceDetails);

        name.setText(MS.getMoneySourceName());
        eName.setText(MS.getMoneySourceName());

        amount.setText(MS.getAmount().toString());
        eAmount.setText(MS.getAmount().toString());

        cur.setText(MS.getCurrencyName());
        eCur.setText(MS.getCurrencyName());

        //Nút Lưu
        findViewById(R.id.save_moneySourceDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chuẩn bị dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MoneySourceDetailsActivity.this);
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
                //Thất Bại
                if (eAmount.getText().toString().length() == 0 || eName.getText().toString().length() == 0
                || eCur.getText().toString().length() == 0) {
                    msg.setText("Vui lòng điền đầy đủ thông tin");
                    dialog.show();
                }else{
                    //Thành Công
                    Toast.makeText(dialog.getContext(), "Chỉnh Sửa Thành Công", Toast.LENGTH_LONG).show();
                    //Làm Việc Với DataBase
                    finish();
                }
            }
        });

        //Nút Xóa
        findViewById(R.id.delete_moneySourceDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chuẩn bị dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MoneySourceDetailsActivity.this);
                builder.setView(R.layout.dialog_two_buttons);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setLayout(850,400);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final TextView msg = dialog.findViewById(R.id.message_two_button_dialog);
                msg.setText("Bạn chắc chắn muốn xóa");
                dialog.findViewById(R.id.confirm_two_button_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.cancel_two_button_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        //Nút Back
        findViewById(R.id.backButton_moneySourceDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    MoneySource findByMSId(String Id){
        for (MoneySource MS : UltilitiesFragment.dataSet){
            if (MS.getMoneySourceId().equals(Id)){
                return MS;
            }
        }
        return null;
    }
}