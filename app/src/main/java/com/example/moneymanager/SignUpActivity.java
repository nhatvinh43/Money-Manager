package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText username_signup = findViewById(R.id.username_signup);
        EditText fullName_signup = findViewById(R.id.fullName_signup);
        EditText password_signup = findViewById(R.id.password_signup);
        EditText passwordConfirm_signup = findViewById(R.id.passwordConfirm_signup);
        Button signupButton = findViewById(R.id.signupButton);
        TextView loginPrompt = findViewById(R.id.loginPrompt);

    }
}
