package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditUserInfoActivity extends AppCompatActivity {

    EditText fullNameET;
    EditText passWordET;
    EditText rePassWordET;
    ImageView avatarIV;
    ProgressBar loading;
    ScrollView container;

    FirebaseAuth fAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        user = fAuth.getInstance().getCurrentUser();

        fullNameET = findViewById(R.id.fullName_editUserInfo);
        passWordET = findViewById(R.id.newPassword_editUserInfo);
        rePassWordET = findViewById(R.id.newPasswordConfirm_editUserInfo);
        avatarIV = findViewById(R.id.userAvatar);
        loading = findViewById(R.id.loading);
        container = findViewById(R.id.editInfoContainer);

        fullNameET.setText(user.getDisplayName());

        //Nút back
        findViewById(R.id.backButton_editUserInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Nút lưu
        findViewById(R.id.save_editUserInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = fullNameET.getText().toString().trim();
                String passWord = passWordET.getText().toString().trim();
                String rePassWord = rePassWordET.getText().toString().trim();

                //Tạo dialog lỗi
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_one_button);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getWindow().setLayout(850,400);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView message = alertDialog.findViewById(R.id.message_one_button_dialog);
                alertDialog.findViewById(R.id.confirm_one_button_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.hide();

                if(TextUtils.isEmpty(fullName)) {
                    alertDialog.show();
                    message.setText("Tên không được bỏ trống!");
                    return;
                }

                if(!TextUtils.isEmpty(passWord)) {
                    if(passWord.compareTo(rePassWord) != 0) {
                        alertDialog.show();
                        message.setText("Nhập lại mật khẩu không trùng khớp!");
                        return;
                    }

                    loading.setVisibility(View.VISIBLE);
                    container.setVisibility(View.INVISIBLE);
                    // Cập nhập password (chưa hoàn thành xong - re-authentication)
                    user.updatePassword(passWord).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                if(user.getDisplayName().compareTo(fullName) != 0) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullName)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        new DataHelper().setUsersCollection(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                                                        Toast.makeText(EditUserInfoActivity.this, "Cập nhập thông tin thành công", Toast.LENGTH_SHORT).show();

                                                        alertDialog.dismiss();
                                                        setResult(Activity.RESULT_OK, new Intent());
                                                        finish();
                                                    }
                                                }
                                            });
                                } else {
                                    alertDialog.dismiss();
                                    finish();
                                }
                            }
                            else {
                                loading.setVisibility(View.GONE);
                                container.setVisibility(View.VISIBLE);
                                Toast.makeText(EditUserInfoActivity.this , "Đổi mật khẩu thất bại" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    // Cập nhập thông tin user
                    loading.setVisibility(View.VISIBLE);
                    container.setVisibility(View.INVISIBLE);

                    if(user.getDisplayName().compareTo(fullName) != 0) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            new DataHelper().setUsersCollection(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                                            Toast.makeText(EditUserInfoActivity.this, "Cập nhập thông tin thành công", Toast.LENGTH_SHORT).show();

                                            alertDialog.dismiss();
                                            setResult(Activity.RESULT_OK, new Intent());
                                            finish();
                                        }
                                    }
                                });
                    } else {
                        alertDialog.dismiss();
                        finish();
                    }
                }

            }
        });

        //Nhớ gọi dialog và set message như ở login và signup activity
    }
}
