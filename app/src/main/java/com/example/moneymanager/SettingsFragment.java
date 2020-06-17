package com.example.moneymanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button signOutButton;
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
        TextView name = getView().findViewById(R.id.email_settings);
        ImageView avatar = getView().findViewById(R.id.userAvatar);
        name.setText(user.getDisplayName());
        avatar.setClipToOutline(true);

        if(user.getPhotoUrl() != null) {
            String photoUrl = user.getPhotoUrl().toString();
            photoUrl = photoUrl + "?type=large";
            Picasso.get().load(photoUrl).into(avatar);
        }

        //Nút Sửa thông tin
        getView().findViewById(R.id.editInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EditUserInfoActivity.class);
                startActivity(intent);
            }
        });

        //NÚt đăng xuất
        getView().findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Taọ dialog, lấy layout ở file dialog_logout
                LayoutInflater factory = LayoutInflater.from(getContext());
                View DialogView = factory.inflate(R.layout.dialog_logout, null);
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

                alertDialog.getWindow().setLayout(900,450);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
    }
}

