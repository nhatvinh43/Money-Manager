package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

public class EditUserInfoActivity extends AppCompatActivity {

    EditText fullNameET;
    EditText passWordET;
    EditText rePassWordET;
    ImageView avatarIV;
    ProgressBar loading;
    ScrollView container;
    Button changeAvatar;

    FirebaseAuth fAuth;
    FirebaseUser user;

    static int PReqCode = 1;
    static int gallaryRQCode = 1;
    Uri pickedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String colorScheme = getSharedPreferences("MyPreferences", MODE_PRIVATE).getString("currentColor", "yellow");

        switch (colorScheme)
        {
            case "yellow":
            {
                getTheme().applyStyle(R.style.AppTheme,true);
                break;
            }

            case "blue":
            {
                getTheme().applyStyle(R.style.AppThemeBlue,true);
                break;
            }

            case "red":
            {
                getTheme().applyStyle(R.style.AppThemeRed,true);
                break;
            }

            case "green":
            {
                getTheme().applyStyle(R.style.AppThemeGreen,true);
                break;
            }

            case "purple":
            {
                getTheme().applyStyle(R.style.AppThemePurple,true);
                break;
            }
        }

        setContentView(R.layout.activity_edit_user_info);

        user = fAuth.getInstance().getCurrentUser();

        fullNameET = findViewById(R.id.fullName_editUserInfo);
        passWordET = findViewById(R.id.newPassword_editUserInfo);
        rePassWordET = findViewById(R.id.newPasswordConfirm_editUserInfo);
        avatarIV = findViewById(R.id.userAvatar);
        loading = findViewById(R.id.loading);
        container = findViewById(R.id.editInfoContainer);
        changeAvatar = findViewById(R.id.changeAvatar);

        fullNameET.setText(user.getDisplayName());
        avatarIV.setClipToOutline(true);
        if(user.getPhotoUrl() != null) {
            String photoUrl = user.getPhotoUrl().toString();
//            photoUrl = photoUrl + "?type=large";
            Glide.with(getApplicationContext()).load(photoUrl).into(avatarIV);
        }

        UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1);
        if (userInfo.getProviderId().equals("google.com") || userInfo.getProviderId().equals("facebook.com")) {
            findViewById(R.id.newPasswordTV).setVisibility(View.GONE);
            passWordET.setVisibility(View.GONE);
            findViewById(R.id.reNewPasswordTV).setVisibility(View.GONE);
            rePassWordET.setVisibility(View.GONE);
        }

        //Nút back
        findViewById(R.id.backButton_editUserInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Nút đổi avatar
        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 22) {
                    checkRequestPermission();
                }
                else {
                    openGallery();
                }
            }
        });

        // Nút lưu
        findViewById(R.id.save_editUserInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = fullNameET.getText().toString().trim();
                final String passWord = passWordET.getText().toString().trim();
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
                        message.setText("Nhập lại mật khẩu sai!");
                        return;
                    }

                    //Tạo dialog nhập mật khẩu cũ
                    AlertDialog.Builder opBuilder = new AlertDialog.Builder(v.getContext());
                    opBuilder.setView(R.layout.dialog_fill_oldpassword);
                    final AlertDialog opAlertDialog = opBuilder.create();
                    opAlertDialog.show();
                    opAlertDialog.getWindow().setLayout(850,700);
                    opAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    final EditText oldPasswordET = opAlertDialog.findViewById(R.id.oldPassword);
                    opAlertDialog.findViewById(R.id.cancel_two_button_dialog).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            opAlertDialog.dismiss();
                        }
                    });
                    opAlertDialog.findViewById(R.id.confirm_two_button_dialog).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            opAlertDialog.dismiss();

                            // Đăng nhập lại
                            loading.setVisibility(View.VISIBLE);
                            container.setVisibility(View.INVISIBLE);
                            changeAvatar.setVisibility(View.INVISIBLE);

                            AuthCredential credentials = EmailAuthProvider.getCredential(user.getEmail(), oldPasswordET.getText().toString().trim());
                            user.reauthenticate(credentials).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    if(task1.isSuccessful()) {

                                        // Cập nhập password
                                        user.updatePassword(passWord).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task2) {
                                                if(task2.isSuccessful()) {

                                                    if(pickedImage != null) {
                                                        new DataHelper().uploadAvatar(new UserAvatarCallBack() {
                                                            @Override
                                                            public void onCallBack(Uri avatarUri) {
                                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                        .setDisplayName(fullName)
                                                                        .setPhotoUri(avatarUri)
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
                                                            }

                                                            @Override
                                                            public void onCallBackFail(String message) {
                                                                Toast.makeText(EditUserInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }, pickedImage);

                                                    } else if(user.getDisplayName().compareTo(fullName) != 0) {
                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                .setDisplayName(fullName)
                                                                .build();

                                                        user.updateProfile(profileUpdates)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task3) {
                                                                        if (task3.isSuccessful()) {
                                                                            new DataHelper().setUsersCollection(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                                                                            Toast.makeText(EditUserInfoActivity.this, "Cập nhập thông tin thành công", Toast.LENGTH_SHORT).show();

                                                                            alertDialog.dismiss();
                                                                            setResult(Activity.RESULT_OK, new Intent());
                                                                            finish();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(EditUserInfoActivity.this, "Cập nhập mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                        alertDialog.dismiss();
                                                        finish();
                                                    }
                                                }
                                                else {
                                                    loading.setVisibility(View.GONE);
                                                    container.setVisibility(View.VISIBLE);
                                                    changeAvatar.setVisibility(View.VISIBLE);
                                                    Toast.makeText(EditUserInfoActivity.this , "Đổi mật khẩu thất bại" + task2.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        loading.setVisibility(View.GONE);
                                        container.setVisibility(View.VISIBLE);
                                        changeAvatar.setVisibility(View.VISIBLE);
                                        Toast.makeText(EditUserInfoActivity.this , "Đổi mật khẩu thất bại" + task1.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                } else {
                    // Cập nhập thông tin user
                    loading.setVisibility(View.VISIBLE);
                    container.setVisibility(View.INVISIBLE);
                    changeAvatar.setVisibility(View.INVISIBLE);

                    if(pickedImage != null) {
                        new DataHelper().uploadAvatar(new UserAvatarCallBack() {
                            @Override
                            public void onCallBack(Uri avatarUri) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)
                                        .setPhotoUri(avatarUri)
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
                            }

                            @Override
                            public void onCallBackFail(String message) {
                                Toast.makeText(EditUserInfoActivity.this , message, Toast.LENGTH_SHORT).show();
                            }
                        }, pickedImage);

                    } else if(user.getDisplayName().compareTo(fullName) != 0) {
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

                                        }
                                        alertDialog.dismiss();
                                        setResult(Activity.RESULT_OK, new Intent());
                                        finish();
                                    }
                                });
                    } else {
                        alertDialog.dismiss();
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    }
                }

            }
        });

        //Nhớ gọi dialog và set message như ở login và signup activity
    }

    private void checkRequestPermission() {
          if(ContextCompat.checkSelfPermission(EditUserInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
              if(ActivityCompat.shouldShowRequestPermissionRationale(EditUserInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                  Toast.makeText(EditUserInfoActivity.this, "Hãy cho phép mở bộ sưu tập của bạn", Toast.LENGTH_SHORT).show();
              }

              ActivityCompat.requestPermissions(EditUserInfoActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
          else {
              openGallery();
          }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, gallaryRQCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == gallaryRQCode && resultCode == RESULT_OK  && data != null) {
            pickedImage = data.getData();
            avatarIV.setImageURI(pickedImage);
        }
    }
}
