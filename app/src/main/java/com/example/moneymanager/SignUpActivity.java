package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText email_signup = findViewById(R.id.email_signup);
        EditText fullName_signup = findViewById(R.id.fullName_signup);
        EditText password_signup = findViewById(R.id.password_signup);
        EditText passwordConfirm_signup = findViewById(R.id.passwordConfirm_signup);
        Button signupButton = findViewById(R.id.signupButton);
        Button loginPrompt = findViewById(R.id.loginPrompt);

        loginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
