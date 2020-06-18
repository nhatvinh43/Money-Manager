package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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

                //Tạo dialog lỗi
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setView(R.layout.dialog_one_button);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getWindow().setLayout(850,400);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView message = alertDialog.findViewById(R.id.message_one_button_dialog);
                alertDialog.findViewById(R.id.confirm_one_button_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                    }
                });
                alertDialog.hide();

                if(TextUtils.isEmpty(email)) {
                    //email_signup.setError("Email không được để trống !");
                    alertDialog.show();
                    message.setText("Email không được để trống!");
                    return;
                }

                if(TextUtils.isEmpty(fullName_signup.getText().toString())) {
                    //fullName_signup.setError("Họ và tên không được để trống !");
                    alertDialog.show();
                    message.setText("Họ và tên không được để trống!");
                    return;
                }

                /*if(TextUtils.isEmpty(password)) {
                    password_signup.setError("Mật khẩu không được để trống !");
                    return;
                }*/

                if(password.length() < 6) {
                    //password_signup.setError("Mật khẩu phải nhiều hơn 5 kí tự !");
                    alertDialog.show();
                    message.setText("Mật khẩu phải nhiều hơn 5 kí tự!");
                    return;
                }

                if(TextUtils.isEmpty(rePassword)) {
                    //passwordConfirm_signup.setError("Nhập lại mật khẩu !");
                    alertDialog.show();
                    message.setText("Vui lòng nhập lại mật khẩu!");
                    return;
                }

                if(!TextUtils.equals(password,rePassword)) {
                    //passwordConfirm_signup.setError("Nhập lại mật khẩu không chính xác!");
                    alertDialog.show();
                    message.setText("Nhập lại mật khẩu không chính xác");
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
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName_signup.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                            }
                                        }
                                    });
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
