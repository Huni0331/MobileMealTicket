package com.example.foodbuy_ex2.kakao;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        KakaoSdk.init(this, "{NATIVE_APP_KEY}");
    }
}
