package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText email = findViewById(R.id.email_login); //Login email
        EditText password = findViewById(R.id.password_login); //Login password
        Button loginButton = findViewById(R.id.loginButton); // Login button
        Button signupPrompt = findViewById(R.id.signupPrompt); // Switch to sign up
        TextView forgotPassword = findViewById(R.id.forgotPassword); //Forgot password

        signupPrompt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignUpActivity.class);
                startActivity(intent);
            } //Chuyển sang màn hình sign up
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PasswordRecoveryActivity.class);
                startActivity(intent);
            }
        });
    }
}
