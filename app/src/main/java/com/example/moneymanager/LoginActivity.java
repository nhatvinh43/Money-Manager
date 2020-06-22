package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;


public class LoginActivity extends AppCompatActivity {
    EditText email_login;
    EditText password_login;
    Button loginButton;
    Button signupPrompt;
    TextView forgotPassword;

    FirebaseAuth fAuth;
    ProgressBar progressBar;

    //google login
    ImageButton ggButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;

    //facebook login
    ImageButton fbButton;
    CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_login = findViewById(R.id.email_login); //Login email
        password_login = findViewById(R.id.password_login); //Login password
        loginButton = findViewById(R.id.loginButton); // Login button
        signupPrompt = findViewById(R.id.signupPrompt); // Switch to sign up
        forgotPassword = findViewById(R.id.forgotPassword); //Forgot password

        ggButton = findViewById(R.id.ggLoginButton);
        fbButton = findViewById(R.id.fbLoginButton);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.loading);

        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // Email login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_login.getText().toString().trim();
                String password = password_login.getText().toString().trim();

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
                        alertDialog.dismiss();
                    }
                });
                alertDialog.hide();

                if(TextUtils.isEmpty(email)) {
                    /*email_login.setError("Email không được để trống !");
                    return;*/
                    alertDialog.show();
                    message.setText("Email không được để trống!");
                    return;
                }

                /*if(TextUtils.isEmpty(password)) {
                    password_login.setError("Mật khẩu không được để trống !");
                    return;
                }*/

                if(password.length() < 6) {
                    /*password_login.setError("Mật khẩu phải nhiều hơn 5 kí tự !");
                    return;*/
                    alertDialog.show();
                    message.setText("Mật khẩu phải nhiều hơn 5 kí tự!");
                    return;
                }

                Loading();

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            unLoading();
                        }
                    }
                });
            }
        });


        // Google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ggLoginIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(ggLoginIntent, RC_SIGN_IN);
            }
        });

        //facebook login
        //FacebookSdk.sdkInitialize(this.getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        Toast.makeText(LoginActivity.this, "Đăng nhập với Facebook thành công", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Đăng nhập với Facebook bị hủy", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(LoginActivity.this, "Có lỗi xãy ra !", Toast.LENGTH_LONG).show();
                    }
                });

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
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

    private void Loading() {
        progressBar.setVisibility(View.VISIBLE);
        email_login.setVisibility(View.INVISIBLE);
        password_login.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        signupPrompt.setVisibility(View.INVISIBLE);
        forgotPassword.setVisibility(View.INVISIBLE);
        ggButton.setVisibility(View.INVISIBLE);
        fbButton.setVisibility(View.INVISIBLE);
    }

    private void unLoading() {
        progressBar.setVisibility(View.GONE);
        email_login.setVisibility(View.VISIBLE);
        password_login.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        signupPrompt.setVisibility(View.VISIBLE);
        forgotPassword.setVisibility(View.VISIBLE);
        ggButton.setVisibility(View.VISIBLE);
        fbButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Loading();
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(LoginActivity.this, "Đăng nhập Google thành công", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
                unLoading();
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = fAuth.getCurrentUser();
                    Toast.makeText(LoginActivity.this, "Hello from google, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();

                    new DataHelper().setUsersCollection(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                    FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        if(!task.getResult().exists()) {
                                            new DataHelper().setMoneySource(new MoneySourceCallBack() {
                                                @Override
                                                public void onCallBack(ArrayList<MoneySource> list) {
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    finish();
                                                }

                                                @Override
                                                public void onCallBackFail(String message) {
                                                    unLoading();
                                                }
                                            }, user.getUid(), "Ví chung", 0.0, 0.0, "curId", "VND");
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        }
                                    }
                                }
                            });

                } else {
                    Toast.makeText(LoginActivity.this, "Thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    unLoading();
                }
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = fAuth.getCurrentUser();

                            Toast.makeText(LoginActivity.this, "Hello from facebook, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            new DataHelper().setUsersCollection(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                            FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()) {
                                                if(!task.getResult().exists()) {
                                                    new DataHelper().setMoneySource(new MoneySourceCallBack() {
                                                        @Override
                                                        public void onCallBack(ArrayList<MoneySource> list) {
                                                            unLoading();
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onCallBackFail(String message) {
                                                            unLoading();
                                                        }
                                                    }, user.getUid(), "Ví chung", 0.0, 0.0, "curId", "VND");
                                                } else {
                                                    unLoading();
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    finish();
                                                }
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(LoginActivity.this, "Thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            unLoading();
                        }
                    }
                });
    }

}
