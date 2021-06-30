package com.example.foodbuy_ex2.qrcontroll;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodbuy_ex2.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrCreateActivity extends AppCompatActivity {

    private ImageView iv_qrBigView;
    private String qr_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcreate);

        Intent intent = getIntent();
        qr_user_id = intent.getStringExtra("menu_userid");


        iv_qrBigView = findViewById(R.id.qrBigView);


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(qr_user_id, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            iv_qrBigView.setImageBitmap(bitmap);
        } catch ( Exception e ){}
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //딜레이 후 시작할 코드 작성
                finish();
            }
        }, 5000);// 5초 정도 딜레이를 준 후 시작
        Toast.makeText(this, "5초 후 화면이 종료됩니다.", Toast.LENGTH_LONG).show();

    }
}

