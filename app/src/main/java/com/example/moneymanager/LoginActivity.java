package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.username_login); //Login username
        EditText password = findViewById(R.id.password_login); //Login password
        Button loginButton = findViewById(R.id.loginButton); // Login button
        TextView signupPrompt = findViewById(R.id.signupPrompt); // Switch to sign up

    }
}
