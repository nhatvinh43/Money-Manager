package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PasswordRecoveryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        final EditText email_passwordRecovery = findViewById(R.id.email_passwordRecovery);
        final Button passwordRecoveryButton = findViewById(R.id.passwordRecoveryButton);
        final EditText otp = findViewById(R.id.otp_passwordRecovery);
        final Button otpButton = findViewById(R.id.otpButton);

        passwordRecoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                otp.setVisibility(View.VISIBLE);
                otpButton.setVisibility(View.VISIBLE);
                email_passwordRecovery.setVisibility(View.GONE);
                passwordRecoveryButton.setVisibility(View.GONE);

            }
        });

    }
}
