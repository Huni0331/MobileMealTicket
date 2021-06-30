package com.example.foodbuy_ex2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodbuy_ex2.mainmenu.MainMenu;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class FoodBar extends AppCompatActivity {

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodbar);

        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan.setCameraId(1); //후방카메라=0, 전면카메라=1
        qrScan.setPrompt("<<QR 코드를 스캔하여 주세요>>");
        qrScan.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "맛점하세요", Toast.LENGTH_LONG).show();



                DatabaseReference mDBReference1 = FirebaseDatabase.getInstance().getReference().child("users");
                Map<String, Object> childUpdates1 = new HashMap<>();
                childUpdates1.put(""+result.getContents()+"/ticket/", ServerValue.increment(-1)); // 티켓 1감소
                mDBReference1.updateChildren(childUpdates1);


                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //딜레이 후 시작할 코드 작성
                        qrScan.initiateScan();
                    }
                }, 3000);// 3초 정도 딜레이를 준 후 시작

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
