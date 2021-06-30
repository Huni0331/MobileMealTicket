package com.example.foodbuy_ex2.mainmenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodbuy_ex2.R;
import com.example.foodbuy_ex2.UserData;
import com.example.foodbuy_ex2.qrcontroll.QrCreateActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainMenu extends AppCompatActivity {

    private ImageView iv_qrSmallView;
    private String user_nickname;
    private String user_id;
    private Button btn_buyTicket;
    private TextView countTicket;

    //식단표를 위한 변수들
    String strings = "";
    TextView todayfoodTextView;

    //database를 위한 변수들
    DatabaseReference mDBReference = null;
    HashMap<String, Object> childUpdates = null;
    Map<String, Object> userValue = null;
    UserData userInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);


        //식단표를 위한 변수선언
        todayfoodTextView = findViewById(R.id.todayfoodTextView);
        final Bundle bundle = new Bundle();

        btn_buyTicket = findViewById(R.id.buyTicket);
        countTicket = findViewById(R.id.countTicket);




        Intent intent = getIntent();
        user_nickname = intent.getStringExtra("main_usernickname");
        user_id = intent.getStringExtra("main_userid");




        iv_qrSmallView = findViewById(R.id.qrSamllView);



        newUser(user_id);
        ticket(user_id);

        btn_buyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onCountClicked(user_id);
            }
        });




        /*작은 qr코드생성------------------------------------------------
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(user_id, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            iv_qrSmallView.setImageBitmap(bitmap);
        } catch ( Exception e ){}

        iv_qrSmallView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), QrCreateActivity.class);
                intent1.putExtra("menu_userid", user_id);
                startActivity(intent1);
            }
        });
        작은 qr코드생성------------------------------------------------*/

        //식단표 생성
        new Thread(){
            @Override
            public void run() {
                Document doc = null;
                try {
//                    sslTrustAllCerts();

                    doc = Jsoup.connect("https://dorm.hanbat.ac.kr/").get();
                    Elements contents = doc.select("a.title h2");
                    strings += contents.text()+" - ";

                    contents = doc.select("a.title strong");
                    strings += contents.text() + "\n\n";


                    contents = doc.select("ul.list");

                    for(Element elem : contents) {
                        Element selem = elem.select("li").next().select("p.contents").first();
                        String str = selem.text();
                        strings += str.replace(',', '\n'); // 문자 , 를 줄바꿈으로 변환
                        Log.d("로그남기기2", ""+selem.text());
                    }

                    bundle.putString("strings", strings); //핸들러를 이용해서 Thread()에서 가져온 데이터를 메인 쓰레드에 보내준다.
                    Message msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    //식단표 핸들러
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            todayfoodTextView.setText(bundle.getString("strings"));                      //이런식으로 View를 메인 쓰레드에서 뿌려줘야한다.
        }
    };
    //식단표 핸들러


    private void newUser(String userId) { // 새로운 유저 초기화 작업
        Log.d("------newUser 함수가 발동", "------------------------------------");
        mDBReference = FirebaseDatabase.getInstance().getReference().child("users");

        mDBReference.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserData post = snapshot.getValue(UserData.class);
                        try {
//                            Log.d("try유저생성 뉴 유져1", ""+post.getUserId().equals(null));
                        } catch (Exception e) {
//                            Log.d("catch유저생성 뉴 유져1", "널 저패겠다");
                            UserData userData = new UserData(user_id, user_nickname);
                            Map<String, Object> childUpdates2 = new HashMap<>();
                            childUpdates2 = new HashMap<>();
                            userValue = userData.toMap();
                            childUpdates2.put(user_id, userValue);
                            mDBReference.updateChildren(childUpdates2);

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void ticket(String userId) {
        Log.d("------ticket 함수가 발동", "------------------------------------");
        mDBReference = FirebaseDatabase.getInstance().getReference().child("users");
        mDBReference.child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserData post = dataSnapshot.getValue(UserData.class);
                        try{
                            countTicket.setText("남은 식권 : "+ post.getUserticket());
//                            Log.d("남은식권로그 : ", ""+post.getUserticket());

                        }catch (Exception e){
                            countTicket.setText("남은 식권 : 0");
//                            Log.d("catch남은식권로그 : 0", "");
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        try{

                        }catch (Exception e){

                        }
                    }
                });
    }

    private void onCountClicked(String userId) {//ticket +1 업데이트 부분
        Log.d("onCountClicked 함수가 발동", "------------------------------------");
        Map<String, Object> childUpdates1 = new HashMap<>();
        childUpdates1.put(""+userId+"/ticket/", ServerValue.increment(1)); // 티켓 1증가
        mDBReference.updateChildren(childUpdates1);
    }




    //식단표 웹페이지 인증?보안문제 때문에 넣은것
    public void sslTrustAllCerts(){
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {

                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {

                    }
                }
        };
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom()); HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch(Exception e) { e.printStackTrace(); } }

}
////////////////////////////////////////////////////////////////
