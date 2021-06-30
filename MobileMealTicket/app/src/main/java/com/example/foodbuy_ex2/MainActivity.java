package com.example.foodbuy_ex2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foodbuy_ex2.mainmenu.MainMenu;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;


import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    private Button Btnlogin, Btnlogout, btn_foodbar;
    private TextView mobileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Btnlogin = findViewById(R.id.login);
        Btnlogout = findViewById(R.id.logout);
        btn_foodbar = findViewById(R.id.btn_foodbar);
        mobileTextView = findViewById(R.id.mobileTextView);



        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if(oAuthToken != null) {
                    //로그인이 되었을 때 처리해야할 일
                }
                if( throwable != null) {
                    //오류가 나왔을 떄 처리해야할 일
                }
                updateKakaoLoginUi();
                return null;
            }
        };

        btn_foodbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FoodBar.class);
                startActivity(intent);
            }
        });

        Btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)){
                    LoginClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);
                } else {
                    LoginClient.getInstance().loginWithKakaoAccount(MainActivity.this, callback);
                }

            }
        });

        Btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUi();
                        return null;
                    }
                });
            }
        });

        updateKakaoLoginUi();
    }

    private void updateKakaoLoginUi() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if ( user != null) {


                    String userId = Long.toString(user.getId());
                    String userNickName = user.getKakaoAccount().getProfile().getNickname();


                    Intent intent = new Intent(MainActivity.this, MainMenu.class);

                    Log.i("메인액티비티", "invoke : id = " + userId);
//                    Log.i("메인액티비티", "invoke : nickname = " + userNickName);

                    intent.putExtra("main_userid", userId);
                    intent.putExtra("main_usernickname", userNickName);
                    startActivity(intent);

                } else {
                    mobileTextView.setText("로그인 해주세요");
                }
                return  null;
            }
        });
    }
}