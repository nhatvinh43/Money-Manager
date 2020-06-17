package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    EditText email_signup;
    EditText fullName_signup;
    EditText password_signup;
    EditText passwordConfirm_signup;
    Button signupButton;
    Button loginPrompt;

    FirebaseAuth fAuth;
    ProgressBar progressBar;

    LinearLayout editTextArea;
    LinearLayout buttonArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_signup = findViewById(R.id.email_signup);
        fullName_signup = findViewById(R.id.fullName_signup);
        password_signup = findViewById(R.id.password_signup);
        passwordConfirm_signup = findViewById(R.id.passwordConfirm_signup);
        signupButton = findViewById(R.id.signupButton);
        loginPrompt = findViewById(R.id.loginPrompt);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.loading);

        editTextArea = findViewById(R.id.editTextArea);
        buttonArea = findViewById(R.id.buttonArea);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_signup.getText().toString().trim();
                String password = password_signup.getText().toString().trim();
                String rePassword = passwordConfirm_signup.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    email_signup.setError("Email không được để trống !");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    password_signup.setError("Mật khẩu không được để trống !");
                    return;
                }

                if(password.length() < 6) {
                    password_signup.setError("Mật khẩu phải nhiều hơn 5 kí tự !");
                    return;
                }

                if(TextUtils.isEmpty(rePassword)) {
                    passwordConfirm_signup.setError("Nhập lại mật khẩu !");
                    return;
                }

                if(!TextUtils.equals(password,rePassword)) {
                    passwordConfirm_signup.setError("Nhập lại mật khẩu không chính xác!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                editTextArea.setVisibility(View.INVISIBLE);
                buttonArea.setVisibility(View.INVISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } else {
                            Toast.makeText(SignUpActivity.this, "Thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            editTextArea.setVisibility(View.VISIBLE);
                            buttonArea.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });

        loginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
