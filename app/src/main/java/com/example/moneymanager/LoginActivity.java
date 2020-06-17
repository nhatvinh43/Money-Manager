package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText email_login;
    EditText password_login;
    Button loginButton;
    Button signupPrompt;
    TextView forgotPassword;

    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_login = findViewById(R.id.email_login); //Login email
        password_login = findViewById(R.id.password_login); //Login password
        loginButton = findViewById(R.id.loginButton); // Login button
        signupPrompt = findViewById(R.id.signupPrompt); // Switch to sign up
        forgotPassword = findViewById(R.id.forgotPassword); //Forgot password

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.loading);

        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_login.getText().toString().trim();
                String password = password_login.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    email_login.setError("Email không được để trống !");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    password_login.setError("Mật khẩu không được để trống !");
                    return;
                }

                if(password.length() < 6) {
                    password_login.setError("Mật khẩu phải nhiều hơn 5 kí tự !");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                email_login.setVisibility(View.INVISIBLE);
                password_login.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
                signupPrompt.setVisibility(View.INVISIBLE);
                forgotPassword.setVisibility(View.INVISIBLE);

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            email_login.setVisibility(View.VISIBLE);
                            password_login.setVisibility(View.VISIBLE);
                            loginButton.setVisibility(View.VISIBLE);
                            signupPrompt.setVisibility(View.VISIBLE);
                            forgotPassword.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });

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
