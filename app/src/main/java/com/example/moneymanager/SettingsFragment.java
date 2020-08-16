package com.example.moneymanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int SETTING_RQCODE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView name;
    ImageView avatar;

    FirebaseAuth fAuth;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

        UserInfo user = fAuth.getCurrentUser();
        name = getView().findViewById(R.id.name_settings);
        avatar = getView().findViewById(R.id.userAvatar);

        name.setText(user.getDisplayName());

        avatar.setClipToOutline(true);
        if(user.getPhotoUrl() != null) {
            Log.d("------------------------------", user.getPhotoUrl().toString());
            String photoUrl = user.getPhotoUrl().toString();
//            photoUrl = photoUrl + "?type=large";
            Glide.with(getContext()).load(photoUrl).into(avatar);
        }

        //Nút Sửa thông tin
        getView().findViewById(R.id.editInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EditUserInfoActivity.class);
                startActivityForResult(intent, SETTING_RQCODE);
            }
        });

        //NÚt đăng xuất
        getView().findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Taọ dialog, lấy layout ở file dialog_logout_confirm
                LayoutInflater factory = LayoutInflater.from(getContext());
                View DialogView = factory.inflate(R.layout.dialog_logout_confirm, null);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                dialogBuilder.setView(DialogView);
                final AlertDialog alertDialog = dialogBuilder.create();

                DialogView.findViewById(R.id.confirmLogout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserInfo user = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1);
                        if (user.getProviderId().equals("google.com")) {
                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build();

                            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
                            mGoogleSignInClient.signOut();
                            Toast.makeText(getActivity(), "User use google",Toast.LENGTH_SHORT).show();
                        }
                        else if (user.getProviderId().equals("facebook.com")) {
                            LoginManager.getInstance().logOut();
                            Toast.makeText(getActivity(), "User use facebook",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "User use email",Toast.LENGTH_SHORT).show();
                        }

                        fAuth.getInstance().signOut();
                        // Xóa dữ liệu offline
                        SharedPreferences preferences = getActivity().getSharedPreferences("periodicTransactionList", MODE_PRIVATE);
                        preferences.edit().remove("list").commit();

                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                });

                DialogView.findViewById(R.id.cancelLogout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

                alertDialog.getWindow().setLayout(850,450);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });

        final RelativeLayout settingsPanel = view.findViewById(R.id.settingsPanel);
        final ProgressBar settingsProgressBar = view.findViewById(R.id.settingsProgressBar);

        //Nút định dạng ngày tháng
        view.findViewById(R.id.formatDateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_format_date);
                final AlertDialog formatDatePanel  = builder.create();
                formatDatePanel.show();
                formatDatePanel.getWindow().setLayout(1000,1200);
                formatDatePanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button ddmmyyyy = formatDatePanel.findViewById(R.id.ddmmyyyy_format_date);
                Button mmddyyyy = formatDatePanel.findViewById(R.id.mmddyyyy_format_date);
                Button yyyyddmm = formatDatePanel.findViewById(R.id.yyyyddmm_format_date);
                Button yyyymmdd = formatDatePanel.findViewById(R.id.yyyymmdd_format_date);



                ddmmyyyy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeDateFormat("dd/MM/yyyy", settingsProgressBar);
                    }
                });

                mmddyyyy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeDateFormat("MM/dd/yyyy", settingsProgressBar);
                    }
                });

                yyyyddmm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeDateFormat("yyyy/dd/MM", settingsProgressBar);
                    }
                });

                yyyymmdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeDateFormat("yyyy/MM/dd", settingsProgressBar);
                    }
                });

            }
        });

        //Nút đặt nhắc nhở
        view.findViewById(R.id.reminderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_reminder);
                final AlertDialog reminderPanel  = builder.create();
                reminderPanel.show();
                reminderPanel.getWindow().setLayout(1000,1200);
                reminderPanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            }
        });

        //Nút đổi màu sắc
        view.findViewById(R.id.colorButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_color);
                final AlertDialog colorChangePanel  = builder.create();
                colorChangePanel.show();
                colorChangePanel.getWindow().setLayout(1000,1200);
                colorChangePanel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                
                View yellow = colorChangePanel.findViewById(R.id.yellow_color);
                View blue = colorChangePanel.findViewById(R.id.blue_color);
                View red = colorChangePanel.findViewById(R.id.red_color);
                View green = colorChangePanel.findViewById(R.id.green_color);
                View purple = colorChangePanel.findViewById(R.id.purple_color);

                yellow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeColor("yellow", settingsProgressBar);
                    }
                });

                blue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeColor("blue", settingsProgressBar);
                    }
                });

                red.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeColor("red", settingsProgressBar);
                    }
                });

                green.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeColor("green", settingsProgressBar);
                    }
                });

                purple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeColor("purple", settingsProgressBar);
                    }
                });

            }
        });

        //Nút FAQ
        view.findViewById(R.id.helpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FAQActivity.class);
                startActivity(intent);
            }
        });

    }

    void changeDateFormat(String format, ProgressBar progressBar)
    {
        SharedPreferences.Editor editor = ((MainActivity)getActivity()).getSharedPreferences("MyPreferences", MODE_PRIVATE).edit();
        editor.putString("currentDate", format);
        editor.apply();

        progressBar.setVisibility(View.VISIBLE);

        Intent intent = ((MainActivity)getActivity()).getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ((MainActivity)getActivity()).finish();
        startActivity(intent);
    }

    void changeColor(String color, ProgressBar progressBar)
    {
        SharedPreferences.Editor editor = ((MainActivity)getActivity()).getSharedPreferences("MyPreferences", MODE_PRIVATE).edit();
        editor.putString("currentColor", color);
        editor.apply();

        Intent intent = ((MainActivity)getActivity()).getIntent();
        ((MainActivity)getActivity()).finish();
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UserInfo user = fAuth.getCurrentUser();

        if(requestCode == SETTING_RQCODE) {
            if(resultCode == Activity.RESULT_OK) {
                name.setText(user.getDisplayName());

                avatar.setClipToOutline(true);
                if(user.getPhotoUrl() != null) {
                    String photoUrl = user.getPhotoUrl().toString();
//                    photoUrl = photoUrl + "?type=large";
                    Glide.with(getContext()).load(photoUrl).into(avatar);
                }
            }
        }
    }
}

