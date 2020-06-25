package com.example.moneymanager;

import android.net.Uri;

public interface UserAvatarCallBack {
    void onCallBack(Uri avatarUri);
    void onCallBackFail(String message);
}
